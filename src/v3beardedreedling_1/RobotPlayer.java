package v3beardedreedling_1;

import battlecode.common.*;

import java.util.Random;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {

    /**
     * Keep in mind that even though these variables are static, in Battlecode they aren't actually shared between your robots.
     */
    static int turnCount = 0;

    static Random rng = null;

    static final Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST,
    };

    static boolean isSmallMap;

    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * It is like the main function for your robot. If this method returns, the robot dies!
     *
     * @param rc  The RobotController object. You use it to perform actions from this robot, and to get
     *            information on its current status. Essentially your portal to interacting with the world.
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        rng = new Random(rc.getID());
        isSmallMap = rc.getMapWidth() * rc.getMapHeight() < 600;
        while (true) {

            turnCount += 1;  // We have now been alive for one more turn!

            try {
                switch (rc.getType()) {
                    case HEADQUARTERS:     HQStrategy.runHeadquarters(rc); break;
                    case CARRIER: CarrierStrategy.runCarrier(rc); break;
                    case LAUNCHER: LauncherStrategy.runLauncher(rc); break;
                    case BOOSTER: 
                    case DESTABILIZER:
                    case AMPLIFIER: AmplifierStrategy.runAmplifier(rc); break;
                }

            } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                System.out.println(rc.getType() + " Exception");
                e.printStackTrace();

            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
        }

        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
    }

    // Common methods

    static void moveRandom(RobotController rc) throws GameActionException {
        Direction dir = directions[rng.nextInt(directions.length)];
        if(rc.canMove(dir)) rc.move(dir);
    }
    static int distSquaredLoc(MapLocation loc1, MapLocation loc2) {
        return (loc1.x - loc2.x) * (loc1.x - loc2.x) + (loc1.y - loc2.y) * (loc1.y - loc2.y);
    }

    static int yReflect(RobotController rc, MapLocation loc) {
        MapLocation newloc = new MapLocation(loc.x, rc.getMapHeight() - loc.y - 1);
        return Communication.locationToInt(rc, newloc);
    }

    static int xReflect(RobotController rc, MapLocation loc) {
        MapLocation newloc = new MapLocation(rc.getMapWidth() - loc.x - 1, loc.y);
        return Communication.locationToInt(rc, newloc);
    }

    static int diagReflect(RobotController rc, MapLocation loc) {
        MapLocation newloc = new MapLocation(rc.getMapWidth() - loc.x - 1, rc.getMapHeight() - loc.y - 1);
        return Communication.locationToInt(rc, newloc);
    }
}
