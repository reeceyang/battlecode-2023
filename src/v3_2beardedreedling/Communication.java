package v3_2beardedreedling;

import java.util.List;

import java.util.ArrayList;

import battlecode.common.*;

class Message {
    public int idx;
    public int value;
    public int turnAdded;

    Message (int idx, int value, int turnAdded) {
        this.idx = idx;
        this.value = value;
        this.turnAdded = turnAdded;
    }
}

class Communication {

    private static final int OUTDATED_TURNS_AMOUNT = 30;
    private static final int AREA_RADIUS = RobotType.CARRIER.visionRadiusSquared;

    // Maybe you want to change this based on exact amounts which you can get on turn 1
    private static final int STARTING_HQ_IDX = 2;
    public static final int STARTING_ISLAND_IDX = GameConstants.MAX_STARTING_HEADQUARTERS + STARTING_HQ_IDX;
    public static final int STARTING_ENEMY_IDX = GameConstants.MAX_NUMBER_ISLANDS + GameConstants.MAX_STARTING_HEADQUARTERS + STARTING_HQ_IDX;
    public static final int STARTING_WELL_IDX = 59; // there is no reason for this
    private static final int COUNT_IDX = 0;
    private static final int SYMMETRY_IDX = 1;

    private static final int TOTAL_BITS = 16;
    private static final int MAPLOC_BITS = 12;
    private static final int TEAM_BITS = 1;
    private static final int HEALTH_BITS = 3;
    private static final int HEALTH_SIZE = (int) Math.ceil(Anchor.ACCELERATING.totalHealth / 8.0);
    private static final int HQ_COUNT_BITS = 2;
    private static final int RESOURCE_TYPE_BITS = 2;
    private static final int CONGESTION_BITS = 1;
    private static int headquarterCount = 0;

    static final int HQ_COUNT_MASK = 0b11;
    static final int MA_AD_MASK = 0b1;
    static final int HQ_CONGEST_MASK = 0b1000;
    static final int RESOURCE_TYPE_MASK = 0b11;
    static final int CONGESTION_MASK = 0b100;

    private static List<Message> messagesQueue = new ArrayList<>();
    public static MapLocation[] headquarterLocs = new MapLocation[GameConstants.MAX_STARTING_HEADQUARTERS];



    static int bitPackHQInfo(RobotController rc, boolean s1, boolean s2, boolean s4, boolean s8) throws GameActionException {
        int hqInt = locationToInt(rc, rc.getLocation());
        hqInt = hqInt << (TOTAL_BITS - MAPLOC_BITS);
        if (s1) { hqInt += 0b1; } // flag true if needs Mana, false if needs Adamantium
        if (s2) { hqInt += 0b10; } // flag for
        if (s4) { hqInt += 0b100; } // flag for
        if (s8) { hqInt += 0b1000; } // flag true if congested
        return hqInt;
    }

    static int increaseHeadquarterCount(RobotController rc) throws GameActionException {
        int curr = rc.readSharedArray(COUNT_IDX);
        headquarterCount = (curr & HQ_COUNT_MASK) + 1;
        rc.writeSharedArray(COUNT_IDX, curr);
        return headquarterCount;
    }

    static int readHeadquarterCount(RobotController rc) throws GameActionException {
        return rc.readSharedArray(COUNT_IDX) & HQ_COUNT_MASK;
    }

    static void addHeadquarter(RobotController rc) throws GameActionException {
        for (int i = STARTING_HQ_IDX; i < GameConstants.MAX_STARTING_HEADQUARTERS + STARTING_HQ_IDX; i++) {
            if (rc.readSharedArray(i) == 0) {
                System.out.println("writing " + i + " to " + bitPackHQInfo(rc, false, false, false, false));
                rc.writeSharedArray(i, bitPackHQInfo(rc, false, false, false, false));
                break;
            }
        }
    }

    static void updateHeadquarterInfo(RobotController rc) throws GameActionException {
        for (int i = STARTING_HQ_IDX; i < GameConstants.MAX_STARTING_HEADQUARTERS + STARTING_HQ_IDX; i++) {
            headquarterLocs[i-STARTING_HQ_IDX] = readHeadquarterLocation(rc, i);
            System.out.println(i + " " + readHeadquarterLocation(rc, i));
            if (rc.readSharedArray(i) == 0) {
                break;
            }
        }
    }

    static MapLocation readHeadquarterLocation(RobotController rc, int idx) throws GameActionException {
        int hqInt = rc.readSharedArray(idx);
        int locInt = hqInt >> (TOTAL_BITS - MAPLOC_BITS);
        if (locInt != 0) {
            return intToLocation(rc, locInt);
        } else {
            return null;
        }
    }

    static boolean checkHeadquarterManaNeed(RobotController rc, int idx) throws GameActionException {
        int hqInt = rc.readSharedArray(idx);
        if (hqInt == 0) {
            return false;
        }
        return ((hqInt & MA_AD_MASK) > 0);
    }

    static boolean checkHeadquarterCongestion(RobotController rc, int idx) throws GameActionException {
        int hqInt = rc.readSharedArray(idx);
        if (hqInt == 0) {
            return false;
        }
        return ((hqInt & HQ_CONGEST_MASK) > 0);
    }

    static int getIdxHQbyLocation(RobotController rc, MapLocation loc) throws GameActionException {
        for (int i = STARTING_HQ_IDX; i < GameConstants.MAX_STARTING_HEADQUARTERS + STARTING_HQ_IDX; i++) {
            if (loc.equals(readHeadquarterLocation(rc, i))) {
                return i;
            }
        }
        return 0;
    }

    static void updateHQStatus(RobotController rc, int x1, int x2, int x3, int x4, int y1, int y2, int y3, int y4, int nx, int ny, int nr) {
        try {
            int val = (x1*0b10000000000 + x2*0b1000000000 + x3*0b100000000 + x4*0b10000000 + y1*0b1000000 + y2*0b100000 + y3*0b10000 + y4*0b1000 + nx*0b100 + ny*0b10 + nr*0b1) | rc.readSharedArray(SYMMETRY_IDX);
            rc.writeSharedArray(SYMMETRY_IDX, val);
        } catch (GameActionException e) {}
    }

    static int readHQStatus(RobotController rc, String j) {
        try {
            switch (j) {
                case "x1": return (rc.readSharedArray(SYMMETRY_IDX) >> (10));
                case "x2": return (rc.readSharedArray(SYMMETRY_IDX) >> (9)) % 0b10;
                case "x3": return (rc.readSharedArray(SYMMETRY_IDX) >> (8)) % 0b10;
                case "x4": return (rc.readSharedArray(SYMMETRY_IDX) >> (7)) % 0b10;
                case "y1": return (rc.readSharedArray(SYMMETRY_IDX) >> (6)) % 0b10;
                case "y2": return (rc.readSharedArray(SYMMETRY_IDX) >> (5)) % 0b10;
                case "y3": return (rc.readSharedArray(SYMMETRY_IDX) >> (4)) % 0b10;
                case "y4": return (rc.readSharedArray(SYMMETRY_IDX) >> (3)) % 0b10;
                case "nx": return (rc.readSharedArray(SYMMETRY_IDX) >> (2)) % 0b10;
                case "ny": return (rc.readSharedArray(SYMMETRY_IDX) >> (1)) % 0b10;
                case "nr": return (rc.readSharedArray(SYMMETRY_IDX) >> (0)) % 0b10;
            }
            return 2;
        } catch (GameActionException e) {
            return 2;
        }
    }

    static void tryWriteMessages(RobotController rc) throws GameActionException {
        messagesQueue.removeIf(msg -> msg.turnAdded + OUTDATED_TURNS_AMOUNT < RobotPlayer.turnCount);
        // Can always write (0, 0), so just checks are we in range to write
        if (rc.canWriteSharedArray(0, 0)) {
            while (messagesQueue.size() > 0 ) {
                Message msg = messagesQueue.remove(0);
                if (rc.canWriteSharedArray(msg.idx, msg.value)) {
                    rc.writeSharedArray(msg.idx, msg.value);
                }
            }
        }
    }

//    static void updateIslandCount(RobotController rc) throws GameActionException {
//        rc.writeSharedArray(COUNT_IDX, rc.getIslandCount() >> HQ_COUNT_BITS);
//    }

    static void updateIslandInfo(RobotController rc, int id) throws GameActionException {
//        for (int i = STARTING_HQ_IDX; i < GameConstants.MAX_STARTING_HEADQUARTERS + STARTING_HQ_IDX; i++) {
//            headquarterLocs[i-STARTING_HQ_IDX] = readHeadquarterLocation(rc, i);
//        }
//        MapLocation closestIslandLoc = null;
//        int closestDistance = -1;
        MapLocation[] islandLocs = rc.senseNearbyIslandLocations(id);
//        for (MapLocation loc : islandLocs) {
//            int distance = rc.getLocation().distanceSquaredTo(loc);
//            if (closestIslandLoc == null || distance < closestDistance) {
//                closestDistance = distance;
//                closestIslandLoc = loc;
//            }
//        }
        if (islandLocs[0] != null) {
            // Remember reading is cheaper than writing so we don't want to write without knowing if it's helpful
            int idx = id + STARTING_ISLAND_IDX;
            int oldIslandValue = rc.readSharedArray(idx);
            int updatedIslandValue = bitPackIslandInfo(rc, id, islandLocs[0]);
            if (oldIslandValue != updatedIslandValue) {
//                if (rc.getType() == RobotType.AMPLIFIER) {
//                    rc.writeSharedArray(idx, updatedIslandValue);
//                    System.out.println(oldIslandValue + " " + updatedIslandValue);
//                } else {
                    Message msg = new Message(idx, updatedIslandValue, RobotPlayer.turnCount);
                    messagesQueue.add(msg);
//                }
            }
        }
    }

    static int bitPackIslandInfo(RobotController rc, int islandId, MapLocation closestLoc) {
        int islandInt = locationToInt(rc, closestLoc);
        islandInt = islandInt << (TOTAL_BITS - MAPLOC_BITS);
        try {
            int teamHolding = 0;
            if (rc.senseTeamOccupyingIsland(islandId) == Team.B) teamHolding=1;
            islandInt += teamHolding << (TOTAL_BITS - MAPLOC_BITS - TEAM_BITS);
            int islandHealth = rc.senseAnchorPlantedHealth(islandId);
            int healthEncoding = (int) Math.ceil((double) islandHealth / HEALTH_SIZE);
            islandInt += healthEncoding;
            return islandInt;
        } catch (GameActionException e) {
            return islandInt;
        }
    }

    static Team readTeamHoldingIsland(RobotController rc, int islandId) {
        try {
            islandId = islandId + STARTING_ISLAND_IDX;
            int islandInt = rc.readSharedArray(islandId);
            int healthMask = 0b111;
            int health = islandInt & healthMask;
            int team = (islandInt >> HEALTH_BITS) & 0b1;
            if (health > 0) {
                switch (team) {
                    case 0: return Team.A;
                    case 1: return Team.B;
                }
            }
        } catch (GameActionException e) {}
        return Team.NEUTRAL;
    }

    static MapLocation readIslandLocation(RobotController rc, int islandId) {
        try {
            islandId = islandId + STARTING_ISLAND_IDX;
            int islandInt = rc.readSharedArray(islandId);
            int idx = islandInt >> (HEALTH_BITS + TEAM_BITS);
            return intToLocation(rc, idx);
        } catch (GameActionException e) {}
        return null;
    }

    static int readMaxIslandHealth(RobotController rc, int islandId) {
        try {
            islandId = islandId + STARTING_ISLAND_IDX;
            int islandInt = rc.readSharedArray(islandId);
            int healthMask = 0b111;
            int health = islandInt & healthMask;
            return health*HEALTH_SIZE;
        } catch (GameActionException e) {
            return -1;
        }
    }

    static MapLocation getClosestIsland(RobotController rc) throws GameActionException {
        MapLocation answer = null;
        for (int idx = STARTING_ISLAND_IDX; idx < STARTING_ENEMY_IDX; idx++) {
            final MapLocation m = readIslandLocation(rc, idx - STARTING_ISLAND_IDX);
            if (m != null && (answer == null || rc.getLocation().distanceSquaredTo(m) < rc.getLocation().distanceSquaredTo(answer))) {
                answer = m;
            }
        }
        return answer;
    }


    static void clearObsoleteEnemies(RobotController rc) {
        for (int i = STARTING_ENEMY_IDX; i < STARTING_WELL_IDX; i++) {
            try {
                MapLocation mapLoc = intToLocation(rc, rc.readSharedArray(i));
                if (mapLoc == null) {
                    continue;
                }
                if (rc.canSenseLocation(mapLoc) && rc.senseNearbyRobots(mapLoc, AREA_RADIUS, rc.getTeam().opponent()).length == 0) {
                    Message msg = new Message(i, locationToInt(rc, null), RobotPlayer.turnCount);
                    messagesQueue.add(msg);
                }
            } catch (GameActionException e) {
                continue;
            }
        }
    }

    static void reportEnemy(RobotController rc, MapLocation enemy) {
        int slot = -1;
        for (int i = STARTING_ENEMY_IDX; i < STARTING_WELL_IDX; i++) {
            try {
                MapLocation prevEnemy = intToLocation(rc, rc.readSharedArray(i));
                if (prevEnemy == null) {
                    slot = i;
                    break;
                } else if (prevEnemy.distanceSquaredTo(enemy) < AREA_RADIUS) {
                    return;
                }
            } catch (GameActionException e) {
                continue;
            }
        }
        if (slot != -1) {
            Message msg = new Message(slot, locationToInt(rc, enemy), RobotPlayer.turnCount);
            rc.setIndicatorDot(enemy, 255, 0, 0);
            messagesQueue.add(msg);
        }
    }

    static MapLocation getClosestEnemy(RobotController rc) {
        MapLocation answer = null;
        for (int i = STARTING_ENEMY_IDX; i < STARTING_WELL_IDX; i++) {
            final int value;
            try {
                value = rc.readSharedArray(i);
                final MapLocation m = intToLocation(rc, value);
                if (m != null && (answer == null || rc.getLocation().distanceSquaredTo(m) < rc.getLocation().distanceSquaredTo(answer))) {
                    answer = m;
                }
            } catch (GameActionException e) {
                continue;
            }
        }
        return answer;
    }

    static void addWell(RobotController rc, ResourceType r, MapLocation loc) throws GameActionException {
        for (int i=STARTING_WELL_IDX; i < GameConstants.SHARED_ARRAY_LENGTH; i++) {
            if (rc.readSharedArray(i) == 0 && !isWellWritten(rc, loc) && (getSymLocInt(rc, loc)==0 || !isWellWritten(rc, Communication.intToLocation(rc, getSymLocInt(rc, loc))))) {
                int val = 0;
                switch (r) {
                    case ADAMANTIUM: val = 0; break;
                    case MANA: val = 1; break;
                    case ELIXIR: val = 2; break;
                }

                // report congestion around well
                RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam());
                int numberAdjacent = 0;
                for (RobotInfo robot : robots) {
                    if (robot.getLocation().isAdjacentTo(loc)) {
                        numberAdjacent += 1;
                    }
                }

                int congested = 0;
                if (numberAdjacent > CarrierStrategy.WELL_CONGESTION_MAX) {
                    congested = 1;
                }
                val += congested << RESOURCE_TYPE_BITS;

//                report HQ closest to well
//                int[] dists = new int[]{3600, 3600, 3600, 3600};
//                for (int j=STARTING_HQ_IDX; j<STARTING_ISLAND_IDX; j++) {
//                    if (rc.readSharedArray(j) != 0) { //  && Communication.readNeedsLauncher(rc, i)==1
//                        dists[j-STARTING_HQ_IDX] = RobotPlayer.distSquaredLoc(loc, readHeadquarterLocation(rc, j));
//                    }
//                }
//
//                int min1 = Math.min(dists[0], dists[1]);
//                int min2 = Math.min(dists[2], dists[3]);
//                int min = Math.min(min1, min2);
//
//                int hqId = 0;
//                if (min != 3600) {
//                    for (int k = 0; k < 4; k++) {
//                        if (min == dists[k]) {
//                            hqId = k+STARTING_HQ_IDX;
//                            break;
//                        }
//                    }
//                }
//                val += (hqId << 1);

                val += (Communication.locationToInt(rc, loc) << RESOURCE_TYPE_BITS + CONGESTION_BITS);
                rc.writeSharedArray(i, val);

                System.out.println(rc.readSharedArray(STARTING_WELL_IDX));
                System.out.println(rc.readSharedArray(STARTING_WELL_IDX+1));
                System.out.println(rc.readSharedArray(STARTING_WELL_IDX+2));
                System.out.println(rc.readSharedArray(STARTING_WELL_IDX+3));
                System.out.println(rc.readSharedArray(STARTING_WELL_IDX+4));

                break;
            } else if (rc.readSharedArray(i) != 0) {
                RobotInfo[] robots = rc.senseNearbyRobots(-1, rc.getTeam());
                int numberAdjacent = 0;
                for (RobotInfo robot : robots) {
                    if (robot.getLocation().isAdjacentTo(loc)) {
                        numberAdjacent += 1;
                    }
                }

                int congested = 0;
                if (numberAdjacent > CarrierStrategy.WELL_CONGESTION_MAX) {
                    congested = 1;
                }

                if (congested != isWellCongested(rc, i)) {
                    int val = (rc.readSharedArray(i) & 0b111111111111000)
                            + (congested << RESOURCE_TYPE_BITS)
                            + (rc.readSharedArray(i) & RESOURCE_TYPE_MASK);
                    rc.writeSharedArray(i, val);
                }

                // TODO: account for possibly erroneous congestion checks that only apply to one well and not its symmetrical counterpart
            }
        }
    }

    static MapLocation readWellLocation(RobotController rc, int idx) throws GameActionException {
        return Communication.intToLocation(rc, rc.readSharedArray(idx) >> RESOURCE_TYPE_BITS + CONGESTION_BITS);
    }

    static int isWellCongested(RobotController rc, int idx) throws GameActionException {
        return (rc.readSharedArray(idx) & CONGESTION_MASK) >> RESOURCE_TYPE_BITS;
    }

//    static MapLocation getClosestHQLocToWell(RobotController rc, int idx) throws GameActionException {
//        return readHeadquarterLocation(rc, (rc.readSharedArray(idx) & 0b1110) >> 1);
//    }

    static ResourceType getWellType(RobotController rc, int idx) throws GameActionException {
        switch (rc.readSharedArray(idx) & RESOURCE_TYPE_MASK) {
            case 0: return ResourceType.ADAMANTIUM;
            case 1: return ResourceType.MANA;
            case 2: return ResourceType.ELIXIR;
            default: return null;
        }
    }

    static MapLocation getNearestWellOfType(RobotController rc, ResourceType r) throws GameActionException {

        int[] dists = new int[]{7200, 7200, 7200, 7200, 7200};
        MapLocation[] locs = new MapLocation[5];

        for (int j=STARTING_WELL_IDX; j<GameConstants.SHARED_ARRAY_LENGTH; j++) {
            if (rc.readSharedArray(j) != 0 && getWellType(rc, j) == r) {
                dists[j-STARTING_WELL_IDX] = RobotPlayer.distSquaredLoc(rc.getLocation(), readWellLocation(rc, j)) * (1 + isWellCongested(rc, j)); // weighted such that a non-congested well at any distance less than 2x the distance of a congested well will be prioritized
                locs[j-STARTING_WELL_IDX] = readWellLocation(rc, j);
                if (findSymmetry(rc) != 0) {
                    int new_dist = RobotPlayer.distSquaredLoc(rc.getLocation(), getSymLoc(rc, readWellLocation(rc, j))) * (1 + isWellCongested(rc, j));
                    if (new_dist < dists[j-STARTING_WELL_IDX]) {
                        dists[j-STARTING_WELL_IDX] = new_dist;
                        locs[j-STARTING_WELL_IDX] = getSymLoc(rc, readWellLocation(rc, j));
                    }
                }
            }
        }

        int min1 = Math.min(dists[0], dists[1]);
        int min2 = Math.min(dists[2], dists[3]);
        int min3 = Math.min(min1, min2);
        int min = Math.min(min3, dists[4]);

        int wellId = 0;
        if (min != 7200) {
            for (int k = 0; k < 5; k++) {
                if (min == dists[k]) {
                    wellId = k + STARTING_WELL_IDX;
                    break;
                }
            }
        } else {
            System.out.println("imposter!");
            return null;
        }

        return readWellLocation(rc, wellId);

    }

    static boolean isWellWritten(RobotController rc, MapLocation loc) throws GameActionException {
        boolean isWritten = false;
        for (int i = STARTING_WELL_IDX; i < GameConstants.SHARED_ARRAY_LENGTH; i++) {
            if (rc.readSharedArray(i) != 0) {
                rc.setIndicatorDot(readWellLocation(rc, i), ((rc.getTeam() == Team.A) ? 1 : 0)*255, 0, ((rc.getTeam() == Team.B) ? 1 : 0)*255);
            }
            if (rc.readSharedArray(i) != 0 && loc.equals(readWellLocation(rc, i))) {
                isWritten = true;
                break;
            }
        }
        return isWritten;
    }

    static int locationToInt(RobotController rc, MapLocation m) {
        if (m == null) {
            return 0;
        }
        return 1 + m.x + m.y * rc.getMapWidth();
    }

    static MapLocation intToLocation(RobotController rc, int m) {
        if (m == 0) {
            return null;
        }
        m--;
        return new MapLocation(m % rc.getMapWidth(), m / rc.getMapWidth());
    }

    static int findSymmetry(RobotController rc) throws GameActionException {
        if (readHQStatus(rc, "ny")==1 && readHQStatus(rc, "nr")==1) {
            return 1; // symmetry about x
        } else if (readHQStatus(rc, "nx")==1 && readHQStatus(rc, "nr")==1) {
            return 2; // symmetry about y
        } else if (readHQStatus(rc, "nx")==1 && readHQStatus(rc, "ny")==1) {
            return 3; // rotational symmetry
        } else {
            return 0;
        }
    }

    static int getSymLocInt(RobotController rc, MapLocation loc) throws GameActionException {
        switch (findSymmetry(rc)){
            case 1: return RobotPlayer.xReflect(rc, loc);
            case 2: return RobotPlayer.yReflect(rc, loc);
            case 3: return RobotPlayer.diagReflect(rc, loc);
            default: return 0;
        }
    }

    static MapLocation getSymLoc(RobotController rc, MapLocation loc) throws GameActionException {
        switch (findSymmetry(rc)){
            case 1: return intToLocation(rc, RobotPlayer.xReflect(rc, loc));
            case 2: return intToLocation(rc, RobotPlayer.yReflect(rc, loc));
            case 3: return intToLocation(rc, RobotPlayer.diagReflect(rc, loc));
            default: return null;
        }
    }

}