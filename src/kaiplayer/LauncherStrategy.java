package kaiplayer;

import battlecode.common.*;

import java.util.Map;

public class LauncherStrategy {
    static MapLocation hq = null;
    static int target = 0;
    static int targetId = 0;
    static boolean goingToEnemy = false;
    static void runLauncher(RobotController rc) throws GameActionException {

        if (RobotPlayer.turnCount == 1) {
            int[] hqLoc = new int[]{Communication.readHQLoc(rc, 1), Communication.readHQLoc(rc, 2), Communication.readHQLoc(rc, 3), Communication.readHQLoc(rc, 4)};
            for (int i : hqLoc) {
                MapLocation m = Communication.intToLocation(rc, i);
                if (i != 0 && RobotPlayer.distSquaredLoc(m, rc.getLocation()) < 2) {
                    hq = m;
                    break;
                }
            }
        }

        boolean goingToHQ = false;

        // Launcher sees if any home HQs are in need of defense, and if so, goes to the nearest one

        int[] dists = new int[]{3600, 3600, 3600, 3600};

        for (int i=1; i<5; i++) {
            if (Communication.getHQEnemies(rc, i) == 1) {
                dists[i-1] = RobotPlayer.distSquaredLoc(rc.getLocation(), Communication.intToLocation(rc, Communication.readHQLoc(rc, i)));
            }
        }

        int min1 = Math.min(dists[0], dists[1]);
        int min2 = Math.min(dists[2], dists[3]);
        int min = Math.min(min1, min2);

        if (min != 3600) {
            for (int i = 0; i < 4; i++) {
                if (min == dists[i]) {
                    target = Communication.readHQLoc(rc, i+1);
                    goingToHQ = true;
                    break;
                }
            }
        } else {
            goingToHQ = false;
        }

        if (goingToHQ) {
            rc.setIndicatorString("Trying to go to headquarters!");
            Pathing.moveTowards(rc, Communication.intToLocation(rc, target));
        }

        // If curr location is too congested by launchers, change target to next enemy hq

        if (target != 0 && rc.canSenseRobotAtLocation(Communication.intToLocation(rc, target)) && rc.senseNearbyRobots(-1,rc.getTeam()).length > 6) {
            // change target to next enemy hq
            System.out.println("Changing HQ target from "+targetId);
            if (targetId + 1 <= Communication.readHeadquarterCount(rc)) {
                targetId ++;
            } else {
                targetId = 1;
            }
            System.out.println("to "+targetId);
            goingToEnemy = true;
        }

        // If no home HQs are in need of defense

        if (goingToHQ == false) {
            if (Communication.readHQStatus(rc, "nx") == 1) {
                if (Communication.readHQStatus(rc, "ny") == 1) {

                    // If launcher doesn't already have a target HQ

                    if (!goingToEnemy) {

                        // Go to closest verified enemy HQ location

                        dists = new int[]{3600, 3600, 3600, 3600};
                        for (int i = 1; i < 5; i++) {
                            if (Communication.readHQLoc(rc, i) != 0) { //  && Communication.readNeedsLauncher(rc, i)==1
                                dists[i - 1] = RobotPlayer.distSquaredLoc(rc.getLocation(), Communication.intToLocation(rc, RobotPlayer.diagReflect(rc, Communication.intToLocation(rc, Communication.readHQLoc(rc, i)))));
                            }
                        }

                        min1 = Math.min(dists[0], dists[1]);
                        min2 = Math.min(dists[2], dists[3]);
                        min = Math.min(min1, min2);

                        if (min != 3600) {
                            for (int i = 0; i < 4; i++) {
                                if (min == dists[i]) {
                                    targetId = i + 1;
                                    break;
                                }
                            }
                        }
                        goingToEnemy = true;
                    }

                    target = RobotPlayer.diagReflect(rc, Communication.intToLocation(rc, Communication.readHQLoc(rc, targetId)));
                    rc.setIndicatorString("Trying to go to enemy HQ #" + targetId);
                    Pathing.moveTowards(rc, Communication.intToLocation(rc, target));

                } else {
                    // TODO: unexplored potential enemy HQ location - if it gets within range to see that there is no HQ there, go to new unexplored enemy HQ location (in case of amplifier death)
                    target = RobotPlayer.yReflect(rc, Communication.intToLocation(rc, Communication.readHQLoc(rc, 1)));
                    Pathing.moveTowards(rc, Communication.intToLocation(rc, target));
                    target = 0;
                }
            } else {
                // TODO: closest unexplored potential enemy HQ location
                target = RobotPlayer.xReflect(rc, Communication.intToLocation(rc, Communication.readHQLoc(rc, 1)));
                Pathing.moveTowards(rc, Communication.intToLocation(rc, target));
                target = 0;
            }
        } else {
            if (goingToEnemy) rc.setIndicatorString("Trying to go to enemy HQ #" + targetId);
            Pathing.moveTowards(rc, Communication.intToLocation(rc, target));
        }

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
