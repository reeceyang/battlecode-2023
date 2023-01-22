package hognosekaireece;

import battlecode.common.*;

public class Pathing {
	
	static final int baseExclusionRadius = 10;
    // Basic bug nav - Bug 0

    static Direction currentDirection = null;
	static Line line = null;
    static int closest = Integer.MAX_VALUE;
    static MapLocation closestLoc = null;
    static final int TIME_LIMIT = 8; // 8 because shortest loop is 8 units long
    static int progressCountdown = TIME_LIMIT;

    static void moveTowards(RobotController rc, MapLocation target) throws GameActionException {
        if (rc.getLocation().equals(target)) {
            return;
        }
        if (!rc.isActionReady()) {
            return;
        }
//        int currentDistance = target.distanceSquaredTo(rc.getLocation());
        if (line == null || !target.equals(line.target)) {
            line = new Line(rc.getLocation(), target);
            closest = target.distanceSquaredTo(rc.getLocation());
            progressCountdown = TIME_LIMIT;
        }
        Direction d = rc.getLocation().directionTo(target);
        rc.setIndicatorString(line + " " + d + " " + !target.equals(line.target) + " " + closest + " " + line.intersectsTile(rc.getLocation()) + " " + progressCountdown);
        rc.setIndicatorLine(rc.getLocation(), line.target, 0, 0, 0);
        MapLocation nextLocation = rc.getLocation().add(d);
        int nextDistance = nextLocation.distanceSquaredTo(target);
//        if (rc.canMove(d) && line.intersectsTile(rc.getLocation()) && nextDistance < closest) {
        if (rc.canMove(d) && nextDistance < closest) {
            rc.move(d);
            currentDirection = null; // there is no obstacle we're going around;
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
                    if (RobotPlayer.isRightHanded) currentDirection = currentDirection.rotateRight();
                    else currentDirection = currentDirection.rotateLeft();
                    break;
                } else {
                    if (RobotPlayer.isRightHanded) currentDirection = currentDirection.rotateLeft();
                    else currentDirection = currentDirection.rotateRight();
                }
            }
        }

        // if the algorithm failed (the robot went in a loop) create a new line
//        if (closestLoc.equals(rc.getLocation())) {
//            resetLine(rc, target);
        int currentDistance = rc.getLocation().distanceSquaredTo(target);
        if (currentDistance < closest) {
            closest = currentDistance;
        } else {
            progressCountdown--;
            if (progressCountdown <= 0) {
                closest = currentDistance;
            }
        }
//        rc.setIndicatorString(msg + (line != null ? line.target + " " + target : "no line") + " " + progressCountdown);
    }

	private static void resetLine(RobotController rc, MapLocation target) {
		line = new Line(rc.getLocation(), target);
		closest = Integer.MAX_VALUE;
		closestLoc = rc.getLocation();
		progressCountdown = TIME_LIMIT;
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
