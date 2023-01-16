package hognoseplayer;

import battlecode.common.*;

public class HQStrategy {
	static int selfidx = 0;
	static boolean starving = false;
	static boolean needMana = false; // true = needs Mana
	
	
	static boolean congested = false;
	static boolean superCongested = false;
	
	// To track changes in resource amounts
	static int Ad = 200;
	static int Ma = 0;
	
	// Control parameters
	static int starvation = 30; // if counts down to 0, change status to starving
	static int adTarget = 50; 
	static int manaTarget = 60;
	static float ratioAdToMana = 1; // an initial value; gets modified based on map size
	static int congestionMax = 40;
	static int congestionMax2 = 69;
	
	/**
     * Run a single turn for a Headquarters.
     * This code is wrapped inside the infinite loop in run(), so it is called once per turn.
     */
    static void runHeadquarters(RobotController rc) throws GameActionException {
    	if (RobotPlayer.turnCount == 1) {
            Communication.addHeadquarter(rc);
            //ratioAdToMana = (rc.getMapWidth() + 10) / 60; // max map size; bigger map -> more Ad
            //manaTarget /= ratioAdToMana;
        } else if (RobotPlayer.turnCount == 2) {
            Communication.updateHeadquarterInfo(rc);
            selfidx = Communication.getIdxHQbyLocation(rc, rc.getLocation());
        }
    	
    	// TRAFFIC MANAGEMENT
    	// Check number of ally robots every 10 turns
    	if (RobotPlayer.turnCount % 10 == 0) {
	    	RobotInfo[] friends = rc.senseNearbyRobots(-1, rc.getTeam());
	    	congested = (friends.length > congestionMax);
	    	superCongested = (friends.length > congestionMax2);
    	}
    	
    	if (congested) { rc.setIndicatorString("Congested"); }
    	
    	// RESOURCE MANAGEMENT
    	// Check for inventory change and adjust starvation counter
		int AdNew = rc.getResourceAmount(ResourceType.ADAMANTIUM);
		int MaNew = rc.getResourceAmount(ResourceType.MANA);
		if (AdNew > adTarget && MaNew > manaTarget) {
			// we gained Ad and Ma
			starvation = 30;
			starving = false;
		} else {
			// uh oh, resources are not coming in
			starvation -= 1;
			if (starvation < 1) {
				starving = true;
				//rc.setIndicatorString("Starving");
			}
		}
		Ad = AdNew;
		Ma = MaNew;
		// Decide which resource to prioritize
		if ((float) AdNew / (float) MaNew > ratioAdToMana && AdNew > adTarget) {
			// we have too much Ad
			needMana = true;
		} else {
			needMana = false;
		}
		
		// CONSTRUCTION MANAGEMENT
    	// TODO: Choose the build location based on occupation of location
        Direction dir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        MapLocation newLoc = rc.getLocation().add(dir);
        // Build anchor if resource rich
        if (rc.canBuildAnchor(Anchor.STANDARD) && rc.getNumAnchors(Anchor.STANDARD) < 5 && rc.getResourceAmount(ResourceType.ADAMANTIUM) > 100) {
            rc.buildAnchor(Anchor.STANDARD);
        } else if (rc.canBuildAnchor(Anchor.STANDARD) && rc.getResourceAmount(ResourceType.ADAMANTIUM) > 600) {
        	rc.buildAnchor(Anchor.STANDARD);
        }
        // Build carriers at the start
        if (RobotPlayer.turnCount < 5) {
        	rc.setIndicatorString("Trying to build initial carrier");
            if (rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
                rc.buildRobot(RobotType.CARRIER, newLoc);
            } else if (rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
            	rc.buildRobot(RobotType.LAUNCHER, newLoc);
            }
        } else {
	        // Adaptive rest of game
	        if (starving) {
	            // Let's try to build a carrier. We need more income.
	            if (!congested && rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
	                rc.buildRobot(RobotType.CARRIER, newLoc);
	            } else {
	            	if (!superCongested && rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
		                rc.buildRobot(RobotType.LAUNCHER, newLoc);
		            } 
	            }
	        } else {
	            // We have enough resources flowing, let's prioritize launchers.
	            if (!superCongested && rc.canBuildRobot(RobotType.LAUNCHER, newLoc)) {
	                rc.buildRobot(RobotType.LAUNCHER, newLoc);
	            } else {
	            	if (!congested && rc.canBuildRobot(RobotType.CARRIER, newLoc)) {
		                rc.buildRobot(RobotType.CARRIER, newLoc);
	            	}
	            }
	        }
        }
        
        // ATTACK/DEFENSE MANAGEMENT
        // TODO: 
        
        // COMMUNICATION
        int stateInt = Communication.bitPackHQInfo(rc, needMana, false, false, congested);
        if (rc.canWriteSharedArray(selfidx, stateInt)) {
        	rc.writeSharedArray(selfidx, stateInt);
        }
        Communication.tryWriteMessages(rc);
    }
}
