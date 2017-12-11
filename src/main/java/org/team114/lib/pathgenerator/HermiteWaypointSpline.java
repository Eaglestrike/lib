package org.team114.lib.pathgenerator;

import java.util.List;
import java.util.ArrayList;
import org.ejml.simple.SimpleMatrix;
import org.team114.lib.util.Geometry;
import org.team114.lib.util.Point;


/**
 * A HermiteWaypointSpline constructs a Spline based on a series of Waypoints
 * using a piecewise function of parametric cubic interpolating spline sections.
 *
 * @see <a href="https://people.cs.clemson.edu/~dhouse/courses/405/notes/splines.pdf">Spline Curves</a>
 */

public class HermiteWaypointSpline {


    /** List of points and matrices containing spline coefficients. */
    protected List<SimpleMatrix> splineSections = new ArrayList<>();
    protected List<Waypoint> pointList = new ArrayList<>();

    /** Generic multiplier for Hermite splines which is needed to find the solution. */
    private static final SimpleMatrix multBase =
            new SimpleMatrix(new double[][] {{1, 0, 0, 0}, {1, 1, 1, 1}, {0, 1, 0, 0}, {0, 1, 2, 3}})
            .invert();

    /** Generic multiplier for Quintic Hermite splines which is needed to find the solution. */
    private static final SimpleMatrix multBaseQ =
            new SimpleMatrix(new double[][] {{1, 0, 0, 0, 0, 0}, {1, 1, 1, 1, 1, 1},
                {0, 1, 0, 0, 0, 0}, {0, 1, 2, 3, 4, 5}, {0, 0, 2, 0, 0, 0}, {0, 0, 2, 6, 12, 20}})
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
    public Point getPointAtT(double t) {
        if (t < 0 || t > splineSections.size())
            if (safeReturn) {
                t = t < 0 ? 0 : splineSections.size();
            } else return null;

        int index = (int) t;
        t -= (int) t;

        return Geometry.solveParametricPolynomial(splineSections.get(index), t);
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

    public Point getDerivativeAtT(double t) {
        if (t < 0 || t > splineSections.size()) {
            if (safeReturn) {
                t = t < 0 ? 0 : splineSections.size();
            } else return new Point(Double.NaN, Double.NaN);
        }
        int index = (int) t;
        t -= (int) t;

        return Geometry.solveParametricDerivative(splineSections.get(index), t);
    }

    /**
     * Reloads the spline segments based on the current contained Waypoints. Allows the option of
     * editing Waypoints in other classes then reloading the spline instead of needing to create a
     * new HermiteWaypointSpline instance.
     */
    public void reloadSpline() {
        splineSections.clear();
        if(pointList.size() < 2) return;

        //check if last point has derivative since the gaps are filled in based on the following points
        if(pointList.get(pointList.size() - 1).autoAssignDerivative)
            pointList.get(pointList.size() - 1).assignDerivative( pointList.get(pointList.size() - 2),  pointList.get(pointList.size() - 1));

        for(int i = 0; i < pointList.size() - 1; i++) {
            if(pointList.get(i).autoAssignDerivative && i != 0)
                pointList.get(i).assignDerivative(pointList.get(i - 1), pointList.get(i + 1));
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
        //multiplying by length makes adds more curves (otherwise its more like line segments)
        //But it also complicated velocity from spline
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

    /**
     * It it like the other one but with more useless variables worked in. If you thought the other
     * one was confusing this is going to be way worse. I got the extra complication by specifying
     * the derivative of the derivative. I have absolutely no clue where that will come from but good luck.
     * 
     * @param ax x of point A
     * @param ay y of point A
     * @param bx x of point B
     * @param by y of point B
     * @param dax x derivative of the spline at point A
     * @param day y derivative of the spline at point A
     * @param dbx x derivative of the spline at point B
     * @param dby y derivative of the spline at point B
     * @param ddax x derivative of the derivative of the spline at point A
     * @param dday y derivative of the derivative of the spline at point A
     * @param ddbx x derivative of the derivative of the spline at point B
     * @param ddby y derivative of the derivative of the spline at point B
     * @return
     */
    public static SimpleMatrix generateQuinticSplineSection(double ax, double ay, double bx, double by,
            double dax, double day, double dbx, double dby, double ddax, double dday, double ddbx,
            double ddby) {
        return multBase.mult(new SimpleMatrix(new double[][] {{ax, ay}, {bx, by}, {dax, day},
            {dbx, dby}, {ddax, dday}, {ddbx, ddby}}));
    }

    /**
     * Same as getClosestPointOnSplineSection() but uses gradient descent.
     * @param p is the point get close to
     * @param spline is the spline section to look at
     * @return the closest point
     */
    public static Point gradientDescent(Point p, SimpleMatrix spline) {
        double d = 0.5, high = 1, low = 0;

        for(int i = 0; i < 20; i++) {
            double a = (high + d) / 2;
            double b = (low + d) / 2;
            Point higher = Geometry.solveParametricPolynomial(spline, a);
            Point lower = Geometry.solveParametricPolynomial(spline, b);
            double dist1 = Geometry.dist(p.x, p.y, higher.x, higher.y);
            double dist2 = Geometry.dist(p.x, p.y, lower.x, lower.y);
            if(dist1 < dist2) {
                low = d;
                d = a;
            }else {
                high = d;
                d = b;
            }
        }
        return Geometry.solveParametricPolynomial(spline, d);
    }

    /**
     * Uses gradient descent in order to get the closest point on this spline to some point p.
     * @param p is the point to get the closest point on the spline to
     * @return The closest point
     */
    public Point getClosestPointOnSpline(Point p) {
        double bestDistance = Double.MAX_VALUE;
        Point bestPoint = null;
        for(SimpleMatrix m : splineSections) {
            Point result = gradientDescent(p, m);
            if(result == null)
                continue;
            double dist = Geometry.dist(p.x, p.y, result.x, result.y);
            if(dist < bestDistance) {
                bestPoint = result;
                bestDistance = dist;
            }
        }
        return bestPoint;
    }

    /**
     * Gets the closest point on a spline section to some point p.
     * @param p is the point get close to
     * @param spline is the spline section to look at
     * @return the closest point
     */
    public Point getClosestPointOnSplineSection(Point p, SimpleMatrix spline) {
        SimpleMatrix ex = spline.cols(0, 1);
        SimpleMatrix ey = spline.cols(1, 2);
        SimpleMatrix dx = ex.extractMatrix(1, ex.numRows(), 0, 1);
        SimpleMatrix dy = ey.extractMatrix(1, ex.numRows(), 0, 1);

        for(int i = 0; i < dx.numRows(); i++) {
            dx.set(i, 0, dx.get(i, 0) * (i + 1));
            dy.set(i, 0, dy.get(i, 0) * (i + 1));
        }

        //For some reason they have a SimpleMatrix.divide(double), but not a SimpleMatrix.multiply()
        SimpleMatrix a = Geometry.addPolynomial(Geometry.multiplyPolynomial(ex, dx), Geometry.multiplyPolynomial(ey, dy));
        SimpleMatrix b = Geometry.addPolynomial(dx.divide(1 / p.x), dy.divide(1 / p.y)).negative();

        SimpleMatrix end = Geometry.addPolynomial(a, b);

        double[] roots = Geometry.solvePolynomial(end, 50, 0.0001);

        double bestDistance = Double.MAX_VALUE;
        Point bestPoint = null;

        for(double d : roots) {
            if(d < 0 || d > 1)
                continue;
            Point point = Geometry.solveParametricPolynomial(spline, d);
            double dist = Geometry.dist(p.x, p.y, point.x, point.y);
            if(dist < bestDistance) {
                bestPoint = point;
                bestDistance = dist;
            }
        }

        for(double d = 0; d < 2; d++) {
            Point point = Geometry.solveParametricPolynomial(spline, d);
            double dist = Geometry.dist(p.x, p.y, point.x, point.y);
            if(dist < bestDistance) {
                bestPoint = point;
                bestDistance = dist;
            }
        }
        return bestPoint;
    }
}
