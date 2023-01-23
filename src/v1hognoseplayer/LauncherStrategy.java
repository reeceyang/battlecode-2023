package hognoseplayerv1_2;

import battlecode.common.*;

public class LauncherStrategy {
	
	static boolean attackmode = true;
	static MapLocation nextLoc;

    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runLauncher(RobotController rc) throws GameActionException {
        // Scan enemies
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(16, opponent);
        int lowestHealth = 1000;
        int smallestDistance = 100;
        RobotInfo target = null;
        
        // Targeting
        if (enemies.length > 0) {
            for (RobotInfo enemy: enemies) {
            	// Prioritize enemies by health and distance, but especially special units
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
                Communication.reportEnemy(rc, enemy.getLocation());
            }
        }
        
        boolean patrolmode = false;
        if (target != null) {
            if (rc.canAttack(target.getLocation())) {
            	rc.attack(target.getLocation());
            } else {
            	rc.setIndicatorString("Failed to attack");
            }
        } else if (attackmode) {
            	// go to nearest reported enemy
            	MapLocation closest = Communication.getClosestEnemy(rc);
            	if (closest != null) {
            		nextLoc = closest;
            	} else {
            		patrolmode = true;
            	}
        } else {
        	patrolmode = true;
        }
        
        if (patrolmode) {
        	// go patrol a nearby well or island
            WellInfo[] wells = rc.senseNearbyWells();
            if (wells.length > 0){
            	int closestDistSq = 4000;
            	MapLocation closestWellLoc = null;
            	for (WellInfo well : wells) {
            		MapLocation wellLoc = well.getMapLocation();
            		int d2 = rc.getLocation().distanceSquaredTo(wellLoc);
            		if (d2 < closestDistSq) {
            			closestDistSq = d2;
            			closestWellLoc = wellLoc;
            		}
            	}
                nextLoc = closestWellLoc;
            } else {
            	MapLocation closestIslandLoc = Communication.getClosestIsland(rc);
            	if (closestIslandLoc != null) { nextLoc = closestIslandLoc; }
            }
        }
        
        Communication.clearObsoleteEnemies(rc);
        Communication.tryWriteMessages(rc);
        
        // prioritize remaining visible enemies
        // TODO: Logic to decide whether it's worth pursuing (based on presence of teammates, etc)
        RobotInfo[] visibleEnemies = rc.senseNearbyRobots(-1, opponent);
        for (RobotInfo enemy : visibleEnemies) {
        	if (enemy.getType() != RobotType.HEADQUARTERS) {
        		nextLoc = enemy.getLocation();
    		}
    	}
        
        // Finally, have a chance to move toward a friendly instead
        if (RobotPlayer.rng.nextInt(4) == 1) {
        	nextLoc = Pathing.swarmLoc(rc, RobotType.LAUNCHER);
        }
        
        // Execute Movement
        if (nextLoc == null) {
        	RobotPlayer.moveRandom(rc);
        } else {
        	Direction overrideDirection = Pathing.avoidCongestedHQ(rc);
        	if (overrideDirection != Direction.CENTER) {
        		// don't go anywhere near a congested HQ; this would interfere with carriers
        		nextLoc = rc.getLocation().add(overrideDirection).add(overrideDirection);
        	}
        	Pathing.moveTowards(rc, nextLoc);
        }
    }
    
    /* -------------------------------- */
}
