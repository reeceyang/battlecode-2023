package kaiplayer;

import battlecode.common.*;

public class AmplifierStrategy {

    static int targetLauncher;

    static void runAmplifier(RobotController rc) throws GameActionException {
        Direction dir = Direction.CENTER;
        Team enemyTeam = rc.getTeam().opponent();

        MapLocation loc = null;
        MapLocation hq = null;

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

            for (int i : hqLoc) {
                MapLocation m = Communication.intToLocation(rc, i);
                if ((m.x - rc.getLocation().x) * (m.x - rc.getLocation().x) + (m.y - rc.getLocation().y) * (m.y - rc.getLocation().y) < 2) {
                    hq = m;
                    break;
                }
            }

        }

        if (rc.canSenseRobot(targetLauncher)) {
            dir = rc.getLocation().directionTo(rc.senseRobot(targetLauncher).location); // follow launcher
        } else {
            dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        }
        if (rc.canMove(dir)) {
            rc.move(dir);
        } else {
            RobotPlayer.moveRandom(rc); // TODO: replace parts in this section with Pathing.moveTowards(targetHQ)
        }

        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, enemyTeam);
        for (RobotInfo bot : nearbyEnemies) {
            if (bot.getType() == RobotType.HEADQUARTERS) {

                if (RobotPlayer.xReflect(rc, bot.location) == hqLoc[0]) {
                    Communication.updateHQStatus(rc, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[1] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[1]) {
                    Communication.updateHQStatus(rc, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[2] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[2]) {
                    Communication.updateHQStatus(rc, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[3] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[3]) {
                    Communication.updateHQStatus(rc, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
                } else {
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                }

                if (RobotPlayer.yReflect(rc, bot.location) == hqLoc[0]) {
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0);
                } else if (hqLoc[1] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[1]) {
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
                } else if (hqLoc[2] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[2]) {
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0);
                } else if (hqLoc[3] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[3]) {
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                } else {
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                }

            }
        }

        MapLocation target_xloc = Communication.intToLocation(rc, RobotPlayer.xReflect(rc, hq));
        if (rc.canSenseLocation(target_xloc)) {
            if (rc.canSenseRobotAtLocation((target_xloc))) {
                if (rc.senseRobotAtLocation(target_xloc).type != RobotType.HEADQUARTERS) {
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                }
            } else {
                Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
            }
        }

        MapLocation target_yloc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, hq));
        if (rc.canSenseLocation(target_yloc)) {
            if (rc.canSenseRobotAtLocation((target_yloc))) {
                if (rc.senseRobotAtLocation(target_yloc).type != RobotType.HEADQUARTERS) {
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                }
            } else {
                Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
            }
        }

//        if (rc.senseNearbyIslands().length > 0) {
            // [LATER] write island locations to shared array if not already there
//        }

    }

}
