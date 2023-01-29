package v3beardedreedling_1;

import battlecode.common.*;

public class LauncherStrategy {
	
	static boolean attackmode = RobotPlayer.isSmallMap;
	static boolean bugOverride = false;
	static final int BUG_OVERRIDE_THRESH = 20;
	static boolean inDanger = false;
    static MapLocation hqLoc;
	static MapLocation nextLoc;

    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runLauncher(RobotController rc) throws GameActionException {
        // Scan robots
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] robots = rc.senseNearbyRobots();
        
        if (hqLoc == null) {
            hqLoc = rc.getLocation();
        }
        if (!RobotPlayer.isSmallMap) {
            attackmode = rc.getRoundNum() > 50;
        }
        nextLoc = Pathing.reportAndPlaySafe(rc, robots, 0);
        if (RobotPlayer.isSmallMap) {
            nextLoc = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        }

        // Targeting
        int lowestHealth = 1000;
        int smallestDistance = 100;
        RobotInfo target = null;
        inDanger = false;
        bugOverride = robots.length > BUG_OVERRIDE_THRESH;
        if (robots.length > 0) {
            for (RobotInfo enemy: robots) {
            	if (enemy.getTeam() != opponent) { continue; }
            	// Prioritize enemies by health and distance, but especially special units
            	inDanger = true;
                if (enemy.getType() == RobotType.HEADQUARTERS) {
                	continue;
                }
            	if (enemy.getType() == RobotType.DESTABILIZER) {
                	target = enemy;
                	break;
                }
            	int enemyHealth = enemy.getHealth();
                int enemyDistance = enemy.getLocation().distanceSquaredTo(rc.getLocation());
                if (enemyHealth < lowestHealth) {
                    target = enemy;
                    lowestHealth = enemyHealth;
                    smallestDistance = enemyDistance;
                }
                else if (enemyHealth == lowestHealth) {
                    if (enemyDistance < smallestDistance){
                        target = enemy;
                        smallestDistance = enemyDistance;
                    }
                }
            }
        }

        // keep the launchers at the hq for the first 50 turns
        if (target != null) {
            if (rc.canAttack(target.getLocation())) {
            	rc.attack(target.getLocation());
            } else {
            	rc.setIndicatorString("Failed to attack");
            }
        } else if (attackmode && !inDanger) {
            // go to nearest island, with priority for visible enemy island
        	boolean attackingIsland = false;
        	int[] ids = rc.senseNearbyIslands();
            for(int id : ids) {
                if(rc.senseTeamOccupyingIsland(id) == opponent) {
                    MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                    if (locs.length > 0) {
                    	nextLoc = locs[0];
                    	attackingIsland = true;
                    } else {
                    	MapLocation closestIslandLoc = Communication.getClosestIsland(rc);
                        if (closestIslandLoc != null) {
                            nextLoc = closestIslandLoc;
                        }
                    }
                }
                rc.setIndicatorString("sensed " + id);
                Communication.updateIslandInfo(rc, id);
            }
        	// or go to nearest reported enemy
            MapLocation closest = Communication.getClosestEnemy(rc);
            if (closest != null && !attackingIsland) {
                nextLoc = closest;
            }     
        }
        	
        
        Communication.clearObsoleteEnemies(rc);
        Communication.tryWriteMessages(rc);
        
        // Execute Movement
        if (nextLoc == null) {
        	RobotPlayer.moveRandom(rc);
        } else {
//        	Direction overrideDirection = Pathing.avoidVisibleHQ(rc);
//        	if (overrideDirection == Direction.CENTER) { overrideDirection = Pathing.avoidCongestedHQ(rc); }
//
//        	if (overrideDirection != Direction.CENTER) {
//        		// don't go anywhere near a congested HQ; this would interfere with carriers
//        		nextLoc = rc.getLocation().add(overrideDirection).add(overrideDirection);
//        	}
        	Pathing.moveTowards(rc, nextLoc, bugOverride);
        	rc.setIndicatorLine(rc.getLocation(), nextLoc, 0, 255, 0);
        }
    }
    
    /* -------------------------------- */
}
