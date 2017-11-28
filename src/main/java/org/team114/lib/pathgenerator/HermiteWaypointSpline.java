package org.team114.lib.pathgenerator;

import java.util.List;
import java.util.ArrayList;
import org.ejml.simple.SimpleMatrix;


/**
 * A HermiteWaypointSpline constructs a Spline based on a series of Waypoints
 * using a piecewise function of parametric cubic interpolating spline sections.
 *
 * @see <a href="https://people.cs.clemson.edu/~dhouse/courses/405/notes/splines.pdf">Spline Curves</a>
 */
public class HermiteWaypointSpline {


    /** List of points and matrices containing spline coefficients. */
    private final List<SimpleMatrix> splineSections = new ArrayList<>();
    private final List<Waypoint> pointList = new ArrayList<>();

    /** Generic multiplier for Hermite splines which is needed to find the solution. */
    private static final SimpleMatrix multBase =
            new SimpleMatrix(new double[][] {{1, 0, 0, 0}, {1, 1, 1, 1}, {0, 1, 0, 0}, {0, 1, 2, 3}})
                    .invert();

    private boolean safeReturn = false;
    /**
     * A HermiteSpline instance will take points and handle finding which function to use to find
     * the solution points are formatted in.
     *
     * @param points Waypoint[] list of Waypoints to build the spline off of
     */
    public HermiteWaypointSpline(Waypoint[] points) {
        for (Waypoint p : points) {
            pointList.add(p);
        }
        reloadSpline();
    }

    /**
     * Takes in t and returns corresponding point from the piecewise spline function. The spline
     * parts are parametric so [0 to 1] returns f( [0 to 1] ) from the first function. The second
     * and so on splines will take in [n - 1 to n] and have it scaled for the corresponding
     * function so that the return is equal to fn( [0 to 1] ).
     *
     * @param t value which determines x and y, and which piecewise spline function to use
     * @return double[] containing {x, y}
     */
    public double[] getPointAtT(double t) {
        if (t < 0 || t > splineSections.size())
            if (safeReturn) {
                t = t < 0 ? 0 : splineSections.size();
            } else return null;

        int index = (int) t;
        t -= (int) t;
        SimpleMatrix solution = new SimpleMatrix(new double[][] {{ 1, t, t * t, t * t * t }})
                .mult(splineSections.get(index));

        return new double[] { solution.get(0, 0), solution.get(0, 1)};
    }

    /**
     * Takes in t and returns corresponding derivative from the piecewise spline function.
     * The spline parts are parametric so [0 to 1] returns f( [0 to 1] ) from the first function.
     * The second and so on splines will take in [n - 1 to n] and have it scaled for the
     * corresponding function so that the return is equal to fn( [0 to 1] ).
     * 
     * @param t value which determines x and y, and which piecewise spline function to use
     * @return double equal to dy/dx
     */

    public double getDerivativeAtT(double t) {
        if (t < 0 || t > splineSections.size()) {
            if (safeReturn) {
                t = t < 0 ? 0 : splineSections.size();
            } else return Double.NaN;
        }
        int index = (int) t;
        t -= (int) t;

        SimpleMatrix solution = new SimpleMatrix(new double[][] {{ 0, 1, 2 * t, 3 * t * t }})
                .mult(splineSections.get(index));

        return solution.get(0, 1) / solution.get(0, 1);
    }

    /**
     * Reloads the spline segments based on the current contained Waypoints. Allows the option of
     * editing Waypoints in other classes then reloading the spline instead of needing to create a
     * new HermiteWaypointSpline instance.
     */
    public void reloadSpline() {
        splineSections.clear();
        if (pointList.size() < 2) return;

        //check if last point has derivative since the gaps are filled in based on the following points
        if (pointList.get(pointList.size() - 1).autoAssignDerivative) {
            pointList.get(pointList.size() - 1).setDerivative(pointList.get(pointList.size() - 1).x
                    - pointList.get(pointList.size() - 2).x,pointList.get(pointList.size() - 1).y
                    - pointList.get(pointList.size() - 2).y);
            pointList.get(pointList.size() - 1).autoAssignDerivative = true;
        }

        for (int i = 0; i < pointList.size() - 1; i++) {
            if (pointList.get(i).autoAssignDerivative && i != 0) {
                pointList.get(i).setDerivative((pointList.get(i + 1).x - pointList.get(i - 1).x),
                        (pointList.get(i + 1).y - pointList.get(i - 1).y));
                pointList.get(i).autoAssignDerivative = true;
            }
            loadSplineBetweenWaypoints(pointList.get(i), pointList.get(i + 1));
        }
    }

    /**
     * Domain of t is [0, getSplineDomain()].
     *
     * @return the maximum input value of t 
     */
    public double getSplineDomain() {
        return splineSections.size();
    }

    /**
     * Returns the matrices used to generate the spline.
     *
     * @return SimpleMatrix[] array of coefficients
     */
    public SimpleMatrix[] getSplineSections() { 
        return (SimpleMatrix[]) splineSections.toArray(); 
    }
    /**
     * If t is out of the range of the spline, safe return will return the last point on the spline
     * otherwise null is returned.
     *
     * @param arg new value for safe return
     */
    public void setSafeReturn(boolean arg) {
        safeReturn = arg;
    }

    /**
     * Appends an array of points to the point list then reloads the spline sections.
     *
     * @param points Array of Waypoints to add
     */
    public void appendPoints(Waypoint[] points) {
        for (Waypoint w : points) {
            pointList.add(w);
    }
        reloadSpline();
    }

    /**
     * Appends a point, then updates the spline.
     *
     * @param point the Waypoint to be added
     */
    public void appendPoint(Waypoint point) {
        if (pointList.size() == 0) {
            pointList.add(point);
            return;
        }
        pointList.add(point);
        reloadSpline();
    }

    /**
     * Returns an ArrayList of Waypoints that can be edited then be applied to the spline using
     * reloadSpline(). Note that if the points some of the points on this list were not added with
     * appendPoint() or appendPoints(), the spline may not have been updated to include them.
     *
     * @return a list of the current points
     */
    public List<Waypoint> getWaypointList() {
        return pointList;
    }

    
    private void loadSplineBetweenWaypoints(Waypoint a, Waypoint b) {
        double mult = Math.sqrt(Math.pow(a.x - b.x, 2) + Math.pow(a.y - b.y, 2));
        splineSections.add(generateSplineSection(a.x, a.y, b.x, b.y, a.derivativeX * mult,
        a.derivativeY * mult, b.derivativeX * mult, b.derivativeY * mult));
    }

    /**
     * Generates a Hermite spline section based on the given end points and derivatives.
     *
     * @param ax x of point A
     * @param ay y of point A
     * @param bx x of point B
     * @param by y of point B
     * @param dax x derivative of the spline at point A
     * @param day y derivative of the spline at point A
     * @param dbx x derivative of the spline at point B
     * @param dby y derivative of the spline at point B
     * @return SimpleMatrix of spline coefficients
     */
    public static SimpleMatrix generateSplineSection(double ax, double ay, double bx, double by,
      double dax, double day, double dbx, double dby) {
        return multBase.mult(new SimpleMatrix(new double[][] {{ax, ay}, {bx, by}, {dax, day},
        {dbx, dby}}));
    }
}
