package kaiplayer;

import battlecode.common.*;

import static kaiplayer.RobotPlayer.turnCount;

public class HQStrategy {

    static MapLocation enemyLocVert;
    static MapLocation enemyLocHor;
    static MapLocation carrierSpawn;

    static void runHeadquarters(RobotController rc) throws GameActionException {
        for (int i=1; i<5; i++) {
            if (rc.readSharedArray(i) != 0) {
                rc.setIndicatorDot(RobotPlayer.xReflectLoc(rc, rc.readSharedArray(i)), 255*((rc.getTeam()==Team.A) ? 1 : 0), 0, 255*((rc.getTeam()==Team.B) ? 1 : 0));
                rc.setIndicatorDot(RobotPlayer.yReflectLoc(rc, rc.readSharedArray(i)), 255*((rc.getTeam()==Team.A) ? 1 : 0), 0, 255*((rc.getTeam()==Team.B) ? 1 : 0));
            }
        }
        if (turnCount == 1) {
            Communication.increaseHeadquarterCount(rc); // add 1 to 0th index of shared array to get number of headquarters currently created
            Communication.addHeadquarter(rc, Communication.locationToInt(rc, rc.getLocation())); // add bitpacked hq loc to shared array
            enemyLocVert = new MapLocation(rc.getLocation().x, rc.getMapHeight() - rc.getLocation().y);
            enemyLocHor = new MapLocation(rc.getMapWidth() - rc.getLocation().x, rc.getLocation().y);
            WellInfo[] wells = rc.senseNearbyWells();
            Direction carrierDir = rc.getLocation().directionTo(wells[0].getMapLocation());
            carrierSpawn = rc.getLocation().add(carrierDir);
        }
        // TODO: since directions are known here, see if there are any obstructions directly adjacent to hq first, to eliminate those dirs

        // Pick a direction to build in.
        Direction launcherDir;
        if (turnCount == 3) {
            // build in direction of first hypothesized headquarter (vertical reflection)
            launcherDir = rc.getLocation().directionTo(enemyLocVert);
        } else if (turnCount == 5) {
            // build in direction of second hypothesized headquarter (horizontal reflection)
            launcherDir = rc.getLocation().directionTo(enemyLocHor);
        } else {
            // TODO: continue building in direction of enemy hq
            launcherDir = RobotPlayer.directions[RobotPlayer.rng.nextInt(RobotPlayer.directions.length)];
        }
        MapLocation launcherSpawn = rc.getLocation().add(launcherDir);

//        if (rc.canBuildAnchor(Anchor.STANDARD)) {
//            // If we can build an anchor do it!
//            rc.buildAnchor(Anchor.STANDARD);
//            rc.setIndicatorString("Building anchor! " + rc.getAnchor());
//        }
        if (turnCount == 1 || turnCount == 2) {
            // Build carriers first
            rc.setIndicatorString("Trying to build a carrier");
            if (rc.canBuildRobot(RobotType.CARRIER, carrierSpawn)) {
                rc.buildRobot(RobotType.CARRIER, carrierSpawn);
            } // TODO: else build carrier in random direction
        } else if (turnCount == 3) {
            // Build launchers next
            rc.setIndicatorString("Trying to build a launcher");
            if (rc.canBuildRobot(RobotType.LAUNCHER, launcherSpawn)) {
                rc.buildRobot(RobotType.LAUNCHER, launcherSpawn);
            }
        } else if (turnCount == 5) {
            // I forgot why we are building the launchers separately but maybe there's a reason - oh right the first one is horizontal and the second is vertical?
            rc.setIndicatorString("Trying to build a launcher");
            if (rc.canBuildRobot(RobotType.LAUNCHER, launcherSpawn)) {
                rc.buildRobot(RobotType.LAUNCHER, launcherSpawn);
            }
        } else if (turnCount == 4 || turnCount == 6) {
            rc.setIndicatorString("Trying to build an amplifier");
            if (rc.canBuildRobot(RobotType.AMPLIFIER, launcherSpawn)) {
                rc.buildRobot(RobotType.AMPLIFIER, launcherSpawn);
            }
        } else {
            // TODO: randomly generate either carrier or launcher
            if (rc.canBuildRobot(RobotType.CARRIER, carrierSpawn)) {
                rc.buildRobot(RobotType.CARRIER, carrierSpawn);
            }
        }

        if (turnCount==200) {
            System.out.println("x1: "+Communication.readHQStatus(rc, "x1"));
            System.out.println("x2: "+Communication.readHQStatus(rc, "x2"));
            System.out.println("x3: "+Communication.readHQStatus(rc, "x3"));
            System.out.println("x4: "+Communication.readHQStatus(rc, "x4"));
            System.out.println("y1: "+Communication.readHQStatus(rc, "y1"));
            System.out.println("y2: "+Communication.readHQStatus(rc, "y2"));
            System.out.println("y3: "+Communication.readHQStatus(rc, "y3"));
            System.out.println("y4: "+Communication.readHQStatus(rc, "y4"));
            System.out.println("nx: "+Communication.readHQStatus(rc, "nx"));
            System.out.println("ny: "+Communication.readHQStatus(rc, "ny"));
            rc.resign();
        }

    }

}
