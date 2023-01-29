package v3_3_1beardedreedling;

import battlecode.common.*;

public class AmplifierStrategy {
    static boolean combatmode = false;
    static boolean bugOverride = false;
    static MapLocation nextLoc;

    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runAmplifier(RobotController rc) throws GameActionException {
        Team enemyTeam = rc.getTeam().opponent();

        for (WellInfo well : rc.senseNearbyWells()) {
            Communication.addWell(rc, well.getMapLocation(), well.getResourceType());
        }

        int[] hqLoc = new int[]{Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 2)),
                Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 3)),
                Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 4)),
                Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 5))};

        RobotInfo[] robots = rc.senseNearbyRobots();
        bugOverride = robots.length > 10;
        
        // report any enemies, and either follow friendlies or avoid enemies
        nextLoc = Pathing.reportAndPlaySafe(rc, robots, 1);

        if (nextLoc == null) {
            // go patrol a nearby well or island
            WellInfo[] wells = rc.senseNearbyWells();
            if (wells.length > 0) {
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
                if (closestIslandLoc != null) {
                    nextLoc = closestIslandLoc;
                }
            }
        }

        Communication.clearObsoleteEnemies(rc);
        Communication.tryWriteMessages(rc);

        // Execute Movement
        if (nextLoc == null) {
            RobotPlayer.moveRandom(rc);
        } else {
            Direction overrideDirection = Pathing.avoidCongestedHQ(rc);
            if (overrideDirection != Direction.CENTER) {
                // don't go anywhere near a congested HQ; this would interfere with carriers
                nextLoc = rc.getLocation().add(overrideDirection).add(overrideDirection);
            }
            Pathing.moveTowards(rc, nextLoc, bugOverride);
        }


        /* -------------------------------- */

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
                    for (int i = 2; i < 6; i++) {
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
                        if (rc.senseRobotAtLocation(target_yloc).type != RobotType.HEADQUARTERS || rc.senseRobotAtLocation(target_yloc).team == rc.getTeam()) {
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
