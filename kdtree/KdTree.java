/*************************************************************************
 *  @author       William Schwartz
 *  Compilation:  javac KdTree.java
 *  Execution:    java KdTree
 *  Dependencies: Point2D.java
 *
 *  Represent a set of points in the unit square. Operations are
 *  guaranteed to take logarithmic time.
 *
 *************************************************************************/

public class KdTree {

    private static final double  MIN        = 0.0;
    private static final double  MAX        = 1.0;
    private static final boolean VERTICAL   = false;
    private static final boolean HORIZONTAL = true;

    private Node root = null;
    private int  N    = 0;

    private static class Node {
        private Point2D p;      // the point
        private RectHV rect;    // axis-aligned rectangle corresponding to this node
        private Node lb;        // left/bottom subtree
        private Node rt;        // right/top subtree

        private Node(Point2D point, Node parent, boolean horizontal, boolean isLB) {
            p = point;
            rect = makeRect(parent, horizontal, isLB);
            lb = null;
            rt = null;
        }

        private RectHV makeRect(Node parent, boolean horizontal, boolean isLB) {
            if (parent == null)
                return new RectHV(MIN, MIN, MAX, MAX);
            else if (horizontal && isLB)
                return new RectHV(parent.rect.xmin(), parent.rect.ymin(),
                                  parent.p.x(),       parent.rect.ymax());
            else if (horizontal && !isLB)
                return new RectHV(parent.p.x(),       parent.rect.ymin(),
                                  parent.rect.xmax(), parent.rect.ymax());
            else if (!horizontal && isLB)
                return new RectHV(parent.rect.xmin(), parent.rect.ymin(),
                                  parent.rect.xmax(), parent.p.y());
            else // (!horizontal && !isLB)
                return new RectHV(parent.rect.xmin(), parent.p.y(),
                                  parent.rect.xmax(), parent.rect.ymax());
        }
    }

    // construct an empty set of points
    public KdTree() { root = null; }

    // is the set empty?
    public boolean isEmpty() { return root == null; }

    // number of points in the set
    public int size() { return N; }

    // add the point p to the set (if it is not already in the set)
    public void insert(Point2D p) {
        assert (p.x() >= MIN && p.x() <= MAX && p.y() >= MIN && p.y() <= MAX);
        root = insert(root, p, null, VERTICAL, false);
    }

    /**
     * Recursively search for where to place a new node, then create it.
     *
     * @param h      the node to branch from (or to create if `h == null`)
     * @param p      the point to insert
     * @param parent the current or about-to-be parent of h
     * @param horiz  `true` if branching at h is by `y`-coordinate
     * @param lb     `true` if the last path we took was down the left/bottom
     *               side, `false` otherwise.
     * @return       h unless h is null, in which case a new node (i.e., this
     *               acts like the identity function except at the bottom of
     *               the tree).
     */
    private Node insert(Node h, Point2D p, Node parent, boolean horiz, boolean lb) {
        if (h == null) {
            N++;
            return new Node(p, parent, horiz, lb);
        }
        else if (horiz && p.y() < h.p.y() || !horiz && p.x() < h.p.x())
            h.lb = insert(h.lb, p, h, !horiz, true);
        else if (!h.p.equals(p))
            h.rt =  insert(h.rt, p, h, !horiz, false);
        return h;
    }

    // does the set contain the point p?
    public boolean contains(Point2D p) {
        assert (p.x() >= MIN && p.x() <= MAX && p.y() >= MIN && p.y() <= MAX);
        Node h = root;
        for (boolean horizontal = VERTICAL; h != null; horizontal = !horizontal) {
            if (h.p.equals(p))
                return true;
            else if (horizontal && p.y() < h.p.y() || !horizontal && p.x() < h.p.x())
                h = h.lb;
            else
                h = h.rt;
        }
        return false;
    }

    /**
     * Draw all of the points and dividing lines to standard draw
     * <p>
     * Draw the points in bold black, the horizontal lines in thing blue, and
     * the vertical lines in thin red.
     * <p>
     * Recurse through the tree three times, so this method takes >3N steps to
     * complete. This is faster than recursing through the tree once because
     * the most expensive operation is changing the pen color.
     */
    public void draw() {
        if (root == null) return;
        StdDraw.show(0);
        // Draw all the points
        StdDraw.setPenColor(StdDraw.BLACK);
        StdDraw.setPenRadius(.01);
        Node h = root;
        drawPoints(root);
        // Draw the division lines
        StdDraw.setPenRadius();
        StdDraw.setPenColor(StdDraw.RED);
        drawLines(root, new VerticalDrawer());
        StdDraw.setPenColor(StdDraw.BLUE);
        HorizontalDrawer hd = new HorizontalDrawer();
        drawLines(root.lb, hd);
        drawLines(root.rt, hd);
        // Show everything all at once.
        StdDraw.show(0);
    }

    // Traverse nodes in order to draw each point
    private void drawPoints(Node h) {
        if (h == null) return;
        drawPoints(h.lb);
        h.p.draw();
        drawPoints(h.rt);
    }

    // Traverse every other layer of nodes in order to draw one line per node
    private void drawLines(Node h, Drawer d) {
        if (h == null) return;
        if (h.lb != null) {
            drawLines(h.lb.lb, d);
            drawLines(h.lb.rt, d);
        }
        d.drawLine(h);
        if (h.rt != null) {
            drawLines(h.rt.lb, d);
            drawLines(h.rt.rt, d);
        }
    }

    // To use as function-pointer carriers for the draw function.
    private interface Drawer { void drawLine(Node h); }
    private static class VerticalDrawer implements Drawer {
        public void drawLine(Node h)
        { StdDraw.line(h.p.x(), h.rect.ymin(), h.p.x(), h.rect.ymax()); }
    }
    private static class HorizontalDrawer implements Drawer {
        public void drawLine(Node h)
        { StdDraw.line(h.rect.xmin(), h.p.y(), h.rect.xmax(), h.p.y()); }
    }

    /**
     * Find all points in the set that are inside the rectangle.
     *
     * @param rect  The rectangle to search inside of
     * @return      An iterable over the set of points interior to `rect`.
     */
    public Iterable<Point2D> range(RectHV rect) {
        // RectHV guarantees that all the mins are less than the maxes.
        assert (rect.xmin() >= MIN && rect.xmin() <= MAX
                && rect.ymin() >= MIN && rect.ymin() <= MAX)
            && (rect.xmax() >= MIN && rect.xmax() <= MAX
                && rect.ymax() >= MIN && rect.ymax() <= MAX);
        Bag<Point2D> inside = new Bag<Point2D>(); // Use a bag for O(1) adds.
        range(root, rect, inside);
        return inside;
    }

    // recursively add nodes' points to bag `inside` if the point is in rect
    private void range(Node h, RectHV rect, Bag<Point2D> inside) {
        if (h == null ||  !rect.intersects(h.rect)) return;
        range(h.lb, rect, inside);
        if (rect.contains(h.p)) inside.add(h.p);
        range(h.rt, rect, inside);
    }

    /**
     * Represent a node as a set {K, R, L} where K is the key and R, L are trees
     *
     * @param h     Root node of the subtree being examined
     * @return      String representation of a node.
     */
    private String toString(Node h) {
        if (h == null) return "";
        return "{" + h.p.toString() + ", "
                   + toString(h.lb) + ", "
                   + toString(h.rt) + "}";
    }

    /**
     * Find a nearest neighbor in the set to p; null if set is empty
     *
     * @param p     The query point
     * @return      The nearest neighbor to `p` in the set
     */
    public Point2D nearest(Point2D p) {
        assert (p.x() >= MIN && p.x() <= MAX && p.y() >= MIN && p.y() <= MAX);
        if (root == null) return null;
        NearestSearchNode champ = new NearestSearchNode();
        champ.champ = root.p;
        champ.champeDist = champ.distanceSquaredTo(query);
        return nearest(root, p, champ);
    }

    private static class NearestSearchNode {
        Point2D champ;
        double champDist;
    }

    // recursively search nodes for a neighbor to query closer than champ
    private Point2D nearest(Node h, Point2D query, NearestSearchNode champ) {
        if (h == null || champDist < h.rect.distanceSquaredTo(query))
            return champ;
        double hpDist = h.p.distanceSquaredTo(query);
        if (champDist > hpDist) {
            champ.champ = h.p;
            champ.champDist = hpDist;
        }
        if (h.rt != null && h.rt.rect.contains(query))
            return nearest(h.lb, query, nearest(h.rt, query, champ));
        return nearest(h.rt, query, nearest(h.lb, query, champ));
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
    private static KdTree readInput(String filename) {
        KdTree set = new KdTree();
        In in = new In(filename);
        while (!in.isEmpty())
            set.insert(new Point2D(in.readDouble(), in.readDouble()));
        in.close();
        return set;
    }

    public static void main(String[] args) {
        KdTree set = readInput(args[0]);
        set.draw();
    }
}
