package v3beardedreedling_1;

import static v3beardedreedling_1.Communication.headquarterLocs;

import battlecode.common.*;

public class CarrierStrategy {
	
	static final int WELL_CONGESTION_MAX = 8;
	static final int ATTACK_HEALTH_THRESH = 51;
    
    static MapLocation hqLoc;
    static int hqIdx = -1; // index into shared array
    static MapLocation islandLoc;
    static ResourceType demanded = null;

    static MapLocation nextLoc;
    static boolean anchorMode = false;
    static boolean inDanger = false;
    static boolean bugOverride = false;

    static MapLocation wellLoc;
    static MapLocation[] wellLocs = new MapLocation[]{null, null, null, null, null};
    static ResourceType[] wellTypes = new ResourceType[]{null, null, null, null, null};

    /**
     * Run a single turn for a Carrier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runCarrier(RobotController rc) throws GameActionException {
    	// INITIALIZATION
    	if (hqLoc == null) scanHQ(rc);
    	else if (hqIdx == -1) {
    		hqIdx = Communication.getIdxHQbyLocation(rc, hqLoc);
    		rc.setIndicatorString("Checking HQ");
    	}
    	
    	if (demanded == null) {
	        if (rc.getRoundNum() < 100) {
	            if ((7*rc.getID()) % 100 > 50) {
	                demanded = ResourceType.MANA;
	            } else {
	                demanded = ResourceType.ADAMANTIUM;
	            }
	        }
	        else {
	            if ((7*rc.getID()) % 100 > 30) {
	                demanded = ResourceType.MANA;
	            } else {
	                demanded = ResourceType.ADAMANTIUM;
	            }
	        }
    	}

        // MINING AND SCOUTING
        // Transfer resource to headquarters
        depositResource(rc, ResourceType.ADAMANTIUM);
        depositResource(rc, ResourceType.MANA);
        
        // Update local well cache
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

        // Push to global well cache and reset local cache
        // TODO: Have some sort of calculation to determine which wells to add
        if (rc.canWriteSharedArray(0,0)) {
            for (int j=0; j < wellLocs.length; j++) {
                if (wellLocs[j] != null) {
                    Communication.addWell(rc, wellTypes[j], wellLocs[j]);
//                    System.out.println("Added well at "+wellLocs[j]+" to shared array");
                }
            }
            wellLocs = new MapLocation[]{null, null, null, null, null};
            wellTypes = new ResourceType[]{null, null, null, null, null};
        }
        
        // Default to 
        scanWellsSelective(rc, demanded);
        scanIslands(rc);

        //Collect from well if close and inventory not full
        if(wellLoc != null && rc.canCollectResource(wellLoc, -1)) {
        	rc.collectResource(wellLoc, -1);
        	if (rc.canCollectResource(wellLoc, -1)) {
        		rc.collectResource(wellLoc, -1);
        	}
        }
        
        // for debugging
    	if (demanded == ResourceType.ADAMANTIUM) {
    		rc.setIndicatorString("Looking for Ad well " + wellLoc);
    	} else { rc.setIndicatorString("Looking for Mana well" + wellLoc); }

    	

        if(islandLoc != null && rc.canTakeAnchor(hqLoc, Anchor.STANDARD)) {
            System.out.println("took the anchor");
            rc.takeAnchor(hqLoc, Anchor.STANDARD);
            anchorMode = true;
        }
        
        if(anchorMode) {
            rc.setIndicatorDot(rc.getLocation(), 1,0,0);
            if(islandLoc == null) nextLoc = null;
            else nextLoc = islandLoc;
            rc.setIndicatorString("going to island " + nextLoc);

            if(rc.canPlaceAnchor() && rc.senseTeamOccupyingIsland(rc.senseIsland(rc.getLocation())) == Team.NEUTRAL) {
                rc.placeAnchor();
                anchorMode = false;
            }
        }
        else {
            int total = getTotalResources(rc);
            if(total < GameConstants.CARRIER_CAPACITY) {
                //move towards well or search for well
                if(wellLoc == null) nextLoc = null;
                else if(!rc.getLocation().isAdjacentTo(wellLoc)) nextLoc = wellLoc;
            }
            if(total == GameConstants.CARRIER_CAPACITY || inDanger && total > 0) {
                //move towards HQ
                nextLoc = hqLoc;
            }
        }
        
        // ENEMIES
        int radiusAct = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] robots = rc.senseNearbyRobots();
        
        int lowestHealth = 1000;
        int smallestDistance = 100;
        RobotInfo target = null;
        inDanger = false;
        if (robots.length > 0) {
            for (RobotInfo robot: robots){
            	if (robot.getTeam() == opponent) {
	                int enemyHealth = robot.getHealth();
	                int enemyDistance = robot.getLocation().distanceSquaredTo(rc.getLocation());
	                if (enemyHealth < lowestHealth && enemyDistance < radiusAct){
	                    target = robot;
	                    lowestHealth = enemyHealth;
	                    smallestDistance = enemyDistance;
	                }
	                else if (enemyHealth == lowestHealth){
	                    if (enemyDistance < smallestDistance){
	                        target = robot;
	                        smallestDistance = enemyDistance;
	                    }
	                }
	                inDanger = true;
            	}
            }
            if (robots.length > 10) {
            	bugOverride = true;
            } else { bugOverride = false; }
        }
        Communication.clearObsoleteEnemies(rc);
        Communication.tryWriteMessages(rc);
        
        // attack if about to die
        if (rc.getHealth() < ATTACK_HEALTH_THRESH) {
        	if (target != null){
                if (rc.canAttack(target.getLocation()))
                    rc.attack(target.getLocation());
                	rc.setIndicatorString("AHHHHH");
            }
        }
        
        // run from enemies
        if (RobotPlayer.isSmallMap && inDanger) {
        	nextLoc = Pathing.reportAndPlaySafe(rc, robots, 2);
        	rc.setIndicatorString("AH");
        }
       
        
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
        	Pathing.moveTowards(rc, nextLoc, bugOverride);
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
        // If none found, refer to communications
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

    static void scanIslands(RobotController rc) throws GameActionException {
        int[] ids = rc.senseNearbyIslands();
        for(int id : ids) {
            if(rc.senseTeamOccupyingIsland(id) == Team.NEUTRAL) {
                MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                if(locs.length > 0) {
                    islandLoc = locs[0];
                }
            }
            Communication.updateIslandInfo(rc, id);
        }
        int islandCount = rc.getIslandCount();
        int closest = 3600;
        for(int id = 1; id < islandCount; id++) {
            if(Communication.readTeamHoldingIsland(rc, id) == Team.NEUTRAL) {
                MapLocation loc = Communication.readIslandLocation(rc, id);
                if (loc != null) {
                    int locDist = loc.distanceSquaredTo(rc.getLocation());
                    if (locDist < closest) {
                        closest = locDist;
                        islandLoc = loc;
                    }
                }
            }
        }
    }
}
