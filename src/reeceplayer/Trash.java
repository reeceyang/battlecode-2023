package reeceplayer;

import battlecode.common.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Trash {
    static void buildGraph(RobotController rc, MapLocation target) throws GameActionException {
        MapInfo[] mapInfos = rc.senseNearbyMapInfos();
        HashMap<MapLocation, ArrayList<MapLocation>> graph = new HashMap<>();
        HashMap<MapLocation, Integer> costs = new HashMap<>();
        MapLocation minLoc = rc.getLocation();
        int minCost = target.distanceSquaredTo(minLoc);
        for (MapInfo mapInfo : mapInfos) {
            if (!mapInfo.isPassable()) continue;
            ArrayList<MapLocation> neighbors = new ArrayList<>();
            for (Direction dir : Direction.values()) {
                MapLocation neighbor = mapInfo.getMapLocation().add(dir);
                if (!rc.senseMapInfo(neighbor).isPassable()) continue;
                neighbors.add(neighbor);
                int neighborCost = target.distanceSquaredTo(neighbor);
                if (neighborCost < minCost) {
                    minCost = neighborCost;
                    minLoc = neighbor;
                }
                costs.put(neighbor, neighborCost);
            }
            graph.put(mapInfo.getMapLocation(), neighbors);
        }
    }
}
