package v3beardedreedling_1;

import static v3beardedreedling_1.Communication.headquarterLocs;

import battlecode.common.*;

public class HQStrategy {
	static int selfidx = 0;
	static boolean starving = true;
	static boolean needMana = false; // true = needs Mana

	static boolean congested = false;
	static boolean superCongested = false;
	static int launcherClustersMade = 0;

	// To track changes in resource amounts
	static int Ad = 200;
	static int Ma = 0;

	// Control parameters
	static int starvation = 30; // if counts down to 0, change status to starving
	static int turns_since_anchor = 0; // if this counts above TURNS_PER_ANCHOR, can build an anchor again
	static int adTarget = 50;
	static int manaTarget = 60;
	static float mapCharacteristic = 1; // a characteristic dimension of the map, recalculated on turn 1. Is 1 when map is 60x60
	static float AdToMana = 1; // updated on turn 1, sets which resource to prioritize
	static final int congestionMax = 30;
	static final int congestionMax2 = 69;
	static final int ANCHOR_LIMIT = 2;
	static final int TURNS_PER_ANCHOR = 80;
	static final double ANCHOR_MAP_FRAC = 0.1; // the portion of the map to cover before building anchors
	
	static MapLocation buildTarget;

	/**
	 * Run a single turn for a Headquarters.
	 * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
	 */
	static void runHeadquarters(RobotController rc) throws GameActionException {
		// INITIALIZATION
		if (RobotPlayer.turnCount == 1) {
			Communication.addHeadquarter(rc);
			mapCharacteristic = (float) Math.sqrt(rc.getMapWidth() * rc.getMapHeight()) / 120; // max map size; bigger map -> more Ad
			AdToMana = (float) (mapCharacteristic + 0.3);
		} else if (RobotPlayer.turnCount == 2) {
			Communication.updateHeadquarterInfo(rc);
			selfidx = Communication.getIdxHQbyLocation(rc, rc.getLocation());
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
		// Check for inventory change and adjust starvation counter
		int AdNew = rc.getResourceAmount(ResourceType.ADAMANTIUM);
		int MaNew = rc.getResourceAmount(ResourceType.MANA);
		if (AdNew > adTarget && MaNew > manaTarget) {
			// we gained Ad and Ma
			starvation = 30;
			starving = false;
		} else {
			// uh oh, resources are not coming in
			starvation -= 1;
			if (starvation < 1) {
				starving = true;
				//rc.setIndicatorString("Starving");
			}
		}
		Ad = AdNew;
		Ma = MaNew;
		// Decide which resource to prioritize
		if ((float) AdNew / (float) MaNew > AdToMana) {
			// we have too much Ad
			needMana = true;
			//rc.setIndicatorString("Asking for mana");
		} else {
			needMana = false;
			//rc.setIndicatorString("Asking for adamantium");
		}

		// CONSTRUCTION MANAGEMENT
		if (starving) {
			buildTarget = rc.getLocation();
		} else { buildTarget = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2); }
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
		Communication.tryWriteMessages(rc);
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
		turns_since_anchor++;
		boolean launcherCluster = (rc.getResourceAmount(ResourceType.MANA) > (RobotPlayer.isSmallMap ? 239 : 179)) || (RobotPlayer.turnCount < 2 && !RobotPlayer.isSmallMap); // whether we can make 4+ launchers
		boolean saveForAnchor = (turns_since_anchor > TURNS_PER_ANCHOR)
				&& (RobotPlayer.turnCount > 1000 || rc.getRobotCount() > rc.getMapWidth() * rc.getMapHeight() * ANCHOR_MAP_FRAC);			
		for (MapLocation newLoc : newLocs) {
			if (newLoc == null) {
				continue;
			}
			
			// If we've made launchers, make one amplifier
			if (launcherClustersMade > 2) {
				if (rc.canBuildRobot(RobotType.AMPLIFIER, newLoc)) {
					rc.buildRobot(RobotType.AMPLIFIER, newLoc);
					launcherClustersMade = 0;
					break;
				}
			}
			
			// If we can, build a cluster of launchers
			if (launcherCluster && !superCongested) {
				if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
					rc.buildRobot(RobotType.LAUNCHER, newLoc);
					continue;
				}
			}

			// Otherwise, build anchor after early game if resource rich
			if (saveForAnchor && rc.canBuildAnchor(Anchor.STANDARD)) {
				rc.buildAnchor(Anchor.STANDARD);
				saveForAnchor = false;
				turns_since_anchor = 0;
				System.out.println("built an anchor");
			}

			// Otherwise, build carriers continuously if starving and not superCongested
			if (!congested && !saveForAnchor) {
				if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
					rc.buildRobot(RobotType.CARRIER, newLoc);
				}
			}

		}
		if (launcherCluster) { launcherClustersMade += 1; }
	}
}
