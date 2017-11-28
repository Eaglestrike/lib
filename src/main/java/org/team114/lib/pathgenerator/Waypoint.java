package org.team114.lib.pathgenerator;

/**
 * Represents a point and a derivative.
 */
public class Waypoint {
    /**
     * X position of this point.
     */
    public double x;
    /**
     * Y position of this point.
     */
    public double y;
    /**
     * X part of the derivative.
     */
    public double derivativeX;
    /**
     * Y part of the derivative.
     */
    public double derivativeY;

    /**
     * Whether the derivative should be assigned automatically or read from the
     * {@link #derivativeX} and {@link #derivativeY} variables.
     * }
     */
    public boolean autoAssignDerivative = true;

    /**
     * Creates a new Waypoint at (x, y) with an unspecified derivative.
     *
     * @param x X coordinate of the new point
     * @param y Y coordinate of the new point
     */
    public Waypoint(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Creates a new Waypoint at (x, y) then sets the derivative using the specified components with
     * setAngle(double angle). Note that the angle is in radians.
     *
     * @param x X coordinate of the new point
     * @param y Y coordinate of the new point
     * @param angle angle of the derivative of the spline that goes through the new point
     */
    public Waypoint(double x, double y, double angle) {
        this(x, y);
        setAngle(angle);
    }

    /**
     * Creates a new Waypoint at (x, y) then sets the derivative using the specified components with
     * setDerivative(double x, double y).
     *
     * @param x X coordinate of the this Waypoint
     * @param y Y coordinate of the Waypoint
     * @param xDerivative the X component of the derivative of the spline that goes through this point
     * @param yDerivative the Y component of the derivative of the spline that goes through this point
     */
    public Waypoint(double x, double y, double xDerivative, double yDerivative) {
        this(x, y);
        setDerivative(xDerivative, yDerivative);
    }

    /**
     * Takes in a ratio of x and y and scales them to maintain y/x while putting x and y in a
     * usable range, preventing unnecessary loops in final splines.
     *
     * @param x derivative x component
     * @param y derivative y component
     * @return this Waypoint to allow for method chaining
     */
    public Waypoint setDerivative(double x, double y) {
        derivativeY = y / Math.sqrt(x * x + y * y);
        derivativeX = x / Math.sqrt(x * x + y * y);
        autoAssignDerivative = false;
        return this;
    }

    /**
     * Takes in a ratio of x and y for the derivative, but does not scale them. If values are too
     * large or small, the curve of the spline may not be visible or unnecessary loops may be
     * created in the spline.
     *
     * @param x derivative x component
     * @param y derivative y component
     * @return this Waypoint to allow for method chaining
     */
    public Waypoint setDerivativeUnscaled(double x, double y) {
        derivativeX = x;
        derivativeY = y;
        autoAssignDerivative = false;
        return this;
    }

    /**
     * Takes in an angle and uses it to determine the derivative x and y components.
     *
     * @param angle the angle of the derivative in radians
     * @return this Waypoint to allow method chaining
     */
    public Waypoint setAngle(double angle) {
        derivativeY = Math.sin(angle);
        derivativeX = Math.cos(angle);
        autoAssignDerivative = false;
        return this;
    }

    /**
     * Returns the current derivative of this waypoint (y/x). If a Waypoint has
     * been auto-assigned a derivative, that will be returned.
     *
     * @return the derivative at this point
     */
    public double getDerivative() {
        return derivativeY / derivativeX;
    }
    
}
