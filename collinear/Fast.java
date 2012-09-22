import java.util.Arrays;

public class Fast {

    // Number of collinear points to look for.
    private static final int MIN_POINTS = 4;

    private static void setUpDrawing() {
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        StdDraw.show(0);
    }

    /**
     * Read input files of points
     *
     * First line gives the number of points; each subsequent line gives two
     * integers, the x and the y coordinates.
     *
     * @param filename  a file name relative to the current working directory
     * @return          an array of Point objects.
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
        if (cmp < 0) cmp = -1;
        else cmp = 1;
        int nextcmp;
        while (i < n - 1) {
            nextcmp = points[i].compareTo(points[++i]);
            if      (nextcmp < 0) nextcmp = -1;
            else if (nextcmp > 0) nextcmp = 1;
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
        points[0].drawTo(points[points.length - 1]);
    }

    private static void output(Point origin, Point[] points, int start, int stop) {
        Point[] scratch = new Point[stop - start + 1];
        scratch[0] = origin;
        for (int i = start; i < stop; i++)
            scratch[i - start + 1] = points[i];
        assert scratch[stop - start] != null;
        Arrays.sort(scratch);
        printLineSegment(scratch);
        draw(scratch);
    }

    public static void main(String[] args) {
        setUpDrawing();

        Point[] points = readInput(args[0]);
        int n = points.length;

        Arrays.sort(points);
        int start, stop; // Pointers to the beginning and end of runs.
        double last, next; // To hold subsequent slopes.
        for (int i = 0; i < n - MIN_POINTS; i++) {
            points[i].draw();
            if (i > 0 && points[i].compareTo(points[i - 1]) == 0)
                continue;
            Point[] scratch = new Point[n - i];
            for (int j = i; j < n; j++)
                scratch[j - i] = points[j];
            Arrays.sort(scratch, points[i].SLOPE_ORDER);
            stop = 0;
            do {
                // Invariant: We're looking at the subarray that begins right
                // after the last run.
                next = points[i].slopeTo(scratch[stop]);
                // Gallop the stop pointer rightward to look for a run.
                do {
                    stop += 1;
                    last = next;
                    next = points[i].slopeTo(scratch[stop]);
                } while (stop < n - i - 1 && last != next);
                start = stop - 1;
                while (stop < n - i && last == points[i].slopeTo(scratch[stop]))
                    stop++;
                if (stop - start + 1 >= MIN_POINTS) // Add one for points[i].
                    output(points[i], scratch, start, stop);
            } while (stop < n - i - 1);
        }
        StdDraw.show(0);
    }
}
