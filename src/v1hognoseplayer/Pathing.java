package hognoseplayerv1_2;

import battlecode.common.*;

public class Pathing {
	
	static final int baseExclusionRadius = 10;
    // Basic bug nav - Bug 0

    static Direction currentDirection = null;

    static void moveTowards(RobotController rc, MapLocation target) throws GameActionException {
        if (rc.getLocation().equals(target)) {
            return;
        }
        if (!rc.isActionReady()) {
            return;
        }
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
            // Try to move in a way that keeps the obstacle on our right
            for (int i = 0; i < 8; i++) {
                if (rc.canMove(currentDirection)) {
                    rc.move(currentDirection);
                    currentDirection = currentDirection.rotateRight();
                    break;
                } else {
                    currentDirection = currentDirection.rotateLeft();
                }
            }
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
    
    // TRAFFIC MANAGEMENT
    static Direction avoidVisibleHQ(RobotController rc) throws GameActionException {
    	// If near an HQ, get a direction going away from the HQ.
    	Direction answer = Direction.CENTER;
    	RobotInfo[] visible = rc.senseNearbyRobots(-1, rc.getTeam());
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
    	int exclusion = rc.getRobotCount();
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
    	}
    	return answer;
    }
}
