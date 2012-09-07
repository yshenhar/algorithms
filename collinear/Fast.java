import java.util.Arrays;

public class Fast extends Brute {

    protected static void output(Point[] points, int start, int stop) {
        Point[] scratch = new Point[stop - start];
        for (int i = start; i < stop; i++)
            scratch[i] = points[i];
        Arrays.sort(scratch);
        printLineSegment(scratch);
        draw(scratch);
    }

    public static void main(String[] args) {
        setUpDrawing();

        Point[] points = readInput(args[0]);
        int n = points.length;

        Point[] scratch = new Point[n];
        int start, stop; // Pointers to the beginning and end of runs.
        double last, next; // To hold subsequent slopes.
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++)
                scratch[j] = points[j];
            Arrays.sort(scratch, points[i].SLOPE_ORDER);
            // 0 1 2 2 2 3
            stop = -1;
            do {
                // Invariant: We're looking at the subarray that begins right
                // after the last run.
                next = scratch[++stop].slopeTo(scratch[++stop]);
                // Gallop the stop pointer rightward to look for a run.
                do {
                    last = next;
                    next = points[stop].slopeTo(scratch[++stop]);
                } while (stop < n - 1 && last != next);
                if (stop == n - 1)
                    break;
                start = stop - 1;
                // Leave start in place and move stop rightward to end of run.
                while (stop < n - 1 && last == scratch[stop].slopeTo(scratch[++stop]));
                if (stop - start > MIN_POINTS)
                    output(scratch, start, stop);
            } while (stop < n - 3);
        }
        StdDraw.show(0);
    }
}
