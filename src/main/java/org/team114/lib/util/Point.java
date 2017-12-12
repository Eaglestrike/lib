package org.team114.lib.util;

/**
 * A two-dimensional point consisting of the ordered pair (x,y).
 */
public class Point {

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
     * @param x
     * @param y
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Makes a copy of a Point
     * @param copy the Point to copy
     */
    public Point(Point copy) {
        this(copy.x(), copy.y());
    }
    
    @Override
    public String toString() {
        return "Point (" + x + ", " + y + ")";
    }
    
}