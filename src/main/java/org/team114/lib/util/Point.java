package org.team114.lib.util;

/**
 * Just a point with an x and y.
 */
public class Point {

    /**
     * X position of this point.
     */
    public double x;
    
    /**
     * Y position of this point.
     */
    public double y;
    
    /**
     * Makes a point at (x, y).
     * @param x
     * @param y
     */
    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }
    
}
