package v3_2beardedreedling;

import battlecode.common.*;

public class AmplifierStrategy {
    static boolean combatmode = false;
    static MapLocation nextLoc;

    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
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

        RobotInfo[] robots = rc.senseNearbyRobots();
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

        int[] islandIds = rc.senseNearbyIslands();
        for (int id : islandIds) {
            Communication.updateIslandInfo(rc, id);
//            System.out.println("island "+id+" is held by "+Communication.readTeamHoldingIsland(rc, id));
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
            Pathing.moveTowards(rc, nextLoc);
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
                    Communication.updateHQStatus(rc, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[1] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[1]) {
                    rc.setIndicatorString("x2");
                    hqId = 2;
                    Communication.updateHQStatus(rc, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[2] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[2]) {
                    rc.setIndicatorString("x3");
                    hqId = 3;
                    Communication.updateHQStatus(rc, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[3] != 0 && RobotPlayer.xReflect(rc, bot.location) == hqLoc[3]) {
                    rc.setIndicatorString("x4");
                    hqId = 4;
                    Communication.updateHQStatus(rc, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0);
                } else {
                    rc.setIndicatorString("nx");
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                }

                if (RobotPlayer.yReflect(rc, bot.location) == hqLoc[0]) {
                    rc.setIndicatorString("y1");
                    hqId = 1;
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
                } else if (hqLoc[1] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[1]) {
                    rc.setIndicatorString("y2");
                    hqId = 2;
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0);
                } else if (hqLoc[2] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[2]) {
                    rc.setIndicatorString("y3");
                    hqId = 3;
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0);
                } else if (hqLoc[3] != 0 && RobotPlayer.yReflect(rc, bot.location) == hqLoc[3]) {
                    rc.setIndicatorString("y4");
                    hqId = 4;
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0);
                } else {
                    rc.setIndicatorString("ny");
                    Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
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

        // TODO: implement nx/ny/nr reporting for island locations as well

        if (Communication.findSymmetry(rc) == 0) {
            for (int i : hqLoc) {
                if (i != 0) {
                    MapLocation hq = Communication.intToLocation(rc, i);
                    MapLocation target_xloc = Communication.intToLocation(rc, RobotPlayer.xReflect(rc, hq));
                    if (rc.canSenseLocation(target_xloc)) {
                        if (rc.canSenseRobotAtLocation((target_xloc))) {
                            if (rc.senseRobotAtLocation(target_xloc).type != RobotType.HEADQUARTERS || rc.senseRobotAtLocation(target_xloc).team == rc.getTeam()) {
                                rc.setIndicatorString("hq nx");
                                Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                            }
                        } else {
                            rc.setIndicatorString("hq nx");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                        }
                    }
                    MapLocation target_yloc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, hq));
                    if (rc.canSenseLocation(target_yloc)) {
                        if (rc.canSenseRobotAtLocation((target_yloc))) {
                            if (rc.senseRobotAtLocation(target_yloc).type != RobotType.HEADQUARTERS || rc.senseRobotAtLocation(target_yloc).team == rc.getTeam()) {
                                rc.setIndicatorString("hq ny");
                                Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                            }
                        } else {
                            rc.setIndicatorString("hq ny");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                        }
                    }
                    MapLocation target_rloc = Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, hq));
                    if (rc.canSenseLocation(target_rloc)) {
                        if (rc.canSenseRobotAtLocation((target_rloc))) {
                            if (rc.senseRobotAtLocation(target_rloc).type != RobotType.HEADQUARTERS || rc.senseRobotAtLocation(target_rloc).team == rc.getTeam()) {
                                rc.setIndicatorString("hq nr");
                                Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                            }
                        } else {
                            rc.setIndicatorString("hq nr");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                        }
                    }
                }
            }

            MapLocation[] adWellLocs = new MapLocation[]{null, null, null, null, null};
            MapLocation[] manaWellLocs = new MapLocation[]{null, null, null, null, null};
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
                                Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                            }
                        } else {
                            rc.setIndicatorString("adwell nx");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                        }
                    }
                    MapLocation target_yloc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, adWell));
                    if (rc.canSenseLocation(target_yloc)) {
                        WellInfo well = rc.senseWell(target_yloc);
                        if (well != null) {
                            if (well.getResourceType() != ResourceType.ADAMANTIUM) {
                                rc.setIndicatorString("adwell ny");
                                Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                            }
                        } else {
                            rc.setIndicatorString("adwell ny");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                        }
                    }
                    MapLocation target_rloc = Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, adWell));
                    if (rc.canSenseLocation(target_rloc)) {
                        WellInfo well = rc.senseWell(target_rloc);
                        if (well != null) {
                            if (well.getResourceType() != ResourceType.ADAMANTIUM) {
                                rc.setIndicatorString("adwell nr");
                                Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                            }
                        } else {
                            rc.setIndicatorString("adwell nr");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
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
                                Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                            }
                        } else {
                            rc.setIndicatorString("mana nx");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                        }
                    }
                    MapLocation target_yloc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, manaWell));
                    if (rc.canSenseLocation(target_yloc)) {
                        WellInfo well = rc.senseWell(target_yloc);
                        if (well != null) {
                            if (well.getResourceType() != ResourceType.MANA) {
                                rc.setIndicatorString("mana ny");
                                Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                            }
                        } else {
                            rc.setIndicatorString("mana ny");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                        }
                    }
                    MapLocation target_rloc = Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, manaWell));
                    if (rc.canSenseLocation(target_rloc)) {
                        WellInfo well = rc.senseWell(target_rloc);
                        if (well != null) {
                            if (well.getResourceType() != ResourceType.MANA) {
                                rc.setIndicatorString("mana nr");
                                Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                            }
                        } else {
                            rc.setIndicatorString("mana nr");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
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
                            System.out.println(target_xloc);
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0);
                        }
                    }
                    MapLocation target_yloc = Communication.intToLocation(rc, RobotPlayer.yReflect(rc, Communication.readIslandLocation(rc, idx - Communication.STARTING_ISLAND_IDX)));
                    if (rc.canSenseLocation(target_yloc)) {
                        int island = rc.senseIsland(target_yloc);
                        if (island == -1) {
                            rc.setIndicatorLine(target_yloc, Communication.readIslandLocation(rc, idx - Communication.STARTING_ISLAND_IDX),0, 0, 0);
                            rc.setIndicatorString("island ny");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0);
                        }
                    }
                    MapLocation target_rloc = Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, Communication.readIslandLocation(rc, idx - Communication.STARTING_ISLAND_IDX)));
                    if (rc.canSenseLocation(target_rloc)) {
                        int island = rc.senseIsland(target_rloc);
                        if (island == -1) {
                            rc.setIndicatorLine(target_rloc, Communication.readIslandLocation(rc, idx - Communication.STARTING_ISLAND_IDX),0, 0, 0);
                            rc.setIndicatorString("island nr");
                            Communication.updateHQStatus(rc, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
                        }
                    }
                }
            }
        }
    }
}
