package v3_2beardedreedling;

import battlecode.common.*;

public class CarrierStrategy {
	
	static final int WELL_CONGESTION_MAX = 8;
	static final int ATTACK_HEALTH_THRESH = 51;
    
    static MapLocation hqLoc;
    static int hqIdx = -1; // index into shared array
    static MapLocation wellLoc;
    static MapLocation islandLoc;
    static ResourceType demanded = null;
    static int currentIslandId = -1;

    static MapLocation nextLoc;
    static boolean anchorMode = false;
    static boolean reportMode = false;
    static boolean inDanger = false;

    static MapLocation[] wellLocs = new MapLocation[]{null, null, null, null, null};
    static ResourceType[] wellTypes = new ResourceType[]{null, null, null, null, null};

    /**
     * Run a single turn for a Carrier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runCarrier(RobotController rc) throws GameActionException {
        Team ownTeam = rc.getTeam();

        if (hqLoc == null) scanHQ(rc); // get the home hq and set hqIdx
    	else if (hqIdx == -1) {
    		hqIdx = Communication.getIdxHQbyLocation(rc, hqLoc);
    		rc.setIndicatorString("Checking HQ");
    	}
        if (demanded == null) {
            if (rc.getRoundNum() < 100) {
                if (rc.getID() % 100 > 30) {
                    demanded = ResourceType.MANA;
                } else {
                    demanded = ResourceType.ADAMANTIUM;
                }
            }
            else {
                if (rc.getID() % 100 > 20) {
                    demanded = ResourceType.MANA;
                } else {
                    demanded = ResourceType.ADAMANTIUM;
                }
            }
        }

        rc.senseNearbyWells();
        for (WellInfo well : rc.senseNearbyWells()) {
            if (!Communication.isWellWritten(rc, well.getMapLocation())) {
                for (int i=0; i < wellLocs.length; i++) {
                    if (wellLocs[i] == null) {
                        wellLocs[i] = well.getMapLocation();
                        wellTypes[i] = well.getResourceType();
                        break;
                    }
                }
            }
        }

        for (MapLocation loc : wellLocs) {
            if (loc != null && !Communication.isWellWritten(rc, loc)) { // if any location is not written, turn on reportMode to go back to hq
                reportMode = true;
                break;
            }
            reportMode = false; // if all locations have been written or there's nothing in the array(?) turn off reportMode
        }

        if (rc.canWriteSharedArray(0,0)) {
            for (int j=0; j < wellLocs.length; j++) {
                if (wellLocs[j] != null) {
                    Communication.addWell(rc, wellTypes[j], wellLocs[j]);
                }
            }
            wellLocs = new MapLocation[]{null, null, null, null, null};
            wellTypes = new ResourceType[]{null, null, null, null, null};
        }

    	// for debugging
    	if (demanded == ResourceType.ADAMANTIUM) {
    		rc.setIndicatorString("Looking for Ad well " + wellLoc);
    	} else { rc.setIndicatorString("Looking for Mana well" + wellLoc); }
    	
        if (wellLoc == null || RobotPlayer.turnCount % 5 == 0) scanWellsSelective(rc, demanded);

        scanIslands(rc, ownTeam);

        // Enemies and scouting
        int radius = rc.getType().visionRadiusSquared;
        int radiusAct = rc.getType().actionRadiusSquared;
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, ownTeam.opponent());
        
        int lowestHealth = 1000;
        int smallestDistance = 100;
        RobotInfo target = null;
        for (RobotInfo enemy : enemies) {
            int enemyHealth = enemy.getHealth();
            int enemyDistance = enemy.getLocation().distanceSquaredTo(rc.getLocation());
            if (enemyHealth < lowestHealth && enemyDistance < radiusAct) {
                target = enemy;
                lowestHealth = enemyHealth;
                smallestDistance = enemyDistance;
            } else if (enemyHealth == lowestHealth) {
                if (enemyDistance < smallestDistance) {
                    target = enemy;
                    smallestDistance = enemyDistance;
                }
            }
        }
        Communication.clearObsoleteEnemies(rc);

        //Collect from well if close and inventory not full
        if(wellLoc != null && rc.canCollectResource(wellLoc, -1)) rc.collectResource(wellLoc, -1);

        //Transfer resource to headquarters and reset inDanger
        depositResource(rc, ResourceType.ADAMANTIUM);
        depositResource(rc, ResourceType.MANA);

        if(islandLoc != null && rc.canTakeAnchor(hqLoc, Anchor.STANDARD)) {
            System.out.println("took the anchor");
            rc.takeAnchor(hqLoc, Anchor.STANDARD);
            anchorMode = true;
        }
        
        if(anchorMode) {
            rc.setIndicatorDot(rc.getLocation(), 255,0,0);
            if (nextLoc == null && islandLoc == null) {
                nextLoc = null;
                rc.setIndicatorString("going to island " + nextLoc);
            } else if (currentIslandId != -1 && ((rc.canSenseLocation(islandLoc) && rc.senseTeamOccupyingIsland(currentIslandId) == ownTeam) || nextLoc != null)) {
                if (Communication.findSymmetry(rc) != 0) {
                    nextLoc = Communication.getSymLoc(rc, islandLoc);
                } else {
                    if (Communication.readHQStatus(rc, "nx") == 1) {
                        switch (rc.getID() % 2) {
                            case 0: nextLoc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, islandLoc)); break;
                            case 1: nextLoc = Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, islandLoc)); break;
                        }
                    } else if (Communication.readHQStatus(rc, "ny") == 1) {
                        switch (rc.getID() % 2) {
                            case 0: nextLoc = Communication.intToLocation(rc, RobotPlayer.xReflect(rc, islandLoc)); break;
                            case 1: nextLoc = Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, islandLoc)); break;
                        }
                    } else {
                        switch (rc.getID() % 2) {
                            case 0: nextLoc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, islandLoc)); break;
                            case 1: nextLoc = Communication.intToLocation(rc, RobotPlayer.xReflect(rc, islandLoc)); break;
                        }
                    }
                }
                if (scanNearbyIslands(rc, ownTeam) != null) {
                    rc.setIndicatorString("i am imposter");
                    nextLoc = islandLoc;
                }
            } else {
                scanIslands(rc, ownTeam); // implementing this should make it so carriers continually look for optimal island to place anchor on?
                nextLoc = islandLoc;
                rc.setIndicatorString("going to island " + nextLoc);
            }

            if (rc.canPlaceAnchor() && rc.senseTeamOccupyingIsland(rc.senseIsland(rc.getLocation())) == Team.NEUTRAL) {
                rc.placeAnchor();
                anchorMode = false;
            }

            if (rc.getHealth() < 31) rc.setIndicatorString("health low");

            if ((islandLoc == null || rc.getLocation().distanceSquaredTo(islandLoc) > 20) && rc.getHealth() < 31) {
                rc.setIndicatorString("trying to attacc");
                if (rc.canAttack(enemies[0].getLocation())) rc.attack(enemies[0].getLocation());
                rc.setIndicatorString("attacc");
            }

        } else if (reportMode) {
            nextLoc = hqLoc;
        } else {
            int total = getTotalResources(rc);
            if(total < GameConstants.CARRIER_CAPACITY) {
                //move towards well or search for well
                if (wellLoc == null && demanded == ResourceType.MANA) {
                    scanWellsSelective(rc, ResourceType.ADAMANTIUM);
                    if (wellLoc != null) {
                        nextLoc = Pathing.findManaWell(rc, wellLoc);
                        wellLoc = null;
                    }
                } else if (rc.getLocation().equals(wellLoc)) {
                    nextLoc = nextWellLoc(rc, wellLoc);
                } else if (wellLoc != null) {
                    nextLoc = wellLoc;
                } else nextLoc = null;
            }
            if (total == GameConstants.CARRIER_CAPACITY || inDanger && total > 0) {
                //move towards HQ
                nextLoc = hqLoc;
            }
        }
        
        // attack if about to die
        if (rc.getHealth() < ATTACK_HEALTH_THRESH) {
        	if (target != null){
                if (rc.canAttack(target.getLocation()))
                    rc.attack(target.getLocation());
//                	rc.setIndicatorString("AHHHHH");
            }
        }
        
        // run from enemies
        if (RobotPlayer.isSmallMap && enemies.length > 0) {
        	nextLoc = Pathing.reportAndPlaySafe(rc, enemies, 2);
//            nextLoc = hqLoc;
        	rc.setIndicatorString("AH");
        	inDanger = true;
        }
        
        Communication.tryWriteMessages(rc);
        
        // Finalize and execute movement
//        if (getTotalResources(rc) == 0) {
//    		// if not carrying resources, move away from any HQ
//    		Direction overrideDirection = Pathing.avoidVisibleHQ(rc);
//        	if (overrideDirection != Direction.CENTER) {
//        		nextLoc = rc.getLocation().add(overrideDirection).add(overrideDirection);
//        	}
//    	}
        if (nextLoc == null) {
        	RobotPlayer.moveRandom(rc);
        } else {
        	Pathing.moveTowards(rc, nextLoc);
            rc.setIndicatorLine(rc.getLocation(), nextLoc, 0, 0, 1);
        }
    }

    
    /* --------------------------------------------- */
    
    static void scanHQ(RobotController rc) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        for(RobotInfo robot : robots) {
            if(robot.getTeam() == rc.getTeam() && robot.getType() == RobotType.HEADQUARTERS) {
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
        if (wellLoc == null) {
            if (demanded != null) {
                wellLoc = Communication.getNearestWellOfType(rc, demanded);
            } else {
                wellLoc = Communication.getNearestWellOfType(rc, ResourceType.MANA);
            }
        }
    }

    static void depositResource(RobotController rc, ResourceType type) throws GameActionException {
        int amount = rc.getResourceAmount(type);
        if(amount > 0) {
            if (rc.canTransferResource(hqLoc, type, amount)) {
            	rc.transferResource(hqLoc, type, amount);
            	inDanger = false;
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

    static void scanIslands(RobotController rc, Team ownTeam) throws GameActionException {
        int[] ids = rc.senseNearbyIslands();
        for(int id : ids) {
            if(rc.senseTeamOccupyingIsland(id) == Team.NEUTRAL) {
                MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                if(locs.length > 0) {
                    islandLoc = locs[0];
                    currentIslandId = id;
                    rc.setIndicatorString("Going to sensed island");
//                    if (rc.getNumAnchors(Anchor.STANDARD) > 0) System.out.println("nearest unoccupied island sensed is "+islandLoc);
                }
            }
            Communication.updateIslandInfo(rc, id);
        }
        if (islandLoc == null) {
            int islandCount = rc.getIslandCount();
            int closest = 3600;
            for (int id = 1; id < islandCount; id++) {
                if (Communication.readTeamHoldingIsland(rc, id) == Team.NEUTRAL) {
                    MapLocation loc = Communication.readIslandLocation(rc, id);
                    if (loc != null) {
                        int locDist = loc.distanceSquaredTo(rc.getLocation());
                        if (locDist < closest) {
                            closest = locDist;
                            islandLoc = loc;
                            currentIslandId = id;
                            rc.setIndicatorString("Going to island from shared array");
                        }
                    }
                }
            }
        }

        if (islandLoc == null) {
            for(int id : ids) {
                if(rc.senseTeamOccupyingIsland(id) == ownTeam.opponent()) {
                    MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                    if(locs.length > 0) {
                        islandLoc = locs[0];
                        currentIslandId = id;
                        rc.setIndicatorString("Going to sensed island");
//                    if (rc.getNumAnchors(Anchor.STANDARD) > 0) System.out.println("nearest unoccupied island sensed is "+islandLoc);
                    }
                }
                Communication.updateIslandInfo(rc, id);
            }
        }

        if (islandLoc == null) {
            int islandCount = rc.getIslandCount();
            int closest = 3600;
            for (int id = 1; id < islandCount; id++) {
                if (Communication.readTeamHoldingIsland(rc, id) == ownTeam.opponent()) {
                    MapLocation loc = Communication.readIslandLocation(rc, id);
                    if (loc != null) {
                        int locDist = loc.distanceSquaredTo(rc.getLocation());
                        if (locDist < closest) {
                            closest = locDist;
                            islandLoc = loc;
                            currentIslandId = id;
                            rc.setIndicatorString("Going to island from shared array");
                        }
                    }
                }
            }
        }
//        if (rc.getNumAnchors(Anchor.STANDARD) > 0) System.out.println("ultimate island decided upon is "+islandLoc);
    }

    static MapLocation scanNearbyIslands(RobotController rc, Team ownTeam) throws GameActionException {
        int[] ids = rc.senseNearbyIslands();
        for(int id : ids) {
            if(rc.senseTeamOccupyingIsland(id) == Team.NEUTRAL) {
                MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                if(locs.length > 0) {
                    currentIslandId = id;
                    rc.setIndicatorString("Going to sensed island");
                    return locs[0];
//                    if (rc.getNumAnchors(Anchor.STANDARD) > 0) System.out.println("nearest unoccupied island sensed is "+islandLoc);
                }
            }
            Communication.updateIslandInfo(rc, id);
        }

        if (islandLoc == null) {
            for(int id : ids) {
                if(rc.senseTeamOccupyingIsland(id) == ownTeam.opponent()) {
                    MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                    if(locs.length > 0) {
                        currentIslandId = id;
                        rc.setIndicatorString("Going to sensed island");
                        return locs[0];
//                    if (rc.getNumAnchors(Anchor.STANDARD) > 0) System.out.println("nearest unoccupied island sensed is "+islandLoc);
                    }
                }
                Communication.updateIslandInfo(rc, id);
            }
        }

        return null;
    }
}
