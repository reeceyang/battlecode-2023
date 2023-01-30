package v4potbellypig;

import battlecode.common.*;

public class LauncherStrategy {

    enum LauncherState {
        DEFAULT,
        OFFENSE,
        DANGER,
        SHOOT_MOVE,
    }
	
	static boolean bugOverride = false;
	static final int BUG_OVERRIDE_THRESH = 20;
    static MapLocation hqLoc;
	static MapLocation nextLoc;
    static LauncherState state = null;
    static Team opponent;

    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runLauncher(RobotController rc) throws GameActionException {
        // Scan robots
        if (opponent == null) {
            opponent = rc.getTeam().opponent();
        }
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
        bugOverride = robots.length > BUG_OVERRIDE_THRESH;
        RobotInfo target = getTargetSetDanger(rc, robots);
        if (target != null) {
            state = LauncherState.SHOOT_MOVE;
        }

        // MOVING
        boolean foundObsoleteEnemyOnly = Communication.clearObsoleteEnemies(rc); // used to ignore obsolete enemies

        switch (state) {
            case DEFAULT:
                break;
            case OFFENSE:
                // go to nearest island, with priority for visible enemy island
                // Sense nearby islands and see if there are any visible squares on that island
                // If we don't sense an island nearby, check communications for islands
                boolean attackingIsland = false;
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
                break;
            case DANGER:
                break;
            case SHOOT_MOVE:
                if (target != null) shootTarget(rc, target);
                break;
        }

        Communication.tryWriteMessages(rc);
        
        // Execute Movement
        if (nextLoc == null) {
        	RobotPlayer.moveRandom(rc);
        } else {
        	Pathing.moveTowards(rc, nextLoc, bugOverride, false);
        	rc.setIndicatorLine(rc.getLocation(), nextLoc, 0, 255, 0);
//            rc.setIndicatorString(nextLoc + " " + rc.getLocation());
        }

        if (rc.isActionReady()) {
            robots = rc.senseNearbyRobots();
            target = getTargetSetDanger(rc, robots);
            if (target != null) {
                shootTarget(rc, target);
                rc.setIndicatorString("move shooting" + target.getLocation());
            }
        }
    }
    
    /* -------------------------------- */

    /**
     * gets the closest target to shoot at and also sets the state to danger
     * if multiple targets are equally the close the one with the least health is prioritized
     * @param rc
     * @param robots
     * @return the target to shoot at
     */
    static RobotInfo getTargetSetDanger(RobotController rc, RobotInfo[] robots) {
        RobotInfo target = null;
        int lowestHealth = 1000;
        int smallestDistance = 100;
        for (RobotInfo enemy : robots) {
            if (enemy.getTeam() != opponent) {
                continue;
            }
            // Prioritize enemies by health and distance, but especially special units
            state = LauncherState.DANGER;
            // kite around the enemy headquarters if there's no other enemies
            if (enemy.getType() == RobotType.HEADQUARTERS && target == null) {
                target = enemy;
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
            // previous code prioritized lower health enemies over closer enemies
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
        return target;
    }

    static void shootTarget(RobotController rc, RobotInfo target) throws GameActionException {
        MapLocation targetLoc = target.getLocation();
        if (rc.canAttack(targetLoc) && target.getType() != RobotType.HEADQUARTERS) {
            rc.attack(targetLoc);
        } else {
            rc.setIndicatorString("Failed to attack");
        }
        // shoot-move (if doing move-shoot nextLoc gets reset the next turn)
        if (targetLoc.distanceSquaredTo(rc.getLocation()) <= 20) {
            Direction d = targetLoc.directionTo(rc.getLocation());
            nextLoc = rc.getLocation().add(d).add(d);
        }
    }
}
