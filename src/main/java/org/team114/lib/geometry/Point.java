package org.team114.lib.geometry;

/**
 * A two-dimensional point consisting of the ordered pair (x, y).
 */
public class Point implements Serializable {

    /**
     * X position of this point.
     */
    private double x;

    /**
     * Gets the x position of the point.
     * @return the x coordinate value
     */
    public double x() {
        return x;
    }
    /**
     * Y position of this point.
     */
    private double y;

    /**
     * Gets the Y position of the point.
     * @return the y coordinate value
     */
    public double y() {
        return y;
    }
    
    /**
     * Makes a point at (x, y).
     * @param x the x position
     * @param y the y position
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Makes a copy of a Point.
     * @param copy the Point to copy
     */
    public Point(Point copy) {
        this(copy.x(), copy.y());
    }
    
    /**
     * Returns the distance between this and another point.
     * @param p is the point get the distance to.
     * @return The distance.
     */
    public double dist(Point p) {
        return Math.sqrt(hyp2(p));
    }

    public double hyp2(Point p) {
        return Math.pow(p.x - x, 2) + Math.pow(p.y - y, 2);
    }

    /**
     * Returns a string representation of the point.
     * @return a string in the form of "Point (&lt;x&gt;, &lt;y&gt;)
     */
    @Override
    public String toString() {
        return "Point (" + x + ", " + y + ")";
    }
    
}
