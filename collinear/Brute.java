/*************************************************************************
 * Name: Billy Schwartz
 * Email: wkschwartz@gmail.com
 *
 * Compilation:  javac Brute.java
 * Execution:    java Brute points.txt
 * Dependencies: StdDraw.java, Point.java
 *
 * Description:Find four collinear points in a set of points.
 *
 *  Print out line segments and draw them. The order of growth of the running
 *  time of the program should be N^4 in the worst case and it should use space
 *  proportional to N.
 *
 *************************************************************************/

import java.util.Iterator;
import java.util.Arrays;

public class Brute {

    /** Read input files of points
     *
     *  First line gives the number of points; each subsequent line gives two
     *  integers, the x and the y coordinates.
     *
     *  Return an array of Point objects.
     */
    private static Point[] readInput(String filename) {
        In in = new In(filename);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            points[i] = new Point(in.readInt(), in.readInt());
        }
        return points;
    }

    // Confirm an array of points is sorted
    private static boolean sorted(Point[] points) {
        int n = points.length;
        if (n < 2)
            return true;
        int i = 0;
        int cmp = 0;
        do {
            cmp = points[i].compareTo(points[++i]);
        } while (cmp == 0 && i < n);
        assert cmp != 0 || i == n; // If i == n then cmp's value doesn't matter.
        cmp = (cmp < 0) ? -1 : 1;
        int nextcmp;
        while (i < n - 1) {
            nextcmp = points[i].compareTo(points[++i]);
            if (nextcmp != 0)
                nextcmp = (nextcmp < 0) ? -1 : 1;
            if (cmp != nextcmp && nextcmp != 0)
                return false;
        }
        return true;
    }

    /** Print to stdout a textual representation of a line segment
     *
     *  points is a SORTED array of Point objects.
     *
     *  Example:
     *  (10000, 0) -> (7000, 3000) -> (3000, 7000) -> (0, 10000)
     */
    private static void printLineSegment(Point[] points) {
        assert sorted(points);
        int end = points.length - 1;
        if (end > 0) {
            for (int i = 0; i < end; i++) {
                System.out.print(points[i] + " -> ");
            }
        }
        System.out.println(points[end]);
    }

    /** Draw line segments connecting a SORTED array of points
     *
     *  points is a SORTED array of Point objects
     */
    private static void draw(Point[] points) {
        assert sorted(points);
        for (int i = points.length - 1; i > 0;) {
            points[i].drawTo(points[--i]);
        }
        points[0].draw();
    }

    // Output a given set of points found to be collinear. points is an array.
    private static void output(Point[] points) {
        Arrays.sort(points);
        printLineSegment(points);
        draw(points);
    }

    // Checks whether the points in the array are collinear
    private static boolean collinear(Point[] points) {
        if (points.length == 2)
            return true;
        Point base = points[0]; // Error if nonsensical number of points.
        double slope = base.slopeTo(points[1]);
        for (int i = 2; i < points.length; ) {
            // May need to improve equality test.
            if (slope != base.slopeTo(points[i++]))
                return false;
        }
        return true;
    }

    public static void main(String[] args) {
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        StdDraw.show(0);
        Point[] points = readInput(args[0]);

        /** Iterate through all combinations

        This (horrible) nested loop works like an odometer: the rightmost,
        dial like the inner-most loop, spins the fastest. Starting each nested
        loop at one greater than its parent loop keeps the entries of the f
        vector from repeating each other's numbers. The f vector then translates
        from selections in the points vector to positions in the result vector.
        We could also write this loop with index variables h, i, j, and k, and
        written the `results[g] = points[f[g]]` line in four lines. Hopefully
        this is easier to read.
        */
        Point[] first3 = new Point[3];
        Point[] result = new Point[4];
        int[] f = {0, 0, 0, 0};
        int n = points.length;
        for             (f[0] = 0       ; f[0] < n; f[0]++) {
            points[f[0]].draw();
            for         (f[1] = f[0] + 1; f[1] < n; f[1]++) {
                for     (f[2] = f[1] + 1; f[2] < n; f[2]++) {
                    for (int g = 0; g < 3; g++) {
                        first3[g] = points[f[g]];
                    }
                    if (!collinear(first3)) {
                        continue;
                    }
                    for (f[3] = f[2] + 1; f[3] < n; f[3]++) {
                        for (int g = 0; g < 4; g++) {
                            result[g] = points[f[g]];
                        }
                        if (collinear(result)) {
                            output(result);
                        }
                    }
                }
            }
        }

        // Show everything all at once.
        StdDraw.show(0);
    }
}
