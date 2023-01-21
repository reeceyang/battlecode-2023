package reeceplayer;

import battlecode.common.*;

import static reeceplayer.BellmanFord.doBellmanFord;

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
        doBellmanFord(rc, target);
//        int currentDistance = target.distanceSquaredTo(rc.getLocation());
//        if (line == null || !target.equals(line.target)) {
//            line = new Line(rc.getLocation(), target);
//            closest = target.distanceSquaredTo(rc.getLocation());
//            progressCountdown = TIME_LIMIT;
//        }
//        Direction d = rc.getLocation().directionTo(target);
//        rc.setIndicatorString(line + " " + d + " " + !target.equals(line.target) + " " + closest + " " + line.intersectsTile(rc.getLocation()) + " " + progressCountdown);
//        rc.setIndicatorLine(rc.getLocation(), line.target, 0, 0, 0);
//        MapLocation nextLocation = rc.getLocation().add(d);
//        int nextDistance = nextLocation.distanceSquaredTo(target);
////        if (rc.canMove(d) && line.intersectsTile(rc.getLocation()) && nextDistance < closest) {
//        if (rc.canMove(d) && nextDistance < closest) {
//            rc.move(d);
//            currentDirection = null; // there is no obstacle we're going around;
//        } else {
//            // Going around some obstacle: can't move towards d because there's an obstacle there
//            // Idea: keep the obstacle on our right hand
//
//            if (currentDirection == null) {
//                currentDirection = d;
//            }
//
//            // Try to move in a way that keeps the obstacle on our right
//            for (int i = 0; i < 8; i++) {
//                if (rc.canMove(currentDirection)) {
//                    rc.move(currentDirection);
//                    if (RobotPlayer.isRightHanded) currentDirection = currentDirection.rotateRight();
//                    else currentDirection = currentDirection.rotateLeft();
//                    break;
//                } else {
//                    if (RobotPlayer.isRightHanded) currentDirection = currentDirection.rotateLeft();
//                    else currentDirection = currentDirection.rotateRight();
//                }
//            }
//        }
//
//        // if the algorithm failed (the robot went in a loop) create a new line
////        if (closestLoc.equals(rc.getLocation())) {
////            resetLine(rc, target);
//        int currentDistance = rc.getLocation().distanceSquaredTo(target);
//        if (currentDistance < closest) {
//            closest = currentDistance;
//        } else {
//            progressCountdown--;
//            if (progressCountdown <= 0) {
//                closest = currentDistance;
//            }
//        }
//        rc.setIndicatorString(msg + (line != null ? line.target + " " + target : "no line") + " " + progressCountdown);
    }

    private static void resetLine(RobotController rc, MapLocation target) {
        line = new Line(rc.getLocation(), target);
        closest = Integer.MAX_VALUE;
        closestLoc = rc.getLocation();
        progressCountdown = TIME_LIMIT;
    }

    private static void doNotBellmanFord(RobotController rc, MapLocation target) throws GameActionException {
        // build graph
//        boolean[][][] graph = new boolean[5][5][RobotPlayer.directions.length];
        boolean[][] open = new boolean[5][5];
        Direction[][] current = new Direction[5][5];
        int[][] costs = new int[5][5];
        MapLocation bot = rc.getLocation();
        for (int dx = -2; dx <= 2; dx++) {
            for (int dy = -2; dy <= 2; dy++) {
                int graphX = 2 + dx, graphY = 2 + dy;
                MapLocation pos = new MapLocation(bot.x + dx, bot.y + dy);
                // is the location accessible
                try {
                    // we want to consider the bot's current location
                    // if pos is not the bot's current location, then it must be on the map
                    // if pos is not sensible, then we assume that it's accessible
                    // if pos is sensible, then it must not be occupied or non-passable to be accessible
                    if (pos.equals(bot)) {
                        open[graphX][graphY] = true;
                        Direction currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                        costs[graphX][graphY] = 100 * pos.add(currentDir).distanceSquaredTo(target);
                        current[graphX][graphY] = currentDir;
                    } else if (rc.onTheMap(pos)) {
                        if (rc.canSenseLocation(pos)) {
                            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                                open[graphX][graphY] = true;
                                Direction currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                                costs[graphX][graphY] = 100 * pos.add(currentDir).distanceSquaredTo(target);
                                current[graphX][graphY] = rc.senseMapInfo(pos).getCurrentDirection();
                            }
                        } else {
                            open[graphX][graphY] = true;
                            costs[graphX][graphY] = 100 * pos.distanceSquaredTo(target);
                        }
                    }
//                    if (pos.equals(bot) || (rc.onTheMap(pos) && (!rc.canSenseLocation(pos) || (!rc.isLocationOccupied(pos) && rc.sensePassability(pos))))) {
                        // add all the neighbors
    //                    for (int i = 0; i < RobotPlayer.directions.length; i++) {
    //                        Direction dir = RobotPlayer.directions[i];
    //                        int neighborX = graphX + dir.dx, neighborY = graphY + dir.dy;
    //                        MapLocation newPos = pos.add(dir);
    //                        if (neighborX < 5 && neighborX >= 0 && neighborY < 5 && neighborY >= 0
    //                                && (newPos.equals(bot) || (rc.onTheMap(newPos) && (!rc.canSenseLocation(newPos) || (!rc.isLocationOccupied(newPos) && rc.sensePassability(newPos))))))
    //                        {
    //                            // neighbor is accessible
    ////                            System.out.println(pos + " " + dir + " accessible");
    //                            graph[graphX][graphY][i] = true;
    //                        }
    //                    }
//                        open[graphX][graphY] = true;
//                        costs[graphX][graphY] = 100 * pos.distanceSquaredTo(target);
//                    }
                } catch (GameActionException e) {
                    System.out.println(bot + " " + pos);
                }
            }
        }

        // relax the graph 3 times
        for (int i = 0; i <= 3; i++) {
            int[][] newCosts = new int[5][5];
            for (int x = 0; x < 5; x++) {
                for (int y = 0; y < 5; y++) {
                    int min_cost = costs[x][y];
                    for (int dirI = 0; dirI < RobotPlayer.directions.length; dirI++) {
                        Direction dir = RobotPlayer.directions[dirI];
                        int neighborX = x + dir.dx, neighborY = x + dir.dy;
                        if (neighborX < 5 && neighborX >= 0 && neighborY < 5 && neighborY >= 0
                                && open[neighborX][neighborY]
                                && (current[neighborX][neighborY] == null || current[neighborX][neighborY] != dir.opposite())  // don't go into an opposing current
                        ) {
                            min_cost = Math.min(min_cost, costs[neighborX][neighborY] + 1);
                        }
                    }
//                    for (int dirI = 0; dirI < graph[x][y].length; dirI++) {
//                        if (graph[x][y][dirI]) {
//                            // is actually a neighbor
//                            Direction dir = RobotPlayer.directions[dirI];
//                            min_cost = Math.min(min_cost, costs[x + dir.dx][y + dir.dy] + 1);
//                        }
//                    }
                    newCosts[x][y] = min_cost;
                }
            }
            costs = newCosts;
        }

        // find the best direction
        Direction bestDir = Direction.CENTER;
        int bestCost = Integer.MAX_VALUE;
        for (int dirI = 0; dirI < RobotPlayer.directions.length; dirI++) {
            Direction dir = RobotPlayer.directions[dirI];
            int neighborX = 2 + dir.dx, neighborY = 2 + dir.dy;
//            if (graph[2][2][dirI]) {
            if (open[neighborX][neighborY]) {
                // is actually a neighbor
//                Direction dir = RobotPlayer.directions[dirI];
                int neighborCost = costs[2 + dir.dx][2 + dir.dy];
//                System.out.println(neighborCost + " " + bestCost);
                if (neighborCost < bestCost) {
                    bestCost = neighborCost;
                    bestDir = dir;
                }
            }
        }
        rc.setIndicatorString(bestDir + " " + target);

        // move the robot
        if (rc.canMove(bestDir)) {
            rc.move(bestDir);
        }
    }
}

