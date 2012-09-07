/*************************************************************************
 * Name: Billy Schwartz
 * Email: wkschwartz@gmail.com
 *
 * Compilation:  javac Point.java
 * Execution:
 * Dependencies: StdDraw.java
 *
 * Description: An immutable data type for points in the plane.
 *
 *************************************************************************/

import java.util.Comparator;

public class Point implements Comparable<Point> {

    // compare points by slope
    public final Comparator<Point> SLOPE_ORDER;

    private final int x;                              // x coordinate
    private final int y;                              // y coordinate

    // create the point (x, y)
    public Point(int x, int y) {
        /* DO NOT MODIFY */
        this.x = x;
        this.y = y;
        SLOPE_ORDER = new SlopeOrder();
    }

    // plot this point to standard drawing
    public void draw() {
        /* DO NOT MODIFY */
        StdDraw.point(x, y);
    }

    // draw line between this point and that point to standard drawing
    public void drawTo(Point that) {
        /* DO NOT MODIFY */
        StdDraw.line(this.x, this.y, that.x, that.y);
    }

    // slope between this point and that point
    public double slopeTo(Point that) {
        if (that.x == this.x) {
            if (that.y == this.y)
                return -Double.NEGATIVE_INFINITY;
            return Double.POSITIVE_INFINITY;
        }
        if (that.y == this.y)
            return 0.0;
        return ((double) that.y - this.y) / (that.x - this.x);
    }

    // is this point lexicographically smaller than that one?
    // comparing y-coordinates and breaking ties by x-coordinates
    public int compareTo(Point that) {
        if (this.y == that.y)
            return this.x - that.x;
        return this.y - that.y;
    }

    private class SlopeOrder implements Comparator<Point> {
        public int compare(Point a, Point c) {
            double res = slopeTo(a) - slopeTo(c);
            if      (res < 0.0) return -1;
            else if (res > 0.0) return  1;
            else                return  0;
        }
    }

    // return string representation of this point
    public String toString() {
        /* DO NOT MODIFY */
        return "(" + x + ", " + y + ")";
    }

    // unit test
    public static void main(String[] args) {
        /* YOUR CODE HERE */
    }
}
