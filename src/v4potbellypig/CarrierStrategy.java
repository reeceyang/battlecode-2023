package v4potbellypig;

import battlecode.common.*;

public class CarrierStrategy {

    enum CarrierState {
        DEFAULT,
        MAKE_ELIXIR,
        SEARCH,
        REPORT,
        DANGER,
        ANCHOR;
    }

    static final int WELL_CONGESTION_MAX = 8;
    static final int ATTACK_HEALTH_THRESH = 51;

    static MapLocation hqLoc;
    static int hqIdx = -1; // index into shared array
    static MapLocation islandLoc;

    static MapLocation nextLoc;
    static CarrierState state = CarrierState.DEFAULT;
    static boolean bugOverride = false;
    static final int BUG_OVERRIDE_THRESH = 20;

    static MapLocation wellLoc;
    static MapLocation adWellLoc;
    static int elixirTargetIdx = -1;
    static ResourceType elixirTargetNeed;
    static boolean foundElixir = false;
    static MapLocation[] wellLocs = new MapLocation[]{null, null, null, null, null};
    static ResourceType[] wellTypes = new ResourceType[]{null, null, null, null, null};
    static final int WELLLOC_MAX_IDX = 4; // change this if length of wellLocs is changed
	static int currentIslandId = -1; // the id of the island currently being targeted
    static boolean keepExploring = false;
    /**
     * Run a single turn for a Carrier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runCarrier(RobotController rc) throws GameActionException {
        // INITIALIZATION
        state = CarrierState.DEFAULT;

        if (hqLoc == null) scanHQ(rc);
        else if (hqIdx == -1) {
            hqIdx = Communication.getIdxHQbyLocation(rc, hqLoc);
            rc.setIndicatorString("Checking HQ");
        }

        // Check if HQ wants an elixir well made, if elixir destined
//        if (rc.getID() % 3 == 0 && rc.getRoundNum() > HQStrategy.ELIXIR_START_TURN) {
//            elixirTargetIdx = Communication.getElixirTarget(rc);
//            if (elixirTargetIdx != -1 && !foundElixir) {
//                state = CarrierState.MAKE_ELIXIR;
//                // check needed resource type
//                switch (Communication.getWellType(rc, elixirTargetIdx)) {
//                    case ADAMANTIUM:
//                        elixirTargetNeed = ResourceType.MANA;
//                        break;
//                    case MANA:
//                        elixirTargetNeed = ResourceType.ADAMANTIUM;
//                        break;
//                    case ELIXIR:
//                        state = CarrierState.DEFAULT;
//                        // the reset of the wellType occurs in the action switch statement
//                        break;
//                    case NO_RESOURCE:
//                        state = CarrierState.DEFAULT;
//                        break;
//                }
//            }
//        }

        // Scan wells, update local well cache, scan islands, and set wellLoc
        if (state == CarrierState.MAKE_ELIXIR) {
            scanWellsSelective(rc, elixirTargetNeed);
        } else {
            scanWellsSelective(rc, RobotPlayer.demanded);
        }
        if (wellLoc == null) {
            state = CarrierState.SEARCH;
        }
        islandLoc = null;
        scanIslands(rc, rc.getTeam(), keepExploring);

        // MINING AND SCOUTING
        // Transfer resource to headquarters
        depositResource(rc, ResourceType.ADAMANTIUM, hqLoc);
        depositResource(rc, ResourceType.MANA, hqLoc);

        // If novel well, turn on reporting mode (sends carrier back to HQ to report finding)
        for (int i = WELLLOC_MAX_IDX; i >= 0; i--) {
            if (wellLocs[i] != null && !Communication.isWellWritten(rc, wellLocs[i])) { // if any location is not written, turn on reportMode to go back to hq
                if (rc.readSharedArray(63) == 0) {
                    state = CarrierState.REPORT;
                } else {
                    wellLoc = new MapLocation(wellLocs[i].x, wellLocs[i].y);
                }
                break;
            }
        }

        // Push to global well cache and reset local cache
        // TODO: Have some sort of calculation to determine which wells to add
        if (rc.canWriteSharedArray(0, 0)) {
            for (int j = WELLLOC_MAX_IDX; j >= 0; j--) {
                if (wellLocs[j] != null) {
                    Communication.addWell(rc, wellTypes[j], wellLocs[j]);
//                    System.out.println("Added well at "+wellLocs[j]+" to shared array");
                }
            }
            wellLocs = new MapLocation[]{null, null, null, null, null};
            wellTypes = new ResourceType[]{null, null, null, null, null};
        }

        // ENEMIES
        int radiusAct = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] robots = rc.senseNearbyRobots();

        int lowestHealth = 1000;
        int smallestDistance = 100;
        RobotInfo target = null;
        if (robots.length > 0) {
            for (RobotInfo robot : robots) {
                if (robot.getTeam() == opponent) {
                    int enemyHealth = robot.getHealth();
                    int enemyDistance = robot.getLocation().distanceSquaredTo(rc.getLocation());
                    if (enemyHealth < lowestHealth && enemyDistance < radiusAct) {
                        target = robot;
                        lowestHealth = enemyHealth;
                        smallestDistance = enemyDistance;
                    } else if (enemyHealth == lowestHealth) {
                        if (enemyDistance < smallestDistance) {
                            target = robot;
                            smallestDistance = enemyDistance;
                        }
                    }
                    state = CarrierState.DANGER;
                }
            }
            if (robots.length > BUG_OVERRIDE_THRESH) {
                bugOverride = true;
            } else {
                bugOverride = false;
            }
        }
        Communication.clearObsoleteEnemies(rc);
        Communication.tryWriteMessages(rc);

        if (islandLoc != null && rc.canTakeAnchor(hqLoc, Anchor.STANDARD)) {
            System.out.println("took the anchor");
            rc.takeAnchor(hqLoc, Anchor.STANDARD);
        }

        if (rc.getAnchor() != null) {
            state = CarrierState.ANCHOR;
        }
        int total = getTotalResources(rc);
        boolean collectedResource = false;
        if (wellLoc != null && rc.canCollectResource(wellLoc, -1)) {
            rc.collectResource(wellLoc, -1);
            collectedResource = true;
        }
        // MODE-BASED ACTIONS
        switch (state) {
            case DEFAULT:
                if (total < 39) {
                    //move towards well or search for well
                    if (wellLoc == null) {
						nextLoc = null;
					} else if (!rc.getLocation().isAdjacentTo(wellLoc)) {
						nextLoc = wellLoc;
					} else if (rc.getLocation().equals(wellLoc)) {
						nextLoc = nextWellLoc(rc, wellLoc);
					}
                } else if (total > 38) {
                    //move towards HQ
                    nextLoc = hqLoc;
                }
                break;
//            case MAKE_ELIXIR:
//                if (wellLoc != null && rc.canCollectResource(wellLoc, -1)) {
//                    rc.collectResource(wellLoc, -1);
//                }
//                if (total < 39) {
//                    //move towards well or search for well
//                    if (wellLoc == null) nextLoc = null;
//                    else if (!rc.getLocation().isAdjacentTo(wellLoc)) nextLoc = wellLoc;
//                } else if (total > 38) {
//                    MapLocation targetLoc = Communication.readWellLocation(rc, elixirTargetIdx);
//                    nextLoc = targetLoc;
//                    // check if the well is converted already
//                    if (rc.canSenseLocation(targetLoc)) {
//                        if (rc.senseWell(targetLoc).getResourceType() == ResourceType.ELIXIR) {
//                            Communication.unsetElixirTargetStatus(rc, elixirTargetIdx);
//                            foundElixir = true;
//                        } else {
//                            // deposit resources into well
//                            depositResource(rc, ResourceType.MANA, targetLoc);
//                            depositResource(rc, ResourceType.ADAMANTIUM, targetLoc);
//                        }
//                    }
//                }
//                break;
            case SEARCH:
                if (adWellLoc != null) {
                    adWellLoc = Communication.getNearestWellOfType(rc, ResourceType.ADAMANTIUM);
                }
                nextLoc = Pathing.findManaWell(rc, adWellLoc, hqLoc);
                break;
            case REPORT:
                nextLoc = hqLoc;
                break;
            case DANGER:
                if (rc.getHealth() < ATTACK_HEALTH_THRESH) {
                    if (target != null) {
                        if (rc.canAttack(target.getLocation()))
                            rc.attack(target.getLocation());
                        rc.setIndicatorString("AHHHHH");
                    }
                }
                nextLoc = Pathing.reportAndPlaySafe(rc, robots, 2);
                break;
            case ANCHOR:
                rc.setIndicatorDot(rc.getLocation(), 1, 0, 0);
                nextLoc = islandLoc;
                // check if the island it target was already taken
                rc.setIndicatorString("currentislandid" + currentIslandId + " " + islandLoc);
                if (!keepExploring && (islandLoc == null
                        || (rc.canSenseLocation(islandLoc) && rc.senseTeamOccupyingIsland(currentIslandId) == rc.getTeam()))) {
                    keepExploring = true;
                }
                if (rc.canPlaceAnchor() && rc.senseTeamOccupyingIsland(rc.senseIsland(rc.getLocation())) == Team.NEUTRAL) {
                    rc.placeAnchor();
                    state = CarrierState.DEFAULT;
                    keepExploring = false;
                } else if (keepExploring) {
//                    rc.setIndicatorString("keep exploring" + islandLoc);
					Direction dir = hqLoc.directionTo(rc.getLocation());
					Direction dir1 = dir.rotateLeft();
					Direction dir2 = dir.rotateRight();
					switch (rc.getID() % 3) {
						case 0: nextLoc = rc.getLocation().add(dir); break;
						case 1: nextLoc = rc.getLocation().add(dir1); break;
						case 2: nextLoc = rc.getLocation().add(dir2); break;
					}
//					if (rc.canSenseLocation(islandLoc) && rc.senseTeamOccupyingIsland(currentIslandId) == rc.getTeam()) {
//						rc.setIndicatorString("condition 1");
//					}
					if (rc.canSenseLocation(nextLoc) && rc.senseIsland(nextLoc) == -1 || !rc.onTheMap(nextLoc)) {
						rc.setIndicatorString("condition 2");
						if (!rc.onTheMap(nextLoc)) {
							switch (rc.getID() % 3) {
								case 0: nextLoc = nextLoc.add(dir1).add(dir1); break;
								case 1: nextLoc = nextLoc.add(dir2); break;
								case 2: nextLoc = nextLoc.add(dir1); break;
							}
						}
					}
					if (islandLoc != null) {
						nextLoc = islandLoc;
					}
				}
                rc.setIndicatorString("going to island " + nextLoc);
				if (rc.getHealth() < 31) rc.setIndicatorString("health low");

				if ((islandLoc == null || rc.getLocation().distanceSquaredTo(islandLoc) > 20) && rc.getHealth() < 31) {
					rc.setIndicatorString("trying to attacc");
					if (rc.canAttack(target.getLocation())) rc.attack(target.getLocation());
					rc.setIndicatorString("attacc");
				}
                break;
        }

        boolean shouldMoveTwice = (state == CarrierState.DEFAULT && !collectedResource) || state == CarrierState.REPORT || state == CarrierState.SEARCH || state == CarrierState.DANGER;
        // PERFORM MOVEMENT
        if (nextLoc == null) {
            RobotPlayer.moveRandom(rc);
        } else {
            Pathing.moveTowards(rc, nextLoc, bugOverride, shouldMoveTwice);
            rc.setIndicatorLine(rc.getLocation(), nextLoc, 0, 0, 1);
        }

    }


    /* --------------------------------------------- */

    static void scanHQ(RobotController rc) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for (RobotInfo robot : robots) {
            if (robot.getTeam() == rc.getTeam() && robot.getType() == RobotType.HEADQUARTERS) {
                hqLoc = robot.getLocation();
                break;
            }
        }
    }

    static void scanWellsSelective(RobotController rc, ResourceType demanded) throws GameActionException {
        // Select wells for matching resource, but prioritize ones which are unoccupied
        WellInfo[] wells = rc.senseNearbyWells();
        RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam());
        MapLocation best = null;
        int closestDistSq = 1000;
        if (wells.length > 0) {
            for (WellInfo well : wells) {
                // Add to local well cache
                if (!Communication.isWellWritten(rc, well.getMapLocation())) {
                    for (int i = 0; i < wellLocs.length; i++) {
                        if (wellLocs[i] == null) {
                            wellLocs[i] = well.getMapLocation();
                            wellTypes[i] = well.getResourceType();
                            break;
                        }
                    }
                }
                // Find at least one Ad well to use for Kai's circle
                if (adWellLoc == null && well.getResourceType() == ResourceType.ADAMANTIUM) {
                    adWellLoc = well.getMapLocation();
                }
                // Do selection
                if (well.getResourceType() == ResourceType.ELIXIR || well.getResourceType() == demanded) {
                    int numberAdjacent = 0;
                    MapLocation thisWellLoc = well.getMapLocation();
                    for (RobotInfo robot : robots) {
                        if (robot.getLocation().isAdjacentTo(thisWellLoc)) {
                            numberAdjacent += 1;
                        }
                    }
                    if (numberAdjacent < WELL_CONGESTION_MAX) {
                        best = thisWellLoc;
                        break;
                    }
                    int d2 = rc.getLocation().distanceSquaredTo(thisWellLoc);
                    if (d2 < closestDistSq) {
                        closestDistSq = d2;
                        best = thisWellLoc;
                    }
                }
            }
        }
        wellLoc = best;
        // If none found, refer to communications
        if (wellLoc == null) {
            wellLoc = Communication.getNearestWellOfType(rc, demanded);
        }
    }

    static void depositResource(RobotController rc, ResourceType type, MapLocation target) throws GameActionException {
        int amount = rc.getResourceAmount(type);
        if (amount > 0) {
            if (rc.canTransferResource(target, type, amount)) {
                rc.transferResource(target, type, amount);
            }
        }
    }

    static int getTotalResources(RobotController rc) {
        return rc.getResourceAmount(ResourceType.ADAMANTIUM)
                + rc.getResourceAmount(ResourceType.MANA)
                + rc.getResourceAmount(ResourceType.ELIXIR);
    }

    static MapLocation nextWellLoc(RobotController rc, MapLocation wellLoc) throws GameActionException {
        // Rules:
        // 1. If there is a carrier at the well, go adjacent to the well
        // 2. If there is no carrier at the well, go to the well
        // 3. If you are at the well and have >30 resource, try to move adjacent to the well

        boolean adjacentPositionOpen = false;

        MapLocation[] adjacentPositions = new MapLocation[]{null, null, null, null, null, null, null, null};
        int index = 0;

        for (Direction d : RobotPlayer.directions) {
            if (!rc.isLocationOccupied(wellLoc.add(d)) && rc.sensePassability(wellLoc.add(d))) {
                adjacentPositions[index] = wellLoc.add(d);
                index++;
            }
        }

//        int closest = 3600;
//        int closest_idx = 0;
//        int closest_temp = 0;
//
//        if (adjacentPositions[0] != null) adjacentPositionOpen = true;
//
//        if (adjacentPositionOpen) {
//            for (MapLocation position : adjacentPositions) {
//                int dist = 0;
//                if (position != null) {
//                    dist = rc.getLocation().distanceSquaredTo(position);
//                }
//                if (dist < closest) {
//                    closest = dist;
//                    closest_idx = closest_temp;
//                }
//                closest_temp++;
//            }
//        }

//        if (rc.getLocation().isAdjacentTo(wellLoc) && rc.canSenseRobotAtLocation(wellLoc)) {
//            return rc.getLocation();
//        }

        if (rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ADAMANTIUM) > 35) {
            // return an open position adjacent to well
            if (adjacentPositionOpen) {
                rc.setIndicatorString("Moving away from center");
                return adjacentPositions[0];
            }
        }

//        if ((!rc.getLocation().equals(wellLoc) && rc.canSenseRobotAtLocation(wellLoc)) || (rc.getResourceAmount(ResourceType.MANA) + rc.getResourceAmount(ResourceType.ADAMANTIUM) > 30)) {
//            // return closest open position adjacent to well
////            if (adjacentPositionOpen) {
////                rc.setIndicatorString("Moving adjacent to well");
////                return adjacentPositions[closest_idx];
////            }
//        } else if (!rc.canSenseRobotAtLocation(wellLoc)) {
//            rc.setIndicatorString("Moving into center");
//            return wellLoc;
//        }

        return wellLoc;
    }

    static void scanIslands(RobotController rc, Team ownTeam, boolean onlyScanVisible) throws GameActionException {
        // check surroundings for islands
        int[] ids = rc.senseNearbyIslands();
        int closest = 3600;
        for (int id : ids) {
            if (rc.senseTeamOccupyingIsland(id) == Team.NEUTRAL) {
                MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                if (locs.length > 0) {
                    islandLoc = locs[0];
					currentIslandId = id;
                    closest = islandLoc.distanceSquaredTo(rc.getLocation());
                }
            }
            Communication.updateIslandInfo(rc, id);
        }
        // check shared data to see if there's closer islands
        if (islandLoc == null && !onlyScanVisible) {
            int islandCount = rc.getIslandCount();
            for (int id = 1; id < islandCount; id++) {
                if (Communication.readTeamHoldingIsland(rc, id) == Team.NEUTRAL) {
                    MapLocation loc = Communication.readIslandLocation(rc, id);
                    if (loc != null) {
                        int locDist = loc.distanceSquaredTo(rc.getLocation());
                        if (locDist < closest) {
                            closest = locDist;
                            islandLoc = loc;
							currentIslandId = id;
                        }
                    }
                }
            }
        }

		// do the same thing but for the opponent
		if (islandLoc == null) {
			for (int id : ids) {
				if (rc.senseTeamOccupyingIsland(id) == ownTeam.opponent()) {
					MapLocation[] locs = rc.senseNearbyIslandLocations(id);
					if (locs.length > 0) {
						islandLoc = locs[0];
						currentIslandId = id;
						closest = islandLoc.distanceSquaredTo(rc.getLocation());
					}
				}
				Communication.updateIslandInfo(rc, id);
			}
		}
			// check shared data to see if there's closer islands
			if (islandLoc == null && !onlyScanVisible) {
				int islandCount = rc.getIslandCount();
				for (int id = 1; id < islandCount; id++) {
					if (Communication.readTeamHoldingIsland(rc, id) == ownTeam.opponent()) {
						MapLocation loc = Communication.readIslandLocation(rc, id);
						if (loc != null) {
							int locDist = loc.distanceSquaredTo(rc.getLocation());
							if (locDist < closest) {
								closest = locDist;
								islandLoc = loc;
								currentIslandId = id;
							}
						}
					}
				}
			}

    }
}