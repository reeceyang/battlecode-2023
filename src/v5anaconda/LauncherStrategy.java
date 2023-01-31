package v5anaconda;

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
    static final int HQ_KITE_THRESHOLD = 100;
    static MapLocation hqLoc;
	static MapLocation nextLoc;
	static int ignoreCounter = 0;
	static MapLocation ignoreLoc;
	static final int IGNORE_TIME = 10;
    static LauncherState state = null;
    static Team opponent;
    static final int ACTION_RADIUS = 16;
    
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

        if (RobotPlayer.isSmallMap && rc.getRoundNum() < 80) {
            nextLoc = new MapLocation(rc.getMapWidth() / 2, rc.getMapHeight() / 2);
        } else {
            nextLoc = Pathing.reportAndPlaySafe(rc, robots, 0);
            if (rc.getRoundNum() > 80) {
                state = LauncherState.OFFENSE;
            }
        }

        // TARGETING
        bugOverride = robots.length > BUG_OVERRIDE_THRESH;
        RobotInfo target = getTargetSetDanger(rc, robots);
        // only shoot move if the target is within action radius
        if (target != null && target.getLocation().distanceSquaredTo(rc.getLocation()) <= ACTION_RADIUS) {
            state = LauncherState.SHOOT_MOVE;
        }

        // MOVING
        if (target == null && Communication.clearObsoleteEnemies(rc)) {
        	// No enemies seen, and an obsolete enemy location visible
        	ignoreCounter += 1;
        } else {
        	// either see an enemy or not by an obsolete location
        	ignoreCounter = 0;
        }
        if (ignoreLoc != null) {
        	// already ignored a location
        	ignoreCounter -= 1;
        }
        if (ignoreCounter > 10) {
        	// set an ignore location
        	ignoreLoc = rc.getLocation();
        }
        if (ignoreCounter < -20) {
        	// clear ignore location
        	ignoreLoc = null;
        }      

        switch (state) {
            case DEFAULT:
            case DANGER:
                break;
            case SHOOT_MOVE:
                if (target != null) shootTarget(rc, target);
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
//                    rc.setIndicatorString("sensed " + id);
                    Communication.updateIslandInfo(rc, id);
                }
                if (!attackingIsland) {
                    MapLocation closestIslandLoc = Communication.getClosestIsland(rc);
                    if (closestIslandLoc != null) {
                        nextLoc = closestIslandLoc;
                    }
                    MapLocation closestEnemyLoc = Communication.getClosestEnemy(rc, ignoreLoc);
                    if (closestEnemyLoc != null) {
                        nextLoc = closestEnemyLoc;
                    }
                }
                break;
        }

        Communication.tryWriteMessages(rc);
        rc.setIndicatorString(state.toString());
        
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
            } else {
                int closestCloudDist = 7200;
                MapLocation targetCloud = null;
                MapLocation[] nearbyClouds = rc.senseNearbyCloudLocations(ACTION_RADIUS);
                for (MapLocation cloud : nearbyClouds) {
                    int tempDist = rc.getLocation().distanceSquaredTo(cloud);
                    if (tempDist < closestCloudDist) {
                        closestCloudDist = tempDist;
                        targetCloud = cloud;
                    }
                }
                if (targetCloud != null) shootCloud(rc, targetCloud);
            }
        }
        
        if (ignoreLoc != null) {
        	rc.setIndicatorString("Ignoring: " + ignoreLoc);
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
            // but only if health is low
            if (enemy.getType() == RobotType.HEADQUARTERS) {
                if (target == null && rc.getHealth() < HQ_KITE_THRESHOLD) {
                    System.out.println(rc.getHealth() + "kiting" + enemy.getLocation());
                    target = enemy;
                }
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
        // if we're shoot-moving and killed the current target, try to get another target
        if (target.getType() != RobotType.HEADQUARTERS && target.getHealth() <= 20 && rc.isMovementReady()) {
            RobotInfo[] robots = rc.senseNearbyRobots();
            target = getTargetSetDanger(rc, robots);
            if (target != null && target.getHealth() <= 0) {
                System.out.println("target has health <= 0 bruh");
            }
            if (target != null) nextLoc = target.getLocation();
            return;
        }
        // shoot-move (if doing move-shoot nextLoc gets reset the next turn)
        if (targetLoc.distanceSquaredTo(rc.getLocation()) <= ACTION_RADIUS) {
            Direction d = targetLoc.directionTo(rc.getLocation());
            nextLoc = rc.getLocation().add(d).add(d);
        }
    }

    static void shootCloud(RobotController rc, MapLocation target) throws GameActionException {
        if (rc.canAttack(target)) {
            rc.attack(target);
        } else {
            rc.setIndicatorString("Failed to attack");
        }
    }
}
