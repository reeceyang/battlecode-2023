package kaiplayer;

import battlecode.common.*;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

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
    static final int STARTING_ISLAND_IDX = GameConstants.MAX_STARTING_HEADQUARTERS + 1;
    private static final int STARTING_ENEMY_IDX = GameConstants.MAX_NUMBER_ISLANDS + GameConstants.MAX_STARTING_HEADQUARTERS + 1;

    private static final int TOTAL_BITS = 16;
    private static final int MAPLOC_BITS = 12;
    private static final int TEAM_BITS = 1;
    private static final int HEALTH_BITS = 3;
    private static final int HEALTH_SIZE = (int) Math.ceil(Anchor.ACCELERATING.totalHealth / 8.0);
    private static final int HQ_COUNT_BITS = 2;
    private static final int ENEMY_HQ_BITS = 4;
    public static int headquarterCount = 0;


    private static List<Message> messagesQueue = new ArrayList<>();
    private static MapLocation[] headquarterLocs = new MapLocation[GameConstants.MAX_STARTING_HEADQUARTERS];



    static int increaseHeadquarterCount(RobotController rc) throws GameActionException {
        headquarterCount = rc.readSharedArray(0) + 1;
        rc.writeSharedArray(0, headquarterCount);
        return headquarterCount;
    }

    static int readHeadquarterCount(RobotController rc) throws GameActionException {
        return rc.readSharedArray(0) & 0b11;
    }

    static void addHeadquarter(RobotController rc, int loc) throws GameActionException {
        if (rc.readSharedArray(headquarterCount) == 0) {
            rc.writeSharedArray(headquarterCount, loc << 4);
        }
    }

    static int readHQLoc(RobotController rc, int hqId) throws GameActionException {
        return rc.readSharedArray(hqId) >> 4;
    }

    static void updateHeadquarterInfo(RobotController rc) throws GameActionException {
        if (RobotPlayer.turnCount == 2) {
            for (int i = 1; i < GameConstants.MAX_STARTING_HEADQUARTERS + 1; i++) {
                headquarterLocs[i-1] = (intToLocation(rc, readHQLoc(rc, i)));
                if (rc.readSharedArray(i) == 0) {
                    break;
                }
            }
        }
    }

    static void tryWriteMessages(RobotController rc) throws GameActionException {
        messagesQueue.removeIf(msg -> msg.turnAdded + OUTDATED_TURNS_AMOUNT < RobotPlayer.turnCount);
        // Can always write (0, 0), so just checks are we in range to write
        if (rc.canWriteSharedArray(0, 0)) {
            while (messagesQueue.size() > 0 ) {
                Message msg = messagesQueue.remove(0); // Take from front or back?
                if (rc.canWriteSharedArray(msg.idx, msg.value)) {
                    rc.writeSharedArray(msg.idx, msg.value);
                }
            }
        }
    }

    static void updateIslandInfo(RobotController rc, int id) throws GameActionException {
        if (headquarterLocs[0] == null) {
            return;
        }
        MapLocation closestIslandLoc = null;
        int closestDistance = -1;
        MapLocation[] islandLocs = rc.senseNearbyIslandLocations(id);
        for (MapLocation loc : islandLocs) {
            int distance = headquarterLocs[0].distanceSquaredTo(loc);
            if (closestIslandLoc == null || distance < closestDistance) {
                closestDistance = distance;
                closestIslandLoc = loc;
            }
        }
        // Remember reading is cheaper than writing so we don't want to write without knowing if it's helpful
        int idx = id + STARTING_ISLAND_IDX;
        int oldIslandValue = rc.readSharedArray(idx);
        int updatedIslandValue = bitPackIslandInfo(rc, idx, closestIslandLoc);
        if (oldIslandValue != updatedIslandValue) {
            Message msg = new Message(idx, updatedIslandValue, RobotPlayer.turnCount);
            messagesQueue.add(msg);
        }
    }

    static int bitPackIslandInfo(RobotController rc, int islandId, MapLocation closestLoc) {
        int islandInt = locationToInt(rc, closestLoc);
        islandInt = islandInt << (TOTAL_BITS - MAPLOC_BITS);
        try {
            Team teamHolding = rc.senseTeamOccupyingIsland(islandId);
            islandInt += teamHolding.ordinal() << (TOTAL_BITS - MAPLOC_BITS - TEAM_BITS);
            int islandHealth = rc.senseAnchorPlantedHealth(islandId);
            int healthEncoding = (int) Math.ceil((double) islandHealth / HEALTH_SIZE);
            islandInt += healthEncoding;
            return islandInt;
        } catch (GameActionException e) {
            return islandInt;
        }
    }

    static void updateHQStatus(RobotController rc, int x1, int x2, int x3, int x4, int y1, int y2, int y3, int y4, int nx, int ny) {
        try {
            int val = (x1*0b1000000000 + x2*0b100000000 + x3*0b10000000 + x4*0b1000000 + y1*0b100000 + y2*0b10000 + y3*0b1000 + y4*0b100 + nx*0b10 + ny*0b1) << (HQ_COUNT_BITS + ENEMY_HQ_BITS) | rc.readSharedArray(0);
            rc.writeSharedArray(0, val);
        } catch (GameActionException e) {}
    }

    static int readHQStatus(RobotController rc, String j) {
        try {
            switch (j) {
                case "x1": return (rc.readSharedArray(0) >> (HQ_COUNT_BITS + ENEMY_HQ_BITS + 9));
                case "x2": return (rc.readSharedArray(0) >> (HQ_COUNT_BITS + ENEMY_HQ_BITS + 8)) % 0b10;
                case "x3": return (rc.readSharedArray(0) >> (HQ_COUNT_BITS + ENEMY_HQ_BITS + 7)) % 0b10;
                case "x4": return (rc.readSharedArray(0) >> (HQ_COUNT_BITS + ENEMY_HQ_BITS + 6)) % 0b10;
                case "y1": return (rc.readSharedArray(0) >> (HQ_COUNT_BITS + ENEMY_HQ_BITS + 5)) % 0b10;
                case "y2": return (rc.readSharedArray(0) >> (HQ_COUNT_BITS + ENEMY_HQ_BITS + 4)) % 0b10;
                case "y3": return (rc.readSharedArray(0) >> (HQ_COUNT_BITS + ENEMY_HQ_BITS + 3)) % 0b10;
                case "y4": return (rc.readSharedArray(0) >> (HQ_COUNT_BITS + ENEMY_HQ_BITS + 2)) % 0b10;
                case "nx": return (rc.readSharedArray(0) >> (HQ_COUNT_BITS + ENEMY_HQ_BITS + 1)) % 0b10;
                case "ny": return (rc.readSharedArray(0) >> (HQ_COUNT_BITS + ENEMY_HQ_BITS)) % 0b10;
            }
            return 2;
        } catch (GameActionException e) {
            return 2;
        }
    }

    static void updateNeedsLauncher(RobotController rc, int hqId) {
        int val = 0;
        try {
            switch (hqId) {
                case 1: val = 0b1; break;
                case 2: val = 0b10; break;
                case 3: val = 0b100; break;
                case 4: val = 0b1000; break;
            }
            if (readNeedsLauncher(rc, hqId) == 0) {
                rc.writeSharedArray(0, val + rc.readSharedArray(0));
            }
        } catch (GameActionException e) {}
    }

    static void updateDoesntNeedLauncher(RobotController rc, int hqId) {
        int val = 0;
        try {
            switch (hqId) {
                case 1: val = 0b1; break;
                case 2: val = 0b10; break;
                case 3: val = 0b100; break;
                case 4: val = 0b1000; break;
            }
            if (readNeedsLauncher(rc, hqId) == 1) {
                rc.writeSharedArray(0, rc.readSharedArray(0) - val);
            }
        } catch (GameActionException e) {}
    }

    static int readNeedsLauncher(RobotController rc, int hqId) {
        try {
            return (rc.readSharedArray(0) >> (hqId-1)) & 0b1;
        } catch (GameActionException e) {
            return 2;
        }
    }

    static void reportHQEnemy(RobotController rc, int b, int hqId) throws GameActionException {
        if ((rc.readSharedArray(hqId) & 0b1) != b) {
            if (b==1) {
                rc.writeSharedArray(hqId, rc.readSharedArray(hqId)+1);
            } else {
                rc.writeSharedArray(hqId, rc.readSharedArray(hqId)-1);
            }
        }
    }

    static int getHQEnemies(RobotController rc, int hqId) throws GameActionException {
        return rc.readSharedArray(hqId) & 0b1;
    }

    static Team readTeamHoldingIsland(RobotController rc, int islandId) {
        try {
            islandId = islandId + STARTING_ISLAND_IDX;
            int islandInt = rc.readSharedArray(islandId);
            int healthMask = 0b111;
            int health = islandInt & healthMask;
            int team = (islandInt >> HEALTH_BITS) % 0b1; // wtf is going on here
            if (health > 0) {
                return Team.values()[team];
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


    static void clearObsoleteEnemies(RobotController rc) {
        for (int i = STARTING_ENEMY_IDX; i < GameConstants.SHARED_ARRAY_LENGTH; i++) {
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
        for (int i = STARTING_ENEMY_IDX; i < GameConstants.SHARED_ARRAY_LENGTH; i++) {
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
            messagesQueue.add(msg);
        }
    }

    static MapLocation getClosestEnemy(RobotController rc) {
        MapLocation answer = null;
        for (int i = STARTING_ENEMY_IDX; i < GameConstants.SHARED_ARRAY_LENGTH; i++) {
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

    public static int locationToInt(RobotController rc, MapLocation m) {
        if (m == null) {
            return 0;
        }
        return 1 + m.x + m.y * rc.getMapWidth();
    }

    public static MapLocation intToLocation(RobotController rc, int m) {
        if (m == 0) {
            return null;
        }
        m--;
        return new MapLocation(m % rc.getMapWidth(), m / rc.getMapWidth());
    }
}
