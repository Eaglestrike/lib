import java.util.List;
import java.util.ArrayList;

import org.ejml.simple.SimpleMatrix;

public class HermiteWaypointSpline {


	//List of points and matrices containing spline coefficients
	private final List<SimpleMatrix> splineSections = new ArrayList<SimpleMatrix>();
	private final List<Waypoint> pointList = new ArrayList<Waypoint>();

	//Generic multiplier for Hermite splines which is needed to find the solution
	private static final SimpleMatrix multBase = new SimpleMatrix( new double[][] {{1, 0, 0, 0},{1, 1, 1, 1},{0, 1, 0, 0},{0, 1, 2, 3}} ).invert();	
	private boolean safeReturn = false;

	/**A HermiteSpline instance will take points and handle finding which function to use to find the solution
	 * points are formatted in.
	 * 
	 * @param points Waypoint[] - A list of Waypoints to build the spline off of
	 */
	public HermiteWaypointSpline(Waypoint[] points) {
		for(Waypoint p : points)
			pointList.add(p);
		reloadSpline();
	}

	/**
	 * takes in t and returns corresponding point from the piecewise spline function. The spline parts are parametric so [0 to 1] returns f( [0 to 1] ) 
	 * from the first function. The second and so on splines will take in [n - 1 to n] and have it scaled for the corresponding function so that the 
	 * return is equal to fn( [0 to 1] ).
	 * 
	 * @param t - value which determines x and y. And which piecewise spline function to use.
	 * @return double[] containing {x, y}
	 */
	public double[] getPointAtT(double t) {
		if(t < 0 || t > splineSections.size())
			if(safeReturn) 
				t = t < 0 ? 0 : splineSections.size();
			else return null;
		
		int index = (int) t;
		t -= ((int)t);
		SimpleMatrix solution = new SimpleMatrix( new double[][] {{ 1, t, t*t, t*t*t }} ).mult( splineSections.get( index ) );
		return new double[] { solution.get(0, 0), solution.get(0, 1)};
	}
	
	/**
	 * takes in t and returns corresponding derivative from the piecewise spline function. The spline parts are parametric so [0 to 1] returns f( [0 to 1] ) 
	 * from the first function. The second and so on splines will take in [n - 1 to n] and have it scaled for the corresponding function so that the 
	 * return is equal to fn( [0 to 1] ).
	 * 
	 * @param t - value which determines x and y. And which piecewise spline function to use.
	 * @return double equal to dy/dx
	 */
	
	public double getDerivativeAtT(double t) {
		if(t < 0 || t > splineSections.size())
			if(safeReturn)
				t = t < 0 ? 0 : splineSections.size();
			else return Double.NaN;
		int index = (int) t;
		t -= ((int)t);
		SimpleMatrix solution = new SimpleMatrix( new double[][] {{ 0, 1, 2*t, 3*t*t }} ).mult( splineSections.get( index ) );
		return solution.get(0, 1) / solution.get(0, 1);
	}
		
	/**
	 * Reloads the spline segments based on the current contained Waypoints. Allows the option of editing Waypoints in other classes
	 * then reloading the spline instead of needing to create a new HermiteWaypointSpline instance.
	 */
	public void reloadSpline() {
		splineSections.clear();
		if(pointList.size() < 2) return;

		//check if last point has derivative since the gaps are filled in based on the following points
		if(!pointList.get(pointList.size()-1).hasDerivative) {
			pointList.get(pointList.size()-1).setDerivative(pointList.get(pointList.size()-1).x-pointList.get(pointList.size()-2).x,pointList.get(pointList.size()-1).y-pointList.get(pointList.size()-2).y);
			pointList.get(pointList.size()-1).hasDerivative = false;
		}

		for(int i = 0; i < pointList.size() - 1; i++) {
			if(!pointList.get(i).hasDerivative && i != 0) {
				pointList.get(i).setDerivative((pointList.get(i+1).x-pointList.get(i-1).x),(pointList.get(i+1).y-pointList.get(i-1).y));
				pointList.get(i).hasDerivative = false;
			}
			loadSplineBetweenWaypoints(pointList.get(i), pointList.get(i + 1));
		}
	}

	/**
	 * Domain of t is [0, getSplineDomain()]
	 * @return the maximum input value of t 
	 */
	public double getSplineDomain() {
		return splineSections.size();
	}

	/**
	 * Returns the matrices used to generate the spline
	 * @return SimpleMatrix[] array of coefficients
	 */
	public SimpleMatrix[] getSplineSections() { 
		return (SimpleMatrix[]) splineSections.toArray(); 
	}
	/**
	 * If t is out of the range of the spline, safe return will return the last point on the spline otherwise null in returned
	 * @param arg - new value for safe return
	 */
	public void setSafeReturn( boolean arg ) {
		safeReturn = arg;
	}
	
	/**
	 * Appends an array of points to the spline then reloads the spline sections
	 * @param points - Array of Waypoints to add
	 */
	public void appendPoints(Waypoint[] points) {
		for(Waypoint w : points)
			pointList.add( w );
		reloadSpline();
	}

	/**
	 * Appends a point, then updates the spline
	 * @param point
	 */
	public void appendPoint(Waypoint point) {
		if(pointList.size() == 0) {
			pointList.add( point );
			return;
		}
		pointList.add( point );
		if(point.hasDerivative)
			loadSplineBetweenWaypoints(pointList.get(pointList.size() - 2), point);
		else
			reloadSpline();
	}

	/**
	 * Returns an ArrayList of Waypoints that can be edited then be applied to the spline using reloadSpline()
	 * @return a list of the current points
	 */
	public List<Waypoint> getWaypointList(){
		return pointList;
	}

	
	private void loadSplineBetweenWaypoints(Waypoint a, Waypoint b) {
		double mult = Math.sqrt(Math.pow(a.x-b.x, 2)+Math.pow(a.y-b.y, 2));
		splineSections.add( generateSplineSection(a.x, a.y, b.x, b.y, a.derivativeX * mult, a.derivativeY * mult, b.derivativeX * mult, b.derivativeY * mult));
	}

	/**Generates a Hermite spline section based on the end points and derivatives
	 * 
	 * @param ax - x of point A
	 * @param ay - y of point A
	 * @param bx - x of point B
	 * @param by - y of point B
	 * @param dax - x derivative of the spline at point A
	 * @param day - y der/vative of the spline at point A
	 * @param dbx - x derivative of the spline at point B
	 * @param dby - y derivative of the spline at point B
	 * @return SimpleMatrix of spline coefficients
	 */
	public static SimpleMatrix generateSplineSection(double ax, double ay, double bx, double by, double dax, double day, double dbx, double dby) {
		return  multBase.mult(new SimpleMatrix( new double[][] {{ax, ay},{bx, by},{dax, day},{dbx, dby}} ));
	}
}

class Waypoint {

	protected double x, y, derivativeX, derivativeY;
	protected boolean hasDerivative = false;

	public Waypoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public Waypoint(double x, double y, double derivative) {
		this(x, y);
		setDerivative(derivative);
	}

	public Waypoint(double x, double y, double xDerivative, double yDerivative) {
		this(x, y);
		setDerivative(xDerivative, yDerivative);
	}

	//This assumes x is in the positive direction
	public Waypoint setDerivative(double arg) {
		derivativeY = Math.sin(Math.atan(arg));
		derivativeX = Math.cos(Math.atan(arg));
		hasDerivative = true;
		return this;
	}

	//longer and more annoying version of the previous but with problem fixed
	public Waypoint setDerivative(double derivative, boolean posX, boolean posY) {
		setDerivative(derivative);
		derivativeX = Math.abs(derivativeX) * (posX? 1 : -1);
		derivativeY = Math.abs(derivativeY) * (posY? 1 : -1);
		return this;
	}

	//Scales so that x^2 + y^2 = 1, because even if the x and y derivative come to a normal value together,
	//they may still need to compensate for the derivative of the next point
	public Waypoint setDerivative(double x, double y) {
		derivativeY = y / Math.sqrt(x*x+y*y);
		derivativeX = x / Math.sqrt(x*x+y*y);
		hasDerivative = true;
		return this;
	}

	public Waypoint setDerivativeUnscaled(double x, double y) {
		derivativeY = x;
		derivativeX = y;
		hasDerivative = true;
		return this;
	}
	
	public Waypoint setAngle(double angle) {
		derivativeY = Math.sin(angle);
		derivativeX = Math.cos(angle);
		hasDerivative = true;
		return this;
	}

	public double getX() { return x; }
	public double getY() { return y; }
	public void setX(double x) { this.x = x; }
	public void setY(double y) { this.y = y; }

	public double getDerivative() {
		return derivativeY / derivativeX;
	}
}
