/*************************************************************************
 *  Author:       William Schwartz
 *  Compilation:  javac PointSET.java
 *  Execution:    java PointSET
 *  Dependencies: Point2D.java, java.util.TreeSet
 *
 *  Represent a set of points in the unit square.
 *
 *************************************************************************/

import java.util.TreeSet;

public class PointSET {

    private TreeSet<Point2D> set;

    // construct an empty set of points
    public PointSET() { set = new TreeSet<Point2D>(); }

    // is the set empty?
    public boolean isEmpty() { return set.isEmpty(); }

    // number of points in the set
    public int size() { return set.size(); }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) { set.add(p); }

    // does the set contain the point p?
    public boolean contains(Point2D p) { return set.contains(p); }

    // draw all of the points to standard draw
    public void draw() {
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        for (Point2D point: set)
            point.draw();
    }

    // all points in the set that are inside the rectangle
    public Iterable<Point2D> range(RectHV rect) {
        TreeSet<Point2D> inside = new TreeSet<Point2D>();
        for (Point2D point: set)
            if (rect.contains(point))
                inside.add(point);
        return inside;
    }

    // a nearest neighbor in the set to p; null if set is empty
    public Point2D nearest(Point2D p) {
        Point2D champPoint = null;
        double champDist2 = Double.POSITIVE_INFINITY;
        double testDist2 = Double.POSITIVE_INFINITY;
        for (Point2D testPoint: set) {
            testDist2 = testPoint.distanceSquaredTo(p);
            if (testDist2 < champDist2) {
                champPoint = testPoint;
                champDist2 = testDist2;
            }
        }
        return champPoint;
    }

}
