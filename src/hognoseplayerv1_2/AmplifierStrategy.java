package hognoseplayerv1_2;

import battlecode.common.*;

public class AmplifierStrategy {

    static void runAmplifier(RobotController rc) throws GameActionException {

        Team enemyTeam = rc.getTeam().opponent();

        rc.senseNearbyWells();
        for (WellInfo well : rc.senseNearbyWells()) {
            Communication.addWell(rc, well.getResourceType(), well.getMapLocation());
        }

        int[] hqLoc = new int[]{Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 2)),
                Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 3)),
                Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 4)),
                Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 5))};
//
//        int target=0;
//
//        int[] dists = new int[]{3600, 3600, 3600, 3600};
//        if (Communication.readHQStatus(rc, "nx") == 1) {
//            if (Communication.readHQStatus(rc, "ny") == 1) {
//                // Go to closest verified enemy HQ location
//
//                for (int i=2; i<6; i++) {
//                    if (Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, i)) != 0) { //  && Communication.readNeedsLauncher(rc, i)==1
//                        dists[i-2] = RobotPlayer.distSquaredLoc(rc.getLocation(), Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, Communication.readHeadquarterLocation(rc, i))));
//                    }
//                }
//
//                int min1 = Math.min(dists[0], dists[1]);
//                int min2 = Math.min(dists[2], dists[3]);
//                int min = Math.min(min1, min2);
//
//                int targetId = 0;
//                if (min != 3600) {
//                    for (int i = 0; i < 4; i++) {
//                        if (min == dists[i]) {
//                            target = RobotPlayer.diagReflect(rc, Communication.readHeadquarterLocation(rc, i+2));
//                            targetId = i;
//                            break;
//                        }
//                    }
//                }
//            } else {
//                // TODO: unexplored potential enemy HQ location - if it gets within range to see that there is no HQ there, go to new unexplored enemy HQ location (in case of amplifier death)
//                target = RobotPlayer.yReflect(rc, Communication.readHeadquarterLocation(rc, 2));
//            }
//        } else {
//            // TODO: closest unexplored potential enemy HQ location
//            target = RobotPlayer.xReflect(rc, Communication.readHeadquarterLocation(rc, 2));
//        }
//        Pathing.moveTowards(rc, Communication.intToLocation(rc, target));

        RobotPlayer.moveRandom(rc);

        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, enemyTeam);
        for (RobotInfo bot : nearbyEnemies) {
            if (bot.getType() == RobotType.HEADQUARTERS) {

                int hqId = 0;

                // report its location to array

                if (RobotPlayer.xReflect(rc, bot.location) == hqLoc[0]) {
                    rc.setIndicatorString("x1");
                    hqId = 1;
                    Communication.updateHQStatus(rc, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[1] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[1]) {
                    rc.setIndicatorString("x2");
                    hqId = 2;
                    Communication.updateHQStatus(rc, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[2] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[2]) {
                    rc.setIndicatorString("x3");
                    hqId = 3;
                    Communication.updateHQStatus(rc, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[3] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[3]) {
                    rc.setIndicatorString("x4");
                    hqId = 4;
                    Communication.updateHQStatus(rc, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
                } else {
                    rc.setIndicatorString("nx");
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                }

                if (RobotPlayer.yReflect(rc, bot.location) == hqLoc[0]) {
                    rc.setIndicatorString("y1");
                    hqId = 1;
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0);
                } else if (hqLoc[1] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[1]) {
                    rc.setIndicatorString("y2");
                    hqId = 2;
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
                } else if (hqLoc[2] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[2]) {
                    rc.setIndicatorString("y3");
                    hqId = 3;
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0);
                } else if (hqLoc[3] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[3]) {
                    rc.setIndicatorString("y4");
                    hqId = 4;
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                } else {
                    rc.setIndicatorString("ny");
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                }

                if (hqId == 0) {
                    int hqInt = RobotPlayer.diagReflect(rc, bot.location);
                    for (int i = 2; i < 6; i ++) {
                        if (hqInt == Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, i))) {
                            hqId = i;
                            break;
                        }
                    }
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
    }
}
