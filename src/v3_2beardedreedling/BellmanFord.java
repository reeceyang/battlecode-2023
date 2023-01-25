package v3_2beardedreedling;

import battlecode.common.*;

public class BellmanFord {
    public static void doBellmanFord(RobotController rc, MapLocation target) throws GameActionException {
        MapLocation bot = rc.getLocation();
        MapLocation pos;
        Direction currentDir;
        boolean open00 = false;
        Direction current00 = null;
        int cost00 = 2147483647;
        pos = new MapLocation(bot.x + -2, bot.y + -2);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open00 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost00 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current00 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open00 = true;
            cost00 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open01 = false;
        Direction current01 = null;
        int cost01 = 2147483647;
        pos = new MapLocation(bot.x + -2, bot.y + -1);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open01 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost01 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current01 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open01 = true;
            cost01 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open02 = false;
        Direction current02 = null;
        int cost02 = 2147483647;
        pos = new MapLocation(bot.x + -2, bot.y + 0);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open02 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost02 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current02 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open02 = true;
            cost02 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open03 = false;
        Direction current03 = null;
        int cost03 = 2147483647;
        pos = new MapLocation(bot.x + -2, bot.y + 1);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open03 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost03 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current03 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open03 = true;
            cost03 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open04 = false;
        Direction current04 = null;
        int cost04 = 2147483647;
        pos = new MapLocation(bot.x + -2, bot.y + 2);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open04 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost04 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current04 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open04 = true;
            cost04 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open10 = false;
        Direction current10 = null;
        int cost10 = 2147483647;
        pos = new MapLocation(bot.x + -1, bot.y + -2);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open10 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost10 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current10 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open10 = true;
            cost10 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open11 = false;
        Direction current11 = null;
        int cost11 = 2147483647;
        pos = new MapLocation(bot.x + -1, bot.y + -1);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open11 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost11 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current11 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open11 = true;
            cost11 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open12 = false;
        Direction current12 = null;
        int cost12 = 2147483647;
        pos = new MapLocation(bot.x + -1, bot.y + 0);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open12 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost12 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current12 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open12 = true;
            cost12 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open13 = false;
        Direction current13 = null;
        int cost13 = 2147483647;
        pos = new MapLocation(bot.x + -1, bot.y + 1);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open13 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost13 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current13 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open13 = true;
            cost13 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open14 = false;
        Direction current14 = null;
        int cost14 = 2147483647;
        pos = new MapLocation(bot.x + -1, bot.y + 2);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open14 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost14 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current14 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open14 = true;
            cost14 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open20 = false;
        Direction current20 = null;
        int cost20 = 2147483647;
        pos = new MapLocation(bot.x + 0, bot.y + -2);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open20 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost20 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current20 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open20 = true;
            cost20 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open21 = false;
        Direction current21 = null;
        int cost21 = 2147483647;
        pos = new MapLocation(bot.x + 0, bot.y + -1);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open21 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost21 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current21 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open21 = true;
            cost21 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open22 = false;
        Direction current22 = null;
        int cost22 = 2147483647;
        pos = new MapLocation(bot.x + 0, bot.y + 0);
        open22 = true;
        currentDir = rc.senseMapInfo(pos).getCurrentDirection();
        cost22 = 100 * pos.add(currentDir).distanceSquaredTo(target);
        current22 = currentDir;
        boolean open23 = false;
        Direction current23 = null;
        int cost23 = 2147483647;
        pos = new MapLocation(bot.x + 0, bot.y + 1);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open23 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost23 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current23 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open23 = true;
            cost23 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open24 = false;
        Direction current24 = null;
        int cost24 = 2147483647;
        pos = new MapLocation(bot.x + 0, bot.y + 2);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open24 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost24 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current24 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open24 = true;
            cost24 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open30 = false;
        Direction current30 = null;
        int cost30 = 2147483647;
        pos = new MapLocation(bot.x + 1, bot.y + -2);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open30 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost30 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current30 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open30 = true;
            cost30 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open31 = false;
        Direction current31 = null;
        int cost31 = 2147483647;
        pos = new MapLocation(bot.x + 1, bot.y + -1);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open31 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost31 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current31 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open31 = true;
            cost31 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open32 = false;
        Direction current32 = null;
        int cost32 = 2147483647;
        pos = new MapLocation(bot.x + 1, bot.y + 0);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open32 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost32 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current32 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open32 = true;
            cost32 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open33 = false;
        Direction current33 = null;
        int cost33 = 2147483647;
        pos = new MapLocation(bot.x + 1, bot.y + 1);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open33 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost33 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current33 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open33 = true;
            cost33 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open34 = false;
        Direction current34 = null;
        int cost34 = 2147483647;
        pos = new MapLocation(bot.x + 1, bot.y + 2);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open34 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost34 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current34 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open34 = true;
            cost34 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open40 = false;
        Direction current40 = null;
        int cost40 = 2147483647;
        pos = new MapLocation(bot.x + 2, bot.y + -2);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open40 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost40 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current40 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open40 = true;
            cost40 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open41 = false;
        Direction current41 = null;
        int cost41 = 2147483647;
        pos = new MapLocation(bot.x + 2, bot.y + -1);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open41 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost41 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current41 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open41 = true;
            cost41 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open42 = false;
        Direction current42 = null;
        int cost42 = 2147483647;
        pos = new MapLocation(bot.x + 2, bot.y + 0);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open42 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost42 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current42 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open42 = true;
            cost42 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open43 = false;
        Direction current43 = null;
        int cost43 = 2147483647;
        pos = new MapLocation(bot.x + 2, bot.y + 1);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open43 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost43 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current43 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open43 = true;
            cost43 = 100 * pos.distanceSquaredTo(target);
        }
        boolean open44 = false;
        Direction current44 = null;
        int cost44 = 2147483647;
        pos = new MapLocation(bot.x + 2, bot.y + 2);
        if (rc.canSenseLocation(pos)) {
            if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {
                open44 = true;
                currentDir = rc.senseMapInfo(pos).getCurrentDirection();
                cost44 = 100 * pos.add(currentDir).distanceSquaredTo(target);
                current44 = currentDir;
            }
        } else if (rc.onTheMap(pos)) {
            open44 = true;
            cost44 = 100 * pos.distanceSquaredTo(target);
        }

        int minCost = 2147483647;
        Direction dir = null;
        int newcost00 = 2147483647;
        int newcost01 = 2147483647;
        int newcost02 = 2147483647;
        int newcost03 = 2147483647;
        int newcost04 = 2147483647;
        int newcost10 = 2147483647;
        int newcost11 = 2147483647;
        int newcost12 = 2147483647;
        int newcost13 = 2147483647;
        int newcost14 = 2147483647;
        int newcost20 = 2147483647;
        int newcost21 = 2147483647;
        int newcost22 = 2147483647;
        int newcost23 = 2147483647;
        int newcost24 = 2147483647;
        int newcost30 = 2147483647;
        int newcost31 = 2147483647;
        int newcost32 = 2147483647;
        int newcost33 = 2147483647;
        int newcost34 = 2147483647;
        int newcost40 = 2147483647;
        int newcost41 = 2147483647;
        int newcost42 = 2147483647;
        int newcost43 = 2147483647;
        int newcost44 = 2147483647;
        minCost = cost00;
        if (open01 && (current01 == null || current01 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.WEST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        newcost00 = minCost;
        minCost = cost01;
        if (open02 && (current02 == null || current02 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.WEST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open00 && (current00 == null || current00 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost00 + 1);
        }
        newcost01 = minCost;
        minCost = cost02;
        if (open03 && (current03 == null || current03 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.WEST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        newcost02 = minCost;
        minCost = cost03;
        if (open04 && (current04 == null || current04 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost04 + 1);
        }
        if (open14 && (current14 == null || current14 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.WEST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        newcost03 = minCost;
        minCost = cost04;
        if (open14 && (current14 == null || current14 != Direction.WEST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        newcost04 = minCost;
        minCost = cost10;
        if (open11 && (current11 == null || current11 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.WEST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open00 && (current00 == null || current00 != Direction.EAST)) {
            minCost = Math.min(minCost, cost00 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        newcost10 = minCost;
        minCost = cost11;
        if (open12 && (current12 == null || current12 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.WEST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open00 && (current00 == null || current00 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost00 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.EAST)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        newcost11 = minCost;
        minCost = cost12;
        if (open13 && (current13 == null || current13 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.WEST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.EAST)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        newcost12 = minCost;
        minCost = cost13;
        if (open14 && (current14 == null || current14 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        if (open24 && (current24 == null || current24 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.WEST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.EAST)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        if (open04 && (current04 == null || current04 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost04 + 1);
        }
        newcost13 = minCost;
        minCost = cost14;
        if (open24 && (current24 == null || current24 != Direction.WEST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        if (open04 && (current04 == null || current04 != Direction.EAST)) {
            minCost = Math.min(minCost, cost04 + 1);
        }
        newcost14 = minCost;
        minCost = cost20;
        if (open21 && (current21 == null || current21 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.WEST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.EAST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        newcost20 = minCost;
        minCost = cost21;
        if (open22 && (current22 == null || current22 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.WEST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.EAST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        newcost21 = minCost;
        minCost = cost22;
        if (open23 && (current23 == null || current23 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.WEST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.EAST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        newcost22 = minCost;
        minCost = cost23;
        if (open24 && (current24 == null || current24 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        if (open34 && (current34 == null || current34 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.WEST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.EAST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open14 && (current14 == null || current14 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        newcost23 = minCost;
        minCost = cost24;
        if (open34 && (current34 == null || current34 != Direction.WEST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open14 && (current14 == null || current14 != Direction.EAST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        newcost24 = minCost;
        minCost = cost30;
        if (open31 && (current31 == null || current31 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open40 && (current40 == null || current40 != Direction.WEST)) {
            minCost = Math.min(minCost, cost40 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.EAST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        newcost30 = minCost;
        minCost = cost31;
        if (open32 && (current32 == null || current32 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.WEST)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open40 && (current40 == null || current40 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost40 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.EAST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        newcost31 = minCost;
        minCost = cost32;
        if (open33 && (current33 == null || current33 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open43 && (current43 == null || current43 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.WEST)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.EAST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        newcost32 = minCost;
        minCost = cost33;
        if (open34 && (current34 == null || current34 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        if (open44 && (current44 == null || current44 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost44 + 1);
        }
        if (open43 && (current43 == null || current43 != Direction.WEST)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.EAST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open24 && (current24 == null || current24 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        newcost33 = minCost;
        minCost = cost34;
        if (open44 && (current44 == null || current44 != Direction.WEST)) {
            minCost = Math.min(minCost, cost44 + 1);
        }
        if (open43 && (current43 == null || current43 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open24 && (current24 == null || current24 != Direction.EAST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        newcost34 = minCost;
        minCost = cost40;
        if (open41 && (current41 == null || current41 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.EAST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        newcost40 = minCost;
        minCost = cost41;
        if (open42 && (current42 == null || current42 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open40 && (current40 == null || current40 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost40 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.EAST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        newcost41 = minCost;
        minCost = cost42;
        if (open43 && (current43 == null || current43 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.EAST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        newcost42 = minCost;
        minCost = cost43;
        if (open44 && (current44 == null || current44 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost44 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.EAST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open34 && (current34 == null || current34 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        newcost43 = minCost;
        minCost = cost44;
        if (open43 && (current43 == null || current43 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open34 && (current34 == null || current34 != Direction.EAST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        newcost44 = minCost;
        cost00 = newcost00;
        cost01 = newcost01;
        cost02 = newcost02;
        cost03 = newcost03;
        cost04 = newcost04;
        cost10 = newcost10;
        cost11 = newcost11;
        cost12 = newcost12;
        cost13 = newcost13;
        cost14 = newcost14;
        cost20 = newcost20;
        cost21 = newcost21;
        cost22 = newcost22;
        cost23 = newcost23;
        cost24 = newcost24;
        cost30 = newcost30;
        cost31 = newcost31;
        cost32 = newcost32;
        cost33 = newcost33;
        cost34 = newcost34;
        cost40 = newcost40;
        cost41 = newcost41;
        cost42 = newcost42;
        cost43 = newcost43;
        cost44 = newcost44;
        minCost = cost00;
        if (open01 && (current01 == null || current01 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.WEST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        newcost00 = minCost;
        minCost = cost01;
        if (open02 && (current02 == null || current02 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.WEST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open00 && (current00 == null || current00 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost00 + 1);
        }
        newcost01 = minCost;
        minCost = cost02;
        if (open03 && (current03 == null || current03 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.WEST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        newcost02 = minCost;
        minCost = cost03;
        if (open04 && (current04 == null || current04 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost04 + 1);
        }
        if (open14 && (current14 == null || current14 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.WEST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        newcost03 = minCost;
        minCost = cost04;
        if (open14 && (current14 == null || current14 != Direction.WEST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        newcost04 = minCost;
        minCost = cost10;
        if (open11 && (current11 == null || current11 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.WEST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open00 && (current00 == null || current00 != Direction.EAST)) {
            minCost = Math.min(minCost, cost00 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        newcost10 = minCost;
        minCost = cost11;
        if (open12 && (current12 == null || current12 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.WEST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open00 && (current00 == null || current00 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost00 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.EAST)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        newcost11 = minCost;
        minCost = cost12;
        if (open13 && (current13 == null || current13 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.WEST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.EAST)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        newcost12 = minCost;
        minCost = cost13;
        if (open14 && (current14 == null || current14 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        if (open24 && (current24 == null || current24 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.WEST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.EAST)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        if (open04 && (current04 == null || current04 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost04 + 1);
        }
        newcost13 = minCost;
        minCost = cost14;
        if (open24 && (current24 == null || current24 != Direction.WEST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        if (open04 && (current04 == null || current04 != Direction.EAST)) {
            minCost = Math.min(minCost, cost04 + 1);
        }
        newcost14 = minCost;
        minCost = cost20;
        if (open21 && (current21 == null || current21 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.WEST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.EAST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        newcost20 = minCost;
        minCost = cost21;
        if (open22 && (current22 == null || current22 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.WEST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.EAST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        newcost21 = minCost;
        minCost = cost22;
        if (open23 && (current23 == null || current23 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.WEST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.EAST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        newcost22 = minCost;
        minCost = cost23;
        if (open24 && (current24 == null || current24 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        if (open34 && (current34 == null || current34 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.WEST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.EAST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open14 && (current14 == null || current14 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        newcost23 = minCost;
        minCost = cost24;
        if (open34 && (current34 == null || current34 != Direction.WEST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open14 && (current14 == null || current14 != Direction.EAST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        newcost24 = minCost;
        minCost = cost30;
        if (open31 && (current31 == null || current31 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open40 && (current40 == null || current40 != Direction.WEST)) {
            minCost = Math.min(minCost, cost40 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.EAST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        newcost30 = minCost;
        minCost = cost31;
        if (open32 && (current32 == null || current32 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.WEST)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open40 && (current40 == null || current40 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost40 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.EAST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        newcost31 = minCost;
        minCost = cost32;
        if (open33 && (current33 == null || current33 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open43 && (current43 == null || current43 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.WEST)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.EAST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        newcost32 = minCost;
        minCost = cost33;
        if (open34 && (current34 == null || current34 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        if (open44 && (current44 == null || current44 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost44 + 1);
        }
        if (open43 && (current43 == null || current43 != Direction.WEST)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.EAST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open24 && (current24 == null || current24 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        newcost33 = minCost;
        minCost = cost34;
        if (open44 && (current44 == null || current44 != Direction.WEST)) {
            minCost = Math.min(minCost, cost44 + 1);
        }
        if (open43 && (current43 == null || current43 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open24 && (current24 == null || current24 != Direction.EAST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        newcost34 = minCost;
        minCost = cost40;
        if (open41 && (current41 == null || current41 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.EAST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        newcost40 = minCost;
        minCost = cost41;
        if (open42 && (current42 == null || current42 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open40 && (current40 == null || current40 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost40 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.EAST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        newcost41 = minCost;
        minCost = cost42;
        if (open43 && (current43 == null || current43 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.EAST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        newcost42 = minCost;
        minCost = cost43;
        if (open44 && (current44 == null || current44 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost44 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.EAST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open34 && (current34 == null || current34 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        newcost43 = minCost;
        minCost = cost44;
        if (open43 && (current43 == null || current43 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open34 && (current34 == null || current34 != Direction.EAST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        newcost44 = minCost;
        cost00 = newcost00;
        cost01 = newcost01;
        cost02 = newcost02;
        cost03 = newcost03;
        cost04 = newcost04;
        cost10 = newcost10;
        cost11 = newcost11;
        cost12 = newcost12;
        cost13 = newcost13;
        cost14 = newcost14;
        cost20 = newcost20;
        cost21 = newcost21;
        cost22 = newcost22;
        cost23 = newcost23;
        cost24 = newcost24;
        cost30 = newcost30;
        cost31 = newcost31;
        cost32 = newcost32;
        cost33 = newcost33;
        cost34 = newcost34;
        cost40 = newcost40;
        cost41 = newcost41;
        cost42 = newcost42;
        cost43 = newcost43;
        cost44 = newcost44;
        minCost = cost00;
        if (open01 && (current01 == null || current01 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.WEST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        newcost00 = minCost;
        minCost = cost01;
        if (open02 && (current02 == null || current02 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.WEST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open00 && (current00 == null || current00 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost00 + 1);
        }
        newcost01 = minCost;
        minCost = cost02;
        if (open03 && (current03 == null || current03 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.WEST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        newcost02 = minCost;
        minCost = cost03;
        if (open04 && (current04 == null || current04 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost04 + 1);
        }
        if (open14 && (current14 == null || current14 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.WEST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        newcost03 = minCost;
        minCost = cost04;
        if (open14 && (current14 == null || current14 != Direction.WEST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        newcost04 = minCost;
        minCost = cost10;
        if (open11 && (current11 == null || current11 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.WEST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open00 && (current00 == null || current00 != Direction.EAST)) {
            minCost = Math.min(minCost, cost00 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        newcost10 = minCost;
        minCost = cost11;
        if (open12 && (current12 == null || current12 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.WEST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open00 && (current00 == null || current00 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost00 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.EAST)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        newcost11 = minCost;
        minCost = cost12;
        if (open13 && (current13 == null || current13 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.WEST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open01 && (current01 == null || current01 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost01 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.EAST)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        newcost12 = minCost;
        minCost = cost13;
        if (open14 && (current14 == null || current14 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        if (open24 && (current24 == null || current24 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.WEST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open02 && (current02 == null || current02 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost02 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.EAST)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        if (open04 && (current04 == null || current04 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost04 + 1);
        }
        newcost13 = minCost;
        minCost = cost14;
        if (open24 && (current24 == null || current24 != Direction.WEST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open03 && (current03 == null || current03 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost03 + 1);
        }
        if (open04 && (current04 == null || current04 != Direction.EAST)) {
            minCost = Math.min(minCost, cost04 + 1);
        }
        newcost14 = minCost;
        minCost = cost20;
        if (open21 && (current21 == null || current21 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.WEST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.EAST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        newcost20 = minCost;
        minCost = cost21;
        if (open22 && (current22 == null || current22 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.WEST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open10 && (current10 == null || current10 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost10 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.EAST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        newcost21 = minCost;
        minCost = cost22;
        if (open23 && (current23 == null || current23 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.WEST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open11 && (current11 == null || current11 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost11 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.EAST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        newcost22 = minCost;
        minCost = cost23;
        if (open24 && (current24 == null || current24 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        if (open34 && (current34 == null || current34 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.WEST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open12 && (current12 == null || current12 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost12 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.EAST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open14 && (current14 == null || current14 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        newcost23 = minCost;
        minCost = cost24;
        if (open34 && (current34 == null || current34 != Direction.WEST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open13 && (current13 == null || current13 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost13 + 1);
        }
        if (open14 && (current14 == null || current14 != Direction.EAST)) {
            minCost = Math.min(minCost, cost14 + 1);
        }
        newcost24 = minCost;
        minCost = cost30;
        if (open31 && (current31 == null || current31 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open40 && (current40 == null || current40 != Direction.WEST)) {
            minCost = Math.min(minCost, cost40 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.EAST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        newcost30 = minCost;
        minCost = cost31;
        if (open32 && (current32 == null || current32 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.WEST)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open40 && (current40 == null || current40 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost40 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open20 && (current20 == null || current20 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost20 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.EAST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        newcost31 = minCost;
        minCost = cost32;
        if (open33 && (current33 == null || current33 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open43 && (current43 == null || current43 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.WEST)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open21 && (current21 == null || current21 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost21 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.EAST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        newcost32 = minCost;
        minCost = cost33;
        if (open34 && (current34 == null || current34 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        if (open44 && (current44 == null || current44 != Direction.SOUTHWEST)) {
            minCost = Math.min(minCost, cost44 + 1);
        }
        if (open43 && (current43 == null || current43 != Direction.WEST)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open22 && (current22 == null || current22 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost22 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.EAST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open24 && (current24 == null || current24 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        newcost33 = minCost;
        minCost = cost34;
        if (open44 && (current44 == null || current44 != Direction.WEST)) {
            minCost = Math.min(minCost, cost44 + 1);
        }
        if (open43 && (current43 == null || current43 != Direction.NORTHWEST)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open23 && (current23 == null || current23 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost23 + 1);
        }
        if (open24 && (current24 == null || current24 != Direction.EAST)) {
            minCost = Math.min(minCost, cost24 + 1);
        }
        newcost34 = minCost;
        minCost = cost40;
        if (open41 && (current41 == null || current41 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.EAST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        newcost40 = minCost;
        minCost = cost41;
        if (open42 && (current42 == null || current42 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open40 && (current40 == null || current40 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost40 + 1);
        }
        if (open30 && (current30 == null || current30 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost30 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.EAST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        newcost41 = minCost;
        minCost = cost42;
        if (open43 && (current43 == null || current43 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open41 && (current41 == null || current41 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost41 + 1);
        }
        if (open31 && (current31 == null || current31 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost31 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.EAST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        newcost42 = minCost;
        minCost = cost43;
        if (open44 && (current44 == null || current44 != Direction.SOUTH)) {
            minCost = Math.min(minCost, cost44 + 1);
        }
        if (open42 && (current42 == null || current42 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost42 + 1);
        }
        if (open32 && (current32 == null || current32 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost32 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.EAST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open34 && (current34 == null || current34 != Direction.SOUTHEAST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        newcost43 = minCost;
        minCost = cost44;
        if (open43 && (current43 == null || current43 != Direction.NORTH)) {
            minCost = Math.min(minCost, cost43 + 1);
        }
        if (open33 && (current33 == null || current33 != Direction.NORTHEAST)) {
            minCost = Math.min(minCost, cost33 + 1);
        }
        if (open34 && (current34 == null || current34 != Direction.EAST)) {
            minCost = Math.min(minCost, cost34 + 1);
        }
        newcost44 = minCost;
        cost00 = newcost00;
        cost01 = newcost01;
        cost02 = newcost02;
        cost03 = newcost03;
        cost04 = newcost04;
        cost10 = newcost10;
        cost11 = newcost11;
        cost12 = newcost12;
        cost13 = newcost13;
        cost14 = newcost14;
        cost20 = newcost20;
        cost21 = newcost21;
        cost22 = newcost22;
        cost23 = newcost23;
        cost24 = newcost24;
        cost30 = newcost30;
        cost31 = newcost31;
        cost32 = newcost32;
        cost33 = newcost33;
        cost34 = newcost34;
        cost40 = newcost40;
        cost41 = newcost41;
        cost42 = newcost42;
        cost43 = newcost43;
        cost44 = newcost44;

        Direction bestDir = Direction.CENTER;
        int bestCost = 2147483647;
        if (open23 && (current23 == null || current23 != Direction.SOUTH)) {
            if (cost23 < bestCost) {
                bestCost = cost23;
                bestDir = Direction.NORTH;
            }
        }
        if (open33 && (current33 == null || current33 != Direction.SOUTHWEST)) {
            if (cost33 < bestCost) {
                bestCost = cost33;
                bestDir = Direction.NORTHEAST;
            }
        }
        if (open32 && (current32 == null || current32 != Direction.WEST)) {
            if (cost32 < bestCost) {
                bestCost = cost32;
                bestDir = Direction.EAST;
            }
        }
        if (open31 && (current31 == null || current31 != Direction.NORTHWEST)) {
            if (cost31 < bestCost) {
                bestCost = cost31;
                bestDir = Direction.SOUTHEAST;
            }
        }
        if (open21 && (current21 == null || current21 != Direction.NORTH)) {
            if (cost21 < bestCost) {
                bestCost = cost21;
                bestDir = Direction.SOUTH;
            }
        }
        if (open11 && (current11 == null || current11 != Direction.NORTHEAST)) {
            if (cost11 < bestCost) {
                bestCost = cost11;
                bestDir = Direction.SOUTHWEST;
            }
        }
        if (open12 && (current12 == null || current12 != Direction.EAST)) {
            if (cost12 < bestCost) {
                bestCost = cost12;
                bestDir = Direction.WEST;
            }
        }
        if (open13 && (current13 == null || current13 != Direction.SOUTHEAST)) {
            if (cost13 < bestCost) {
                bestCost = cost13;
                bestDir = Direction.NORTHWEST;
            }
        }


        if (rc.canMove(bestDir)) {
            rc.move(bestDir);
        }
    }
}


