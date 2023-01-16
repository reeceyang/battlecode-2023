package kaiplayer;

import battlecode.common.*;

public class AmplifierStrategy {

    static int targetLauncher;

    static void runAmplifier(RobotController rc) throws GameActionException {
        Direction dir = Direction.CENTER;
        Team enemyTeam = rc.getTeam().opponent();

        int[] hqLoc = new int[]{rc.readSharedArray(1), rc.readSharedArray(2), rc.readSharedArray(3), rc.readSharedArray(4)};

        if (RobotPlayer.turnCount == 1) {
            // identify the closest launcher by ID, set it to targetLauncher
            RobotInfo[] nearestBots = rc.senseNearbyRobots(2);
            for (RobotInfo bot : nearestBots) {
                if (bot.getType() == RobotType.LAUNCHER) { // TODO: very scuffed, get HQ to write launcher ID to shared array instead?
                    targetLauncher = bot.getID();
                    break;
                }
            }
        }

        if (rc.canSenseRobot(targetLauncher)) {
            Pathing.moveTowards(rc, rc.senseRobot(targetLauncher).location); // follow launcher
        } else {
            RobotPlayer.moveRandom(rc);
        }

        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, enemyTeam);
        for (RobotInfo bot : nearbyEnemies) {
            if (bot.getType() == RobotType.HEADQUARTERS) {

                if (RobotPlayer.xReflect(rc, bot.location) == hqLoc[0]) {
                    rc.setIndicatorString("x1");
                    Communication.updateHQStatus(rc, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[1] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[1]) {
                    rc.setIndicatorString("x2");
                    Communication.updateHQStatus(rc, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[2] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[2]) {
                    rc.setIndicatorString("x3");
                    Communication.updateHQStatus(rc, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[3] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[3]) {
                    rc.setIndicatorString("x4");
                    Communication.updateHQStatus(rc, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
                } else {
                    rc.setIndicatorString("nx");
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                }

                if (RobotPlayer.yReflect(rc, bot.location) == hqLoc[0]) {
                    rc.setIndicatorString("y1");
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0);
                } else if (hqLoc[1] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[1]) {
                    rc.setIndicatorString("y2");
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
                } else if (hqLoc[2] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[2]) {
                    rc.setIndicatorString("y3");
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0);
                } else if (hqLoc[3] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[3]) {
                    rc.setIndicatorString("y4");
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                } else {
                    rc.setIndicatorString("ny");
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                }

            }
        }

        for (int i : hqLoc) {
            if (i != 0) {
                MapLocation hq = Communication.intToLocation(rc, i);
                MapLocation target_xloc = Communication.intToLocation(rc, RobotPlayer.xReflect(rc, hq));
                if (rc.canSenseLocation(target_xloc)) {
                    if (rc.canSenseRobotAtLocation((target_xloc))) {
                        if (rc.senseRobotAtLocation(target_xloc).type != RobotType.HEADQUARTERS || rc.senseRobotAtLocation(target_xloc).team == rc.getTeam()) {
                            rc.setIndicatorString("nx");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                        }
                    } else {
                        rc.setIndicatorString("nx");
                        Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                    }
                }
                MapLocation target_yloc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, hq));
                if (rc.canSenseLocation(target_yloc)) {
                    if (rc.canSenseRobotAtLocation((target_yloc))) {
                        if (rc.senseRobotAtLocation(target_yloc).type != RobotType.HEADQUARTERS || rc.senseRobotAtLocation(target_xloc).team == rc.getTeam()) {
                            rc.setIndicatorString("ny");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                        }
                    } else {
                        rc.setIndicatorString("ny");
                        Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                    }
                }
            }
        }

        // TODO: report enemy clusters

//        if (rc.senseNearbyIslands().length > 0) {
            // [LATER] write island locations to shared array if not already there
//        }

    }

}
