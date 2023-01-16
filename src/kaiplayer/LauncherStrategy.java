package kaiplayer;

import battlecode.common.*;

import java.util.Map;

public class LauncherStrategy {
    static void runLauncher(RobotController rc) throws GameActionException {

        MapLocation hq = null;

        if (RobotPlayer.turnCount == 1) {
            int[] hqLoc = new int[]{rc.readSharedArray(1), rc.readSharedArray(2), rc.readSharedArray(3), rc.readSharedArray(4)};
            for (int i : hqLoc) {
                MapLocation m = Communication.intToLocation(rc, i);
                if ((m.x - rc.getLocation().x) * (m.x - rc.getLocation().x) + (m.y - rc.getLocation().y) * (m.y - rc.getLocation().y) < 2) {
                    hq = m;
                    break;
                }
            }
        }

        int targetHQ = 1; // CHANGE THIS TO TARGET 0 - ONLY 1 FOR BUILD PURPOSES

        if (Communication.readHQStatus(rc, "nx") == 1) {
            if (Communication.readHQStatus(rc, "ny") == 1) {
                targetHQ = RobotPlayer.diagReflect(rc, hq);
            } else {
                // TODO: pick a "y" that hasn't been verified; if all have been verified, pick closest "y"
                System.out.println("I'm tired");
            }
        } else {
            // TODO: pick an "x" that hasn't been verified; if all have been verified, pick closest "x"
            System.out.println("I'm even more tired");
        }

        Pathing.moveTowards(rc, Communication.intToLocation(rc, targetHQ));

        // Try to attack someone
        int radius = rc.getType().actionRadiusSquared;
        Team opponent = rc.getTeam().opponent();
        RobotInfo[] enemies = rc.senseNearbyRobots(radius, opponent);

        // TODO: pick enemy with lowest health to attack

        if (enemies.length > 0) {
             MapLocation toAttack = enemies[0].location;
//            MapLocation toAttack = rc.getLocation().add(Direction.EAST);

            if (rc.canAttack(toAttack)) {
                rc.setIndicatorString("Attacking");
                rc.attack(toAttack);
            }
        }

    }
}
