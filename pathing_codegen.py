# variable names
COST = 'cost'
CURR = 'current'
OPEN = 'open'

SIZE = 5  # size of the grid
DMIN = -2
DMAX = 2
HOME = 2  # center of the grid
WELL_PENALTY = 3 # better to go around

MAX_V = 2147483647

DIRS = ["Direction.NORTH",
        "Direction.NORTHEAST",
        "Direction.EAST",
        "Direction.SOUTHEAST",
        "Direction.SOUTH",
        "Direction.SOUTHWEST",
        "Direction.WEST",
        "Direction.NORTHWEST",
        ]

DIR_TO_D = {"Direction.NORTH": (0, 1),
            "Direction.NORTHEAST": (1, 1),
            "Direction.EAST": (1, 0),
            "Direction.SOUTHEAST": (1, -1),
            "Direction.SOUTH": (0, -1),
            "Direction.SOUTHWEST": (-1, -1),
            "Direction.WEST": (-1, 0),
            "Direction.NORTHWEST": (-1, 1),
            }

OPPOSITE = {
    "Direction.NORTH": "Direction.SOUTH",
    "Direction.NORTHEAST": "Direction.SOUTHWEST",
    "Direction.EAST": "Direction.WEST",
    "Direction.SOUTHEAST": "Direction.NORTHWEST",
    "Direction.SOUTH": "Direction.NORTH",
    "Direction.SOUTHWEST": "Direction.NORTHEAST",
    "Direction.WEST": "Direction.EAST",
    "Direction.NORTHWEST": "Direction.SOUTHEAST",
}

NUM_DIR = len(DIRS)

def def_vars():
    s = """MapLocation bot = rc.getLocation();
MapLocation pos;
Direction currentDir;
int wellCost;
"""
    for dx in range(DMIN, DMAX + 1):
        for dy in range(DMIN, DMAX + 1):
            array_x = HOME + dx
            array_y = HOME + dy
            s += f"""boolean {OPEN}{array_x}{array_y} = false;
Direction {CURR}{array_x}{array_y} = null;
int {COST}{array_x}{array_y} = {MAX_V};
pos = new MapLocation(bot.x + {dx}, bot.y + {dy});
"""
            # pre-compute whether pos is the bot's current location
            if dx == 0 and dy == 0:
                s += f"""{OPEN}{array_x}{array_y} = true;
currentDir = rc.senseMapInfo(pos).getCurrentDirection();
wellCost = rc.senseWell(pos) == null ? 0 : {WELL_PENALTY};
{COST}{array_x}{array_y} = 100 * (pos.add(currentDir).distanceSquaredTo(target) + wellCost);
{CURR}{array_x}{array_y} = currentDir;
"""
            else:
                s += f"""if (rc.canSenseLocation(pos)) {{
    if (rc.sensePassability(pos) && !rc.isLocationOccupied(pos)) {{
        {OPEN}{array_x}{array_y} = true;
        currentDir = rc.senseMapInfo(pos).getCurrentDirection();
        wellCost = rc.senseWell(pos) == null ? 0 : {WELL_PENALTY};
        {COST}{array_x}{array_y} = 100 * (pos.add(currentDir).distanceSquaredTo(target) + wellCost);
        {CURR}{array_x}{array_y} = currentDir;
    }}
}} else if (rc.onTheMap(pos)) {{
    {OPEN}{array_x}{array_y} = true;
    {COST}{array_x}{array_y} = 100 * pos.distanceSquaredTo(target);
}}
"""
    return s


def relax_graph(iterations):
    # reuse these variables
    s = f'int minCost = {MAX_V};\nDirection dir = null;\n'
    for x in range(SIZE):
        for y in range(SIZE):
            s += f'int new{COST}{x}{y} = {MAX_V};\n'
    for _ in range(iterations):
        for x in range(SIZE):
            for y in range(SIZE):
                s += f"""if ({OPEN}{x}{y}) {{
    minCost = {COST}{x}{y};
"""
                for dir in DIRS:
                    dx, dy = DIR_TO_D[dir]
                    neigh_x = x + dx
                    neigh_y = y + dy
                    if 0 <= neigh_x < SIZE and 0 <= neigh_y < SIZE:
                        # don't go into an inaccessible square or an opposing current
                        s += f"""if ({OPEN}{neigh_x}{neigh_y} && {CURR}{neigh_x}{neigh_y} != {OPPOSITE[dir]}) {{
    minCost = Math.min(minCost, {COST}{neigh_x}{neigh_y} + 1);
}}
"""
                s += f'new{COST}{x}{y} = minCost;\n}}\n'

        # swap the costs with the new costs at the end of the iteration
        for x in range(SIZE):
            for y in range(SIZE):
                s += f'{COST}{x}{y} = new{COST}{x}{y};\n'
        # don't need to swap costs the last iteration - just use new costs for calculating best direction
        # if _ != iterations - 1:
        #     for x in range(SIZE):
        #         for y in range(SIZE):
        #             s += f'{COST}{x}{y} = new{COST}{x}{y};\n'
    return s

def choose_best_dir():
    s = f"""Direction bestDir = Direction.CENTER;
int bestCost = {MAX_V};
"""
    for dir in DIRS:
        dx, dy = DIR_TO_D[dir]
        neigh_x = HOME + dx
        neigh_y = HOME + dy
        s += f"""if ({OPEN}{neigh_x}{neigh_y} && {CURR}{neigh_x}{neigh_y} != {OPPOSITE[dir]}) {{
    if ({COST}{neigh_x}{neigh_y} < bestCost) {{
        bestCost = {COST}{neigh_x}{neigh_y};
        bestDir = {dir};
    }}
}}
"""
    return s

def move_again():
    s = f'bestCost = {MAX_V};\n'
    for dir in DIRS:
        dx, dy = DIR_TO_D[dir]
        x = HOME + dx
        y = HOME + dy
        s += f"""if (bestDir == {dir}) {{
    bestDir = Direction.CENTER;
    Direction current = Direction.CENTER;
"""
        for next_dir in DIRS:
            neigh_x = x + dx
            neigh_y = y + dy
            s += f"""if ({OPEN}{neigh_x}{neigh_y} && {CURR}{neigh_x}{neigh_y} != {OPPOSITE[next_dir]}) {{
    if ({COST}{neigh_x}{neigh_y} < bestCost) {{
        bestCost = {COST}{neigh_x}{neigh_y};
        bestDir = {dir};
        if ({CURR}{neigh_x}{neigh_y} != null) current = {CURR}{neigh_x}{neigh_y};
    }}
}}
"""
        s += f"""if (rc.canMove(bestDir)) {{
        MapLocation next = rc.getLocation().add(bestDir);
        if (!rc.getLocation().isAdjacentTo(target) || (next.isAdjacentTo(target) && next.add(current).isAdjacentTo(target))) {{
            rc.move(bestDir);
            return true;
        }}
    }}
    return false;
}}\n"""

    return s


def get_current_from_bestdir():
    s = ""
    for dir in DIRS:
        dx, dy = DIR_TO_D[dir]
        s += f"""if (bestDir == {dir}) {{
    current = {CURR}{HOME + dx}{HOME + dy};
}}
"""
    return s

def make_method(package):
    return f"""package {package};

import battlecode.common.*;

public class BellmanFord {{
public static boolean doBellmanFord(RobotController rc, MapLocation target, boolean moveTwice) throws GameActionException {{
    {def_vars()}
    {relax_graph(3)}
    {choose_best_dir()}
    
    if (rc.canMove(bestDir)) {{
        MapLocation next = rc.getLocation().add(bestDir);
        Direction current = Direction.CENTER;
        {get_current_from_bestdir()}
        if (!rc.getLocation().isAdjacentTo(target) || (next.isAdjacentTo(target) && next.add(current).isAdjacentTo(target))) {{
            rc.move(bestDir);
        }}
    }}
    
    if (rc.isMovementReady() && moveTwice) {{
        {move_again()}
    }}
    return false;
}}
public static boolean doCheapBellmanFord(RobotController rc, MapLocation target, boolean moveTwice) throws GameActionException {{
    {def_vars()}
    {relax_graph(2)}
    {choose_best_dir()}
    
    if (rc.canMove(bestDir)) {{
        MapLocation next = rc.getLocation().add(bestDir);
        Direction current = Direction.CENTER;
        {get_current_from_bestdir()}
        if (!rc.getLocation().isAdjacentTo(target) || (next.isAdjacentTo(target) && next.add(current).isAdjacentTo(target))) {{
            rc.move(bestDir);
        }}
    }}
    
    if (rc.isMovementReady() && moveTwice) {{
        {move_again()}
    }}
    return false;
}}
public static boolean doCheapestBellmanFord(RobotController rc, MapLocation target, boolean moveTwice) throws GameActionException {{
    {def_vars()}
    {relax_graph(1)}
    {choose_best_dir()}
    
    if (rc.canMove(bestDir)) {{
        MapLocation next = rc.getLocation().add(bestDir);
        Direction current = Direction.CENTER;
        {get_current_from_bestdir()}
        if (!rc.getLocation().isAdjacentTo(target) || (next.isAdjacentTo(target) && next.add(current).isAdjacentTo(target))) {{
            rc.move(bestDir);
        }}
    }}
    
    if (rc.isMovementReady() && moveTwice) {{
        {move_again()}
    }}
    return false;
}}
}}
"""


print(make_method('5anaconda'))
