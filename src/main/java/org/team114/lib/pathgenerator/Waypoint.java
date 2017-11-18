package org.team114.lib.pathgenerator;

public class Waypoint {

	public double x;
    public double y;
    public double derivativeX;
    public double derivativeY;

	public boolean autoAssignDerivative = true;

	/**
	 * Creates a new Waypoint at (x, y) with an unspecified derivative.
	 * @param x X coordinate of the Waypoint
	 * @param y Y coordinate of the Waypoint
	 */
	public Waypoint(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Creates a new Waypoint at (x, y) then sets the derivative using the specified components with
	 * setAngle(double angle).
	 * Note that the angle is in radians.
	 * @param x X coordinate of the Waypoint
	 * @param y Y coordinate of the Waypoint
	 * @param angle angle of the derivative of the spline that goes through this point
	 */
	public Waypoint(double x, double y, double angle) {
		this(x, y);
		setAngle(angle);
	}

	/**
	 * Creates a new Waypoint at (x, y) then sets the derivative using the specified components with
     * setDerivative(double x, double y).
	 * @param x X coordinate of the Waypoint
	 * @param y Y coordinate of the Waypoint
	 * @param xDerivative the X component of the derivative of the spline that goes through this point
	 * @param yDerivative the Y component of the derivative of the spline that goes through this point
	 */
	public Waypoint(double x, double y, double xDerivative, double yDerivative) {
		this(x, y);
		setDerivative(xDerivative, yDerivative);
	}

	/**
	 * Takes in a ratio of x and y and scales them to maintain y/x while putting x and y in a usable range.
	 * (Prevents unnecessary loops in final splines)
	 * @param x derivative x component
	 * @param y derivative y component
	 * @return the Waypoint that was assigned the derivative to allow for shorter code in application. A new Waypoint
     * is not created by reassigning the derivative.
	 */
	public Waypoint setDerivative(double x, double y) {
		derivativeY = y / Math.sqrt(x * x + y * y);
		derivativeX = x / Math.sqrt(x * x + y * y);
		autoAssignDerivative = true;
		return this;
	}

	/**
	 * Takes in a ratio of x and y for the derivative, but does not scale them. If values are too large or small,
     * the curve of the spline may not be visable or unnessesary loops may be created in the spline.
	 * @param x derivative x component
	 * @param y derivative y component
	 * @return the Waypoint that was assigned the derivative to allow for shorter code in application. A new Waypoint
     * is not created by reassigning the derivative.
	 */
	public Waypoint setDerivativeUnscaled(double x, double y) {
		derivativeX = x;
		derivativeY = y;
		autoAssignDerivative = false;
		return this;
	}

	/**
	 * Takes in an angle and uses it to determine the derivative x and y components.
	 * @param angle the angle of the derivative in radians
	 * @return the Waypoint that was assigned the derivative to allow for shorter code in application. A new Waypoint
     * is not created by reassigning the derivative.
	 */
	public Waypoint setAngle(double angle) {
		derivativeY = Math.sin(angle);
		derivativeX = Math.cos(angle);
		autoAssignDerivative = false;
		return this;
	}

	/**
	 * Returns the current derivative of a waypoint in the form of the y/x. If a Waypoint has beem auto-assigned a
     * derivative, that will be returned.
	 * @return the derivative at a Waypoint
	 */
	public double getDerivative() {
		return derivativeY / derivativeX;
	}
	
}
