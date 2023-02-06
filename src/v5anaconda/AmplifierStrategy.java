package v5anaconda;

import battlecode.common.*;

public class AmplifierStrategy {
    static boolean combatmode = false;
    static MapLocation nextLoc;
    static MapLocation[] emptyClouds = new MapLocation[]{null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null};
    static int emptyIndex = 0;
    static MapLocation[] adWellLocs = new MapLocation[32];
    static MapLocation[] manaWellLocs = new MapLocation[32];
    /**
     * Run a single turn for an Amplifier.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runAmplifier(RobotController rc) throws GameActionException {
        Team enemyTeam = rc.getTeam().opponent();

        WellInfo[] wells = rc.senseNearbyWells();
        for (WellInfo well : wells) {
            Communication.addWell(rc, well.getResourceType(), well.getMapLocation());
        }

        int[] hqLoc = new int[]{Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 2)),
                Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 3)),
                Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 4)),
                Communication.locationToInt(rc, Communication.readHeadquarterLocation(rc, 5))};

        RobotInfo[] robots = rc.senseNearbyRobots();
        // report any enemies, and either follow friendlies or avoid enemies
        Report report = Pathing.reportAndPlaySafe(rc, robots, 1);
        nextLoc = report != null ? report.nextLoc : null;

        if (nextLoc == null) {
            // go patrol a nearby well or island
            MapLocation[] clouds = rc.senseNearbyCloudLocations();
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
            } else if (clouds.length > 0) {
                int closestDistSq = 4000;
                MapLocation closestCloudLoc = null;
                boolean isEmpty = false;
                for (MapLocation cloud : clouds) {
                    for (MapLocation emptyCloud : emptyClouds) {
                        if (emptyCloud == null) break;
                        if (cloud.equals(emptyCloud)) {
                            isEmpty = true;
                            break;
                        }
                    }
                    if (!isEmpty) {
                        int d2 = rc.getLocation().distanceSquaredTo(cloud);
                        if (d2 < closestDistSq) {
                            closestDistSq = d2;
                            closestCloudLoc = cloud;
                        }
                    }
                }
                nextLoc = closestCloudLoc;
            } else {
                MapLocation closestIslandLoc = Communication.getClosestIsland(rc);
                if (closestIslandLoc != null) {
                    nextLoc = closestIslandLoc;
                }
            }
        }

        if (rc.senseCloud(rc.getLocation())) {
            for (MapLocation loc : rc.getAllLocationsWithinRadiusSquared(rc.getLocation(), GameConstants.CLOUD_VISION_RADIUS_SQUARED)) {
                if (rc.senseCloud(loc)) {
                    boolean isEmpty = false;
                    for (MapLocation emptyCloud : emptyClouds) {
                        if (emptyCloud == null) break;
                        if (loc.equals(emptyCloud)) {
                            isEmpty = true;
                            break;
                        }
                    }
                    if (!isEmpty && rc.senseWell(loc) == null && rc.senseIsland(loc) == -1 && emptyIndex < emptyClouds.length) {
                        emptyClouds[emptyIndex] = loc;
//                        System.out.println(emptyIndex + " " + loc);
                        emptyIndex++;
                    }
                }
            }
        }

        int[] islandIds = rc.senseNearbyIslands();
        for (int id : islandIds) {
            Communication.updateIslandInfo(rc, id);
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
            Pathing.moveTowards(rc, nextLoc, false, false);
        }


        /* -------------------------------- */

        for (RobotInfo bot : robots) {
            if (bot.getType() == RobotType.HEADQUARTERS && bot.getTeam() == enemyTeam) {

                int hqId = 0;

                // report its location to array
                int xReflect = RobotPlayer.xReflect(rc, bot.location);
                int yReflect = RobotPlayer.yReflect(rc, bot.location);

                // TODO: Make into a switch (xReflect) and switch (yReflect) statement
                if (xReflect == hqLoc[0]) {
                    rc.setIndicatorString("x1");
                    hqId = 1;
                    Communication.updateSymmetryInfo(rc, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (xReflect == hqLoc[1]) {
                    rc.setIndicatorString("x2");
                    hqId = 2;
                    Communication.updateSymmetryInfo(rc, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (xReflect == hqLoc[2]) {
                    rc.setIndicatorString("x3");
                    hqId = 3;
                    Communication.updateSymmetryInfo(rc, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (xReflect == hqLoc[3]) {
                    rc.setIndicatorString("x4");
                    hqId = 4;
                    Communication.updateSymmetryInfo(rc, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0);
                } else {
                    rc.setIndicatorString("nx");
                    Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                }

                if (RobotPlayer.yReflect(rc, bot.location) == hqLoc[0]) {
                    rc.setIndicatorString("y1");
                    hqId = 1;
                    Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
                } else if (yReflect == hqLoc[1]) {
                    rc.setIndicatorString("y2");
                    hqId = 2;
                    Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0);
                } else if (yReflect == hqLoc[2]) {
                    rc.setIndicatorString("y3");
                    hqId = 3;
                    Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
                } else if (yReflect == hqLoc[3]) {
                    rc.setIndicatorString("y4");
                    hqId = 4;
                    Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0);
                } else {
                    rc.setIndicatorString("ny");
                    Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
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

        if (Communication.findSymmetry(rc) == 0) {
            for (int i : hqLoc) {
                if (i != 0) {
                    MapLocation hq = Communication.intToLocation(rc, i);
                    MapLocation target_xloc = Communication.intToLocation(rc, RobotPlayer.xReflect(rc, hq));
                    if (rc.canSenseLocation(target_xloc)) {
                        if (rc.canSenseRobotAtLocation((target_xloc))) {
                            if (rc.senseRobotAtLocation(target_xloc).type != RobotType.HEADQUARTERS || rc.senseRobotAtLocation(target_xloc).team == rc.getTeam()) {
                                rc.setIndicatorString("hq nx");
                                Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                            }
                        } else {
                            rc.setIndicatorString("hq nx");
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                        }
                    }
                    MapLocation target_yloc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, hq));
                    if (rc.canSenseLocation(target_yloc)) {
                        if (rc.canSenseRobotAtLocation((target_yloc))) {
                            if (rc.senseRobotAtLocation(target_yloc).type != RobotType.HEADQUARTERS || rc.senseRobotAtLocation(target_yloc).team == rc.getTeam()) {
                                rc.setIndicatorString("hq ny");
                                Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                            }
                        } else {
                            rc.setIndicatorString("hq ny");
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                        }
                    }
                    MapLocation target_rloc = Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, hq));
                    if (rc.canSenseLocation(target_rloc)) {
                        if (rc.canSenseRobotAtLocation((target_rloc))) {
                            if (rc.senseRobotAtLocation(target_rloc).type != RobotType.HEADQUARTERS || rc.senseRobotAtLocation(target_rloc).team == rc.getTeam()) {
                                rc.setIndicatorString("hq nr");
                                Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                            }
                        } else {
                            rc.setIndicatorString("hq nr");
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                        }
                    }
                }
            }

            for (int idx = Communication.STARTING_WELL_IDX; idx < GameConstants.SHARED_ARRAY_LENGTH; idx++) {
                if (Communication.getWellType(rc, idx) == ResourceType.ADAMANTIUM) {
                    adWellLocs[idx - Communication.STARTING_WELL_IDX] = Communication.readWellLocation(rc, idx);
                } else if (Communication.getWellType(rc, idx) == ResourceType.MANA) {
                    manaWellLocs[idx - Communication.STARTING_WELL_IDX] = Communication.readWellLocation(rc, idx);
                }
            }

            for (MapLocation adWell : adWellLocs) {
                if (adWell != null) {
                    MapLocation target_xloc = Communication.intToLocation(rc, RobotPlayer.xReflect(rc, adWell));
                    if (rc.canSenseLocation(target_xloc)) {
                        WellInfo well = rc.senseWell(target_xloc);
                        if (well != null) {
                            if (well.getResourceType() != ResourceType.ADAMANTIUM) {
                                rc.setIndicatorString("adwell nx");
                                Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                            }
                        } else {
                            rc.setIndicatorString("adwell nx");
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                        }
                    }
                    MapLocation target_yloc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, adWell));
                    if (rc.canSenseLocation(target_yloc)) {
                        WellInfo well = rc.senseWell(target_yloc);
                        if (well != null) {
                            if (well.getResourceType() != ResourceType.ADAMANTIUM) {
                                rc.setIndicatorString("adwell ny");
                                Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                            }
                        } else {
                            rc.setIndicatorString("adwell ny");
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                        }
                    }
                    MapLocation target_rloc = Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, adWell));
                    if (rc.canSenseLocation(target_rloc)) {
                        WellInfo well = rc.senseWell(target_rloc);
                        if (well != null) {
                            if (well.getResourceType() != ResourceType.ADAMANTIUM) {
                                rc.setIndicatorString("adwell nr");
                                Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                            }
                        } else {
                            rc.setIndicatorString("adwell nr");
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                        }
                    }
                }
            }

            for (MapLocation manaWell : manaWellLocs) {
                if (manaWell != null) {
                    MapLocation target_xloc = Communication.intToLocation(rc, RobotPlayer.xReflect(rc, manaWell));
                    if (rc.canSenseLocation(target_xloc)) {
                        WellInfo well = rc.senseWell(target_xloc);
                        if (well != null) {
                            if (well.getResourceType() != ResourceType.MANA) {
                                rc.setIndicatorString("mana nx");
                                Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                            }
                        } else {
                            rc.setIndicatorString("mana nx");
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                        }
                    }
                    MapLocation target_yloc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, manaWell));
                    if (rc.canSenseLocation(target_yloc)) {
                        WellInfo well = rc.senseWell(target_yloc);
                        if (well != null) {
                            if (well.getResourceType() != ResourceType.MANA) {
                                rc.setIndicatorString("mana ny");
                                Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                            }
                        } else {
                            rc.setIndicatorString("mana ny");
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                        }
                    }
                    MapLocation target_rloc = Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, manaWell));
                    if (rc.canSenseLocation(target_rloc)) {
                        WellInfo well = rc.senseWell(target_rloc);
                        if (well != null) {
                            if (well.getResourceType() != ResourceType.MANA) {
                                rc.setIndicatorString("mana nr");
                                Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                            }
                        } else {
                            rc.setIndicatorString("mana nr");
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                        }
                    }
                }
            }

            for (int idx = Communication.STARTING_ISLAND_IDX; idx < Communication.STARTING_ENEMY_IDX; idx++) {
                if (rc.readSharedArray(idx) != 0) {
                    MapLocation target_xloc = Communication.intToLocation(rc, RobotPlayer.xReflect(rc, Communication.readIslandLocation(rc, idx - Communication.STARTING_ISLAND_IDX)));
                    if (rc.canSenseLocation(target_xloc)) {
                        int island = rc.senseIsland(target_xloc);
                        if (island == -1) {
                            rc.setIndicatorLine(target_xloc, Communication.readIslandLocation(rc, idx - Communication.STARTING_ISLAND_IDX),0, 0, 0);
//                            System.out.println(target_xloc);
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                        }
                    }
                    MapLocation target_yloc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, Communication.readIslandLocation(rc, idx - Communication.STARTING_ISLAND_IDX)));
                    if (rc.canSenseLocation(target_yloc)) {
                        int island = rc.senseIsland(target_yloc);
                        if (island == -1) {
                            rc.setIndicatorLine(target_yloc, Communication.readIslandLocation(rc, idx - Communication.STARTING_ISLAND_IDX),0, 0, 0);
                            rc.setIndicatorString("island ny");
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                        }
                    }
                    MapLocation target_rloc = Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, Communication.readIslandLocation(rc, idx - Communication.STARTING_ISLAND_IDX)));
                    if (rc.canSenseLocation(target_rloc)) {
                        int island = rc.senseIsland(target_rloc);
                        if (island == -1) {
                            rc.setIndicatorLine(target_rloc, Communication.readIslandLocation(rc, idx - Communication.STARTING_ISLAND_IDX),0, 0, 0);
                            rc.setIndicatorString("island nr");
                            Communication.updateSymmetryInfo(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                        }
                    }
                }
            }
        }
    }
}
