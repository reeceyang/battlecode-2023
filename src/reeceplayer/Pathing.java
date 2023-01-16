package reeceplayer;

import battlecode.common.*;

import java.util.Random;

public class Pathing {
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
        }
        Direction d = rc.getLocation().directionTo(target);
        rc.setIndicatorString(line + " " + d + " " + !target.equals(line.target) + " " + closest + " " + line.intersectsTile(rc.getLocation()) + " " + progressCountdown);
        rc.setIndicatorLine(line.start, line.target, 0, 0, 0);
        MapLocation nextLocation = rc.getLocation().add(d);
        int nextDistance = nextLocation.distanceSquaredTo(target);
        if (rc.canMove(d) && line.intersectsTile(rc.getLocation()) && nextDistance < closest) {
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
            if (progressCountdown == 0) {
                resetLine(rc, target);
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
}

