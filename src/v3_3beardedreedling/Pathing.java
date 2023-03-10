package v3_3beardedreedling;

import battlecode.common.*;
import v3_2beardedreedling.RobotPlayer;

public class Pathing {
	
	static final int baseExclusionRadius = 16;
    // Basic bug nav - Bug 0
	
	static boolean leftHanded = false;
	static final int STUCK_RADIUS_SQ = 16;

    static Direction currentDirection = null;
	static MapLocation previousTarget = null;
	static final int TIME_LIMIT = 20;
	static int progressCountdown = TIME_LIMIT;
	static int closest = Integer.MAX_VALUE;
	static boolean bugMode = false;
	static final int BUG_MODE_TIME_LIMIT = 100;

    static void moveTowards(RobotController rc, MapLocation target, boolean bugOverride) throws GameActionException {
    	
        if (rc.getLocation().equals(target)) {
            return;
        }
        if (!rc.isMovementReady()) {
            return;
        }
		if (target != previousTarget) {
			previousTarget = target;
			closest = Integer.MAX_VALUE;
			bugMode = false;
			progressCountdown = TIME_LIMIT;
		}
		// apply bug mode override
		if (bugOverride) bugMode = true;
		
		if (!bugMode) {
			//int before = Clock.getBytecodesLeft();
			BellmanFord.doBellmanFord(rc, target);
//			rc.setIndicatorString("bellman ford used " + (before - Clock.getBytecodesLeft()));
		} else {
			//rc.setIndicatorString("bug mode " + progressCountdown + " left");
			Direction d = rc.getLocation().directionTo(target);
			if (rc.canMove(d)) {
				rc.move(d);
				currentDirection = null; // there is no obstacle we're going around
			} else {
				// Going around some obstacle: can't move towards d because there's an obstacle there
				// Idea: keep the obstacle on our right hand

				if (currentDirection == null) {
					currentDirection = d;
				}
				// Try to move in a way that keeps the obstacle on our right or left
				for (int i = 0; i < 8; i++) {
					if (rc.canMove(currentDirection)) {
						rc.move(currentDirection);
						if (leftHanded) {
							currentDirection = currentDirection.rotateRight();
						} else {
							currentDirection = currentDirection.rotateLeft();
						}
						break;
					} else {
						if (leftHanded) {
							currentDirection = currentDirection.rotateLeft();
						} else {
							currentDirection = currentDirection.rotateRight();
						}
					}
				}
			}
		}
		int currentDistance = rc.getLocation().distanceSquaredTo(target);
		if (currentDistance <= closest) {
			closest = currentDistance;
		} else {
			progressCountdown--;
			if (progressCountdown <= 0) {
				bugMode = !bugMode;
				progressCountdown = bugMode ? BUG_MODE_TIME_LIMIT : TIME_LIMIT;
			}
		}
	}
    
    // COMBAT MICRO
    static MapLocation reportAndPlaySafe(RobotController rc, RobotInfo[] robots, int safetyLevel) throws GameActionException {
    	// In combat, return a move away from enemies and toward friendlies. As a side effect, also scans and reports enemies.
    	// Safety level: 0 = run if outnumbered   1 = stay near friendlies   2 = run away from any enemies
    	MapLocation enemyAvg = null;
    	MapLocation friendlyAvg = null;
    	int xEnemyAvg = 0;
    	int yEnemyAvg = 0;
    	int xFriendlyAvg = 0;
    	int yFriendlyAvg = 0;
    	int nEnemy = 0;
    	int nFriendly = 0;
    	boolean outnumbered = false;
        Team opponent = rc.getTeam().opponent();
        // Report and get average location of enemies and of friendlies	
        for (RobotInfo robot : robots) {
        	if (robot.getTeam() == opponent) {
				if (robot.getType() == RobotType.HEADQUARTERS) {
					nEnemy = 50; // headquarters are extremely dangerous
					continue;
				}
        		Communication.reportEnemy(rc, robot.getLocation());
        		if (robot.getType() == RobotType.LAUNCHER || robot.getType() == RobotType.DESTABILIZER) {
	        		xEnemyAvg += robot.getLocation().x;
	        		yEnemyAvg += robot.getLocation().y;
	        		nEnemy++;
        		}
			} else if (safetyLevel < 2 && robot.getType() == RobotType.LAUNCHER || robot.getType() == RobotType.DESTABILIZER) {
        		xFriendlyAvg += robot.getLocation().x;
        		yFriendlyAvg += robot.getLocation().y;
        		nFriendly++;
        	}
        }    
        if (nEnemy != 0) { 
        	enemyAvg = new MapLocation(xEnemyAvg / nEnemy, yEnemyAvg / nEnemy);
        	outnumbered = nEnemy > nFriendly;
        }   
        if (nFriendly != 0) { friendlyAvg = new MapLocation(xFriendlyAvg / nFriendly, yFriendlyAvg / nFriendly); }
        if (enemyAvg != null && friendlyAvg != null) {
        	// Reposition behind friendlies
        	Direction d = enemyAvg.directionTo(friendlyAvg);
        	rc.setIndicatorString("Repositioning");
        	return rc.getLocation().add(d).add(d);
        } else if (safetyLevel > 0 && nEnemy > 1 || outnumbered) {
        	// Run from enemies
        	Direction d = enemyAvg.directionTo(rc.getLocation());
        	rc.setIndicatorString("Running away");
        	return rc.getLocation().add(d).add(d); 	
        } else if (safetyLevel < 2 && friendlyAvg != null) {
        	// Reposition to friendlies
        	rc.setIndicatorString("Grouping up");
        	return friendlyAvg;
        } else {
        	// Nothing is happening
        	return null;
        }
    }
    
    // SWARM FORMATION
    static MapLocation swarmLoc(RobotController rc, RobotType type) throws GameActionException {
    	// Return the first visible robot of type type.
    	RobotInfo[] friends = rc.senseNearbyRobots(-1, rc.getTeam());
    	MapLocation target = null;
    	for (RobotInfo robot : friends) {
    		if (robot.getType() == type) {
    			target = robot.getLocation();
    			break;
    		}
    	}
    	return target;
    }
    
    // TRAFFIC MANAGEMENT AND HQ AVOIDANCE
    static Direction avoidVisibleHQ(RobotController rc) throws GameActionException {
    	// If near an HQ, get a direction going away from the HQ (friendly or enemy).
    	Direction answer = Direction.CENTER;
    	RobotInfo[] visible = rc.senseNearbyRobots();
    	for (RobotInfo robot : visible) {
    		if (robot.getType() == RobotType.HEADQUARTERS) {
    			MapLocation loc = robot.getLocation();
    			if (loc.distanceSquaredTo(rc.getLocation()) < baseExclusionRadius) {
    				answer = loc.directionTo(rc.getLocation());
    				break;
    			}
    		}
    	}
    	return answer;
    }
    
    static Direction avoidCongestedHQ(RobotController rc) throws GameActionException {
    	// Suggest a direction to move away from the nearest congested HQ. Otherwise center direction.
    	Direction answer = Direction.CENTER;
    	int closestDist = 3600;
    	int closestIdx = -1;
    	int exclusion = rc.getRobotCount() / 4;
    	for (int i = 0; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {
    		if (Communication.checkHeadquarterCongestion(rc, i)) {
    			// checkHQCongestion will return false if the HQ doesn't exist
    			int dist = rc.getLocation().distanceSquaredTo(Communication.readHeadquarterLocation(rc, i));
    			if (dist < closestDist) {
    				closestDist = dist;
        			closestIdx = i;
    			}
    		}	 
    	} 
    	if (closestIdx != -1 && closestDist < exclusion) {
    		answer = (Communication.readHeadquarterLocation(rc, closestIdx)).directionTo(rc.getLocation());
    		rc.setIndicatorString("Avoiding congested HQ");
    	}
    	return answer;
    }
    
    static MapLocation findManaWell(RobotController rc, MapLocation adWell, MapLocation hqLoc) throws GameActionException {
		if (adWell == null) {
			return null;
		}
    	if (rc.getLocation().distanceSquaredTo(adWell) > 36) {
			rc.setIndicatorString("Going towards Ad well "+adWell);
			return adWell;
		} else {
			Direction d = hqLoc.directionTo(adWell);
			switch (rc.getID() % 3) {
				case 0:
					d = d.rotateLeft();
					break;
				case 1:
					break;
				case 2:
					d = d.rotateRight();
					break;
			}
			return rc.getLocation().add(d);
//			MapLocation[] nearbyClouds = rc.senseNearbyCloudLocations(adWell, 100);
//			if (nearbyClouds.length > 0) {
//				rc.setIndicatorString("Going towards cloud "+nearbyClouds[0]);
//				return nearbyClouds[0]; // need better strategy so it doesn't get stuck in a cloud forever ;-;
//			} else {
//				rc.setIndicatorString("Going around Ad well "+adWell);
//			}
		}
	}
}
