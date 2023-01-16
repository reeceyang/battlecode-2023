package hognosekaireece;

import battlecode.common.MapLocation;

public class Line {
    public MapLocation start;
    public MapLocation target;

    public Line(MapLocation p1, MapLocation p2) {
        this.start = p1;
        this.target = p2;
    }

    // copied from java.awt.geom.Line2D!
    public static int relativeCCW(double x1, double y1,
                                  double x2, double y2,
                                  double px, double py) {
        x2 -= x1;
        y2 -= y1;
        px -= x1;
        py -= y1;
        double ccw = px * y2 - py * x2;
        if (ccw == 0.0) {
            // The point is colinear, classify based on which side of
            // the segment the point falls on.  We can calculate a
            // relative value using the projection of px,py onto the
            // segment - a negative value indicates the point projects
            // outside of the segment in the direction of the particular
            // endpoint used as the origin for the projection.
            ccw = px * x2 + py * y2;
            if (ccw > 0.0) {
                // Reverse the projection to be relative to the original x2,y2
                // x2 and y2 are simply negated.
                // px and py need to have (x2 - x1) or (y2 - y1) subtracted
                //    from them (based on the original values)
                // Since we really want to get a positive answer when the
                //    point is "beyond (x2,y2)", then we want to calculate
                //    the inverse anyway - thus we leave x2 & y2 negated.
                px -= x2;
                py -= y2;
                ccw = px * x2 + py * y2;
                if (ccw < 0.0) {
                    ccw = 0.0;
                }
            }
        }
        return (ccw < 0.0) ? -1 : ((ccw > 0.0) ? 1 : 0);
    }
    public static boolean linesIntersect(double x1, double y1,
                                         double x2, double y2,
                                         double x3, double y3,
                                         double x4, double y4)
    {
        return ((relativeCCW(x1, y1, x2, y2, x3, y3) *
                relativeCCW(x1, y1, x2, y2, x4, y4) <= 0)
                && (relativeCCW(x3, y3, x4, y4, x1, y1) *
                relativeCCW(x3, y3, x4, y4, x2, y2) <= 0));
    }

    public boolean intersectsTile(MapLocation loc) {
//        boolean bottom = Line2D.linesIntersect(loc.x - 0.5, loc.y - 0.5, loc.x + 0.5, loc.y - 0.5, p1.x, p1.y, p2.x, p2.y);
//        boolean top = Line2D.linesIntersect(loc.x - 0.5, loc.y + 0.5, loc.x + 0.5, loc.y + 0.5, p1.x, p1.y, p2.x, p2.y);
//        boolean left = Line2D.linesIntersect(loc.x - 0.5, loc.y - 0.5, loc.x - 0.5, loc.y + 0.5, p1.x, p1.y, p2.x, p2.y);
//        boolean right = Line2D.linesIntersect(loc.x + 0.5, loc.y - 0.5, loc.x + 0.5, loc.y + 0.5, p1.x, p1.y, p2.x, p2.y);
//        return bottom || top || left || right;

        // if the line intersects the 'X' through the center of the tile then the line intersects the tile
//        boolean up = linesIntersect(loc.x - 0.5, loc.y - 0.5, loc.x + 0.5, loc.y + 0.5, start.x, start.y, target.x, target.y);
//        boolean down = linesIntersect(loc.x - 0.5, loc.y + 0.5, loc.x + 0.5, loc.y - 0.5, start.x, start.y, target.x, target.y);
        // do like a 2-tile wide square where the line can intersect
        boolean up = linesIntersect(loc.x - 1, loc.y - 1, loc.x + 1, loc.y + 1, start.x, start.y, target.x, target.y);
        boolean down = linesIntersect(loc.x - 1, loc.y + 1, loc.x + 1, loc.y - 1, start.x, start.y, target.x, target.y);
        return up || down;
    }

    @Override
    public String toString() {
        return "Line{" +
                "start=" + start +
                ", target=" + target +
                '}';
    }
}
