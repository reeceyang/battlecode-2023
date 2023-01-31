package v4potbellypig;

import battlecode.common.*;

public class HQStrategy {
	static int selfidx = 0;
	static boolean enoughCarriers = false;
	static boolean needMana = true; // true = needs Mana

	static boolean congested = false;
	static boolean superCongested = false;
	static int launcherClustersMade = 0;

	// To track changes in resource amounts, via a jank moving average
	static final int N_TURNS_AVG = 40;
	static int[] AdIncomeRecord = new int[N_TURNS_AVG];
	static int[] MaIncomeRecord = new int[N_TURNS_AVG];
	static int AdPrev = 0;
	static int MaPrev = 0;
	static int AdSpent = 0;
	static int MaSpent = 0;

	// To track wells
	static MapLocation elixir_loc = null;
	static MapLocation[] known_wells = new MapLocation[10];
	static ResourceType[] known_welltypes = new ResourceType[10];

	// Control parameters
	static int starvation = 30; // if counts down to 0, change status to starving
	static int turns_since_anchor = 0; // if this counts above TURNS_PER_ANCHOR, can build an anchor again
	static int adMin = 50;
	static int manaMin = 60;
	static int AdIncomeTarget = 2;
	static int MaIncomeTarget = 5;
	static float mapCharacteristic = 1; // a characteristic dimension of the map, recalculated on turn 1. Is 1 when map is 60x60
	static final int congestionMax = 30;
	static final int congestionMax2 = 69;
	static final int ANCHOR_LIMIT = 2;
	static final int TURNS_PER_ANCHOR = 80;
	static final double ANCHOR_MAP_FRAC = 0.1; // the portion of the map to cover before building anchors
	static final int ELIXIR_START_TURN = 5000;
	static final int ELIXIR_RADIUS = 360;

	static boolean symmetryFound = false;

	static MapLocation buildTarget;

	/**
	 * Run a single turn for a Headquarters.
	 * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
	 */
	static void runHeadquarters(RobotController rc) throws GameActionException {
		// INITIALIZATION
		if (RobotPlayer.turnCount == 1) {
			Communication.addHeadquarter(rc);
			mapCharacteristic = (float) Math.sqrt(rc.getMapWidth() * rc.getMapHeight());
		} else if (RobotPlayer.turnCount == 2) {
			Communication.updateHeadquarterInfo(rc);
			selfidx = Communication.getIdxHQbyLocation(rc, rc.getLocation());
		}

		if (RobotPlayer.turnCount == 100) {
			for (int i = Communication.STARTING_WELL_IDX; i < GameConstants.SHARED_ARRAY_LENGTH; i++) {
				System.out.println(Communication.readWellLocation(rc, i));
			}
		}

		// TRAFFIC MANAGEMENT
		// Check number of ally robots every 10 turns
		if (RobotPlayer.turnCount % 10 == 0) {
			RobotInfo[] friends = rc.senseNearbyRobots(-1, rc.getTeam());
			congested = (friends.length > congestionMax);
			superCongested = (friends.length > congestionMax2);
		}

		if (congested) { rc.setIndicatorString("Congested"); }

		// RESOURCE MANAGEMENT
		// Estimate income
		int AdDelta = rc.getResourceAmount(ResourceType.ADAMANTIUM) - AdPrev;
		int MaDelta = rc.getResourceAmount(ResourceType.MANA) - MaPrev;
		AdIncomeRecord[rc.getRoundNum() % N_TURNS_AVG] = AdDelta + AdSpent;
		MaIncomeRecord[rc.getRoundNum() % N_TURNS_AVG] = MaDelta + MaSpent;
		int AdTotRecord = 0;
		int MaTotRecord = 0;
		for (int i = N_TURNS_AVG - 1; i >= 0; i--) {
			AdTotRecord += AdIncomeRecord[i];
			MaTotRecord += MaIncomeRecord[i];
		}
		AdPrev = rc.getResourceAmount(ResourceType.ADAMANTIUM);
		MaPrev = rc.getResourceAmount(ResourceType.MANA);
		enoughCarriers = MaTotRecord / N_TURNS_AVG > MaIncomeTarget && AdTotRecord / N_TURNS_AVG > AdIncomeTarget;

		// Decide which resource to prioritize
		needMana = true;

		for (int wellIdx = Communication.STARTING_WELL_IDX; wellIdx < GameConstants.SHARED_ARRAY_LENGTH; wellIdx ++) {
			if (rc.readSharedArray(wellIdx) != 0) rc.setIndicatorDot(Communication.readWellLocation(rc, wellIdx), 0, 255*(rc.getTeam()==Team.A ? 1 : 0), 0);
		}

		if (!symmetryFound && Communication.findSymmetry(rc) != 0) {
			System.out.println("Symmetry found: "+Communication.findSymmetry(rc));
			symmetryFound = true;
		}
		// CONSTRUCTION MANAGEMENT
		if (rc.getRoundNum() < 100) {
			buildTarget = rc.getLocation();
		} else {
			buildTarget = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
		}
		MapLocation[] buildableLocations = getFreeLocations(rc, buildTarget);
		buildStuff(rc, buildableLocations);

		// ATTACK/DEFENSE MANAGEMENT
		// TODO:
		RobotInfo[] robots = rc.senseNearbyRobots();
		Pathing.reportAndPlaySafe(rc, robots, 0);

		// COMMUNICATION
		int stateInt = Communication.bitPackHQInfo(rc, needMana, false, false, congested);
		if (rc.canWriteSharedArray(selfidx, stateInt)) {
			rc.writeSharedArray(selfidx, stateInt);
		}
		
		// Bait launchers out into the world
		if (RobotPlayer.turnCount < 200 && RobotPlayer.turnCount % 50 == 0 || RobotPlayer.isSmallMap && RobotPlayer.turnCount == 10) {
			baitFriendlyLaunchers2(rc);
		}
				
		Communication.tryWriteMessages(rc);

		rc.setIndicatorString(String.format("AdIncome: %f, MaIncome: %f\"", (float) AdTotRecord / N_TURNS_AVG, (float) MaTotRecord / N_TURNS_AVG));
	}

	static MapLocation[] getFreeLocations(RobotController rc, MapLocation target) throws GameActionException {
		// returns up to (may include null) 5 empty, non-impassable locations, preferring locations in the general direction of target
		MapLocation[] accessible = rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), rc.getType().actionRadiusSquared);
		MapLocation[] results = new MapLocation[] {null, null, null, null, null};
		int[] distances = new int[] {3600, 3600, 3600, 3600, 3600};
		for (MapLocation loc : accessible) {
			if (rc.canSenseLocation(loc)) {
				// get distances to target if the square is empty and passable
				if (!rc.canSenseRobotAtLocation(loc) && rc.sensePassability(loc)) {
					int thisdist = loc.distanceSquaredTo(target);
					for (int i = 0; i < 5; i++) {
						if (thisdist < distances[i]) {
							results[i] = loc;
							distances[i] = thisdist;
							break;
						}
					}
				}
			}
		}
		return results;
	}

	static void buildStuff(RobotController rc, MapLocation[] newLocs) throws GameActionException {
		AdSpent = 0;
		MaSpent = 0;
		turns_since_anchor++;
		boolean launcherCluster = (rc.getResourceAmount(ResourceType.MANA) > 179) || (RobotPlayer.turnCount < 2 && !RobotPlayer.isSmallMap); // whether we can make 4+ launchers
		boolean saveForAnchor = rc.getNumAnchors(Anchor.STANDARD) < ANCHOR_LIMIT && (turns_since_anchor > TURNS_PER_ANCHOR)
				&& (RobotPlayer.turnCount > 1000 || rc.getRobotCount() > rc.getMapWidth() * rc.getMapHeight() * ANCHOR_MAP_FRAC);
		for (MapLocation newLoc : newLocs) {
			if (newLoc == null) {
				continue;
			}

			// If we've made launchers, make one amplifier
			if (launcherClustersMade > 2) {
				if (rc.canBuildRobot(RobotType.AMPLIFIER, newLoc)) {
					rc.buildRobot(RobotType.AMPLIFIER, newLoc);
					AdSpent += 30;
					MaSpent += 15;
					launcherClustersMade = 0;
					break;
				}
			}

			// If we can, build a cluster of launchers
			if (launcherCluster && !superCongested) {
				if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
					rc.buildRobot(RobotType.LAUNCHER, newLoc);
					MaSpent += 45;
					continue;
				}
			}

			// If we can, make a destabilizer
			if (rc.canBuildRobot(RobotType.DESTABILIZER, newLoc)) {
				rc.buildRobot(RobotType.DESTABILIZER, newLoc);
			}

			// Otherwise, build anchor after early game if resource rich
			if (saveForAnchor && rc.canBuildAnchor(Anchor.STANDARD)) {
				rc.buildAnchor(Anchor.STANDARD);
				AdSpent += 80;
				MaSpent += 80;
				saveForAnchor = false;
				turns_since_anchor = 0;
				System.out.println("built an anchor");
			}

			// Otherwise, build carriers continuously if starving and not superCongested
			if (!enoughCarriers && !congested && !saveForAnchor) {
				if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
					rc.buildRobot(RobotType.CARRIER, newLoc);
					AdSpent += 50;
				}
			}

		}
		if (launcherCluster) { launcherClustersMade += 1; }
	}

	static void checkAllKnownWells(RobotController rc) throws GameActionException {
		for (int j = Communication.STARTING_WELL_IDX; j < GameConstants.SHARED_ARRAY_LENGTH; j++) {
			if (rc.readSharedArray(j) != 0) {
				known_wells[j - Communication.STARTING_WELL_IDX] = Communication.readWellLocation(rc, j);
				known_wells[j+5 - Communication.STARTING_WELL_IDX] = Communication.getSymLoc(rc, known_wells[j - Communication.STARTING_WELL_IDX]);
				known_welltypes[j - Communication.STARTING_WELL_IDX] = Communication.getWellType(rc, j);
				known_welltypes[j+5 - Communication.STARTING_WELL_IDX] = known_welltypes[j - Communication.STARTING_WELL_IDX];
			}
		}
	}
	
	static void baitFriendlyLaunchers(RobotController rc) throws GameActionException {
		// Places "enemies" on map for launchers in attackMode to rally towards.
		MapLocation center = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
		int distance = (int) Math.min(Math.ceil(mapCharacteristic / 2), 15);
		Direction dir = rc.getLocation().directionTo(center);
		MapLocation target1 = RobotPlayer.shiftByAmount(rc, rc.getLocation(), dir, distance);
		MapLocation target2 = RobotPlayer.shiftByAmount(rc, rc.getLocation(), dir.rotateLeft(), distance);
		MapLocation target3 = RobotPlayer.shiftByAmount(rc, rc.getLocation(), dir.rotateRight(), distance);
		if (rc.onTheMap(target1)) { Communication.reportEnemy(rc, target1); }
		if (rc.onTheMap(target3)) { Communication.reportEnemy(rc, target3); }
		if (rc.onTheMap(target2)) { Communication.reportEnemy(rc, target2); }	
	}

	static void baitFriendlyLaunchers2(RobotController rc) throws GameActionException {
		if (Communication.findSymmetry(rc) == 0) {
			MapLocation target1 = Communication.intToLocation(rc, RobotPlayer.xReflect(rc, rc.getLocation()));
			MapLocation target2 = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, rc.getLocation()));
			MapLocation target3 = Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, rc.getLocation()));
			if (Communication.readHQStatus(rc, "nx") == 0) Communication.reportEnemy(rc, target1);
			if (Communication.readHQStatus(rc, "ny") == 0) Communication.reportEnemy(rc, target2);
			if (Communication.readHQStatus(rc, "nr") == 0) Communication.reportEnemy(rc, target3);
		} else {
			Communication.reportEnemy(rc, Communication.getSymLoc(rc, rc.getLocation()));
		}
	}
}
