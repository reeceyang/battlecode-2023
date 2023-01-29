package v4potbellypig;

import battlecode.common.*;

public class LauncherStrategy {

    enum LauncherState {
        DEFAULT,
        OFFENSE,
        DANGER,
    }
	
	static boolean bugOverride = false;
	static final int BUG_OVERRIDE_THRESH = 20;
    static MapLocation hqLoc;
	static MapLocation nextLoc;
    static LauncherState state = null;

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

        state = LauncherState.DEFAULT;

        if (RobotPlayer.isSmallMap) {
            nextLoc = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        } else {
            nextLoc = Pathing.reportAndPlaySafe(rc, robots, 0);
            if (rc.getRoundNum() > 50) {
                state = LauncherState.OFFENSE;
            }
        }

        // TARGETING
        int lowestHealth = 1000;
        int smallestDistance = 100;
        RobotInfo target = null;
        bugOverride = robots.length > BUG_OVERRIDE_THRESH;
        for (RobotInfo enemy : robots) {
            if (enemy.getTeam() != opponent) {
                continue;
            }
            // Prioritize enemies by health and distance, but especially special units
            state = LauncherState.DANGER;
            if (enemy.getType() == RobotType.HEADQUARTERS) {
                continue;
            }
            if (enemy.getType() == RobotType.DESTABILIZER) {
                target = enemy;
                break;
            }
            int enemyHealth = enemy.getHealth();
            int enemyDistance = enemy.getLocation().distanceSquaredTo(rc.getLocation());
            if (enemyDistance < smallestDistance) {
                target = enemy;
                smallestDistance = enemyDistance;
                lowestHealth = enemyHealth;
            } else if (enemyDistance == smallestDistance && enemyHealth < lowestHealth) {
                target = enemy;
                lowestHealth = enemyHealth;
            }
//            if (enemyHealth < lowestHealth) {
//                target = enemy;
//                lowestHealth = enemyHealth;
//                smallestDistance = enemyDistance;
//            } else if (enemyHealth == lowestHealth) {
//                if (enemyDistance < smallestDistance) {
//                    target = enemy;
//                    smallestDistance = enemyDistance;
//                }
//            }
        }

        // MOVING
        boolean foundObsoleteEnemyOnly = Communication.clearObsoleteEnemies(rc); // used to ignore obsolete enemies
        
        if (target != null) {
            MapLocation targetLoc = target.getLocation();
            if (rc.canAttack(targetLoc)) {
            	rc.attack(targetLoc);
            } else {
            	rc.setIndicatorString("Failed to attack");
            }
            if (targetLoc.distanceSquaredTo(rc.getLocation()) <= 20) {
                Direction d = targetLoc.directionTo(rc.getLocation());
                nextLoc = rc.getLocation().add(d).add(d);
            }
        } else if (state == LauncherState.OFFENSE) {// go to nearest island, with priority for visible enemy island
            boolean attackingIsland = false;
            // Sense nearby islands and see if there are any visible squares on that island
            int[] ids = rc.senseNearbyIslands();
            for (int id : ids) {
                if (rc.senseTeamOccupyingIsland(id) == opponent) {
                    MapLocation[] locs = rc.senseNearbyIslandLocations(id);
                    if (locs.length > 0) {
                        nextLoc = locs[0];
                        attackingIsland = true;
                    } 
                }
                rc.setIndicatorString("sensed " + id);
                Communication.updateIslandInfo(rc, id);
            }
            // If we don't sense an island nearby, check communications for islands
            if (!attackingIsland) {
            	MapLocation closestIslandLoc = Communication.getClosestIsland(rc);
                if (closestIslandLoc != null) {
                    nextLoc = closestIslandLoc;
                }
                MapLocation closestEnemyLoc = Communication.getClosestEnemy(rc);
                if (closestEnemyLoc != null && !foundObsoleteEnemyOnly) {
                    nextLoc = closestEnemyLoc;
                }
            }
        }

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
        	Pathing.moveTowards(rc, nextLoc, bugOverride, false);
        	rc.setIndicatorLine(rc.getLocation(), nextLoc, 0, 255, 0);
//            rc.setIndicatorString(nextLoc + " " + rc.getLocation());
        }
    }
    
    /* -------------------------------- */
}
