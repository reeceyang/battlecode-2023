package v3beardedreedling;

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
    static final int STARTING_ISLAND_IDX = GameConstants.MAX_STARTING_HEADQUARTERS;
    private static final int STARTING_ENEMY_IDX = GameConstants.MAX_NUMBER_ISLANDS + GameConstants.MAX_STARTING_HEADQUARTERS;

    private static final int TOTAL_BITS = 16;
    private static final int MAPLOC_BITS = 12;
    private static final int TEAM_BITS = 1;
    private static final int HEALTH_BITS = 3;
    private static final int HEALTH_SIZE = (int) Math.ceil(Anchor.ACCELERATING.totalHealth / 8.0);
    
    static final int MA_AD_MASK = 0b1;
    static final int HQ_CONGEST_MASK = 0b1000;

    private static List<Message> messagesQueue = new ArrayList<>();
    private static MapLocation[] headquarterLocs = new MapLocation[GameConstants.MAX_STARTING_HEADQUARTERS];



    static int bitPackHQInfo(RobotController rc, boolean s1, boolean s2, boolean s4, boolean s8) throws GameActionException {
        int hqInt = locationToInt(rc, rc.getLocation());
        hqInt = hqInt << (TOTAL_BITS - MAPLOC_BITS);
        if (s1) { hqInt += 0b1; } // flag true if needs Mana, false if needs Adamantium
        if (s2) { hqInt += 0b10; } // flag for 
        if (s4) { hqInt += 0b100; } // flag for
        if (s8) { hqInt += 0b1000; } // flag true if congested
        return hqInt;
    }
    
 
    static void addHeadquarter(RobotController rc) throws GameActionException {
        for (int i = 0; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {
            if (rc.readSharedArray(i) == 0) {
                rc.writeSharedArray(i, bitPackHQInfo(rc, false, false, false, false));
                break;
            }
        }
    }

    static void updateHeadquarterInfo(RobotController rc) throws GameActionException {
        for (int i = 0; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {	
            headquarterLocs[i] = readHeadquarterLocation(rc, i);
            if (rc.readSharedArray(i) == 0) {
                break;
            }
        }
    }
    
    static MapLocation readHeadquarterLocation(RobotController rc, int idx) throws GameActionException {
    	int hqInt = rc.readSharedArray(idx);
    	int locInt = hqInt >> (TOTAL_BITS - MAPLOC_BITS);
        return intToLocation(rc, locInt);
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
    	for (int i = 0; i < GameConstants.MAX_STARTING_HEADQUARTERS; i++) {
    		if (loc.equals(readHeadquarterLocation(rc, i))) {
    			return i;
    		}
    	}
    	return 0;
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

    static Team readTeamHoldingIsland(RobotController rc, int islandId) {
        try {
            islandId = islandId + STARTING_ISLAND_IDX;
            int islandInt = rc.readSharedArray(islandId);
            int healthMask = 0b111;
            int health = islandInt & healthMask;
            int team = (islandInt >> HEALTH_BITS) % 0b1;
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

    private static int locationToInt(RobotController rc, MapLocation m) {
        if (m == null) {
            return 0;
        }
        return 1 + m.x + m.y * rc.getMapWidth();
    }

    private static MapLocation intToLocation(RobotController rc, int m) {
        if (m == 0) {
            return null;
        }
        m--;
        return new MapLocation(m % rc.getMapWidth(), m / rc.getMapWidth());
    }
}