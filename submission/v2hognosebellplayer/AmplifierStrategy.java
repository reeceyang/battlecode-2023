package v2hognosebellplayer;

import battlecode.common.*;

public class AmplifierStrategy {
	static boolean combatmode = false;
	static MapLocation nextLoc;

    /**
     * Run a single turn for a Launcher.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runAmplifier(RobotController rc) throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots();
        // report any enemies, and either follow friendlies or avoid enemies
        nextLoc = Pathing.reportAndPlaySafe(rc, robots, 1);
        
        if (nextLoc == null) {
        	// go patrol a nearby well or island
            WellInfo[] wells = rc.senseNearbyWells();
            if (wells.length > 0){
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
            	if (closestIslandLoc != null) { nextLoc = closestIslandLoc; }
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
        	Pathing.moveTowards(rc, nextLoc);
        }
    }
    
    /* -------------------------------- */
}
