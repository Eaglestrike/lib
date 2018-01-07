package org.team114.lib.pathgenerator;

import java.util.ArrayList;
import java.util.List;

import org.team114.lib.util.Point;

/**
 * An abstract piecewise path section.
 *
 */
public class Path {

    /**
     * A list of the x of the piecewise spline.
     */
    private List<PolynomialSpline> xComponents;

    /**
     * A list of the y of the piecewise spline.
     */
    private List<PolynomialSpline> yComponents;

    /**
     * Creates a path based on pre-generated lists of of the x and y spline components. This method
     * is not recommended through and if possible the PathFactory should be used.
     * @param xComponents to use
     * @param yComponents to use
     */
    public Path(List<PolynomialSpline> xComponents, List<PolynomialSpline> yComponents) {
        assert(xComponents.size() == yComponents.size());

        this.xComponents = xComponents;
        this.yComponents = yComponents;
    }

    /**
     * Creates a path based on a the long list of coefficients given by the spline viewer application.
     * Array is ordered as [piecewise section][x component or y component][coefficient]. It is not
     * recommended to use this method if not copy pasting from the spline viewer application.
     * @param coefficients
     */
    public Path(double[][][] coefficients) {
        xComponents = new ArrayList<PolynomialSpline>();
        yComponents = new ArrayList<PolynomialSpline>();
        for(double[][] part : coefficients) {
            assert(part.length == 2);
            xComponents.add(new PolynomialSpline(part[0]));
            yComponents.add(new PolynomialSpline(part[1]));
        }
    }

    /**
     * Gets the point on path at t.
     * @param t is how far along the path to get the point.
     * @return The Point at t.
     */
    public Point getPointAtT(double t) {
        if (t < 0 || t > length()) {
            throw new IndexOutOfBoundsException("The parameter must be between 0 and " + length() + ": " + t);
        }
        int component = (int) t;
        if(t == length()) { //Prevent out of bounds
            t = 1;
            component = (int) (length() - 1);
        }else {
            t = t - ((int) t);
        }
        return new Point(xComponents.get(component).eval(t), yComponents.get(component).eval(t));
    }

    /**
     * Gets the x and y derivatives of the path at a point and returns them in point form.
     * @param t is how far along the path to get the point.
     * @return The Point containing the x and y derivatives.
     */
    public Point dfdt(double t) {
        if (t < 0 || t > length()) {
            throw new IndexOutOfBoundsException("The parameter must be between 0 and " + length() + ": " + t);
        }
        int component = (int) t;
        if(t == length()) { //Prevent out of bounds
            t = 1;
            component = (int) (length() - 1);
        }else {
            t = t - ((int) t);
        }
        return new Point(xComponents.get(component).dfdt(t), yComponents.get(component).dfdt(t));
    }

    public double length() {
        return xComponents.size();
    }

    /**
     * Since the key points along the spline are not essentials after generation they are not stored.
     * This method returns a generated list of the points along the spline, but assumes parametric
     * segments. If in the event points are essential, a wrapper may be useful.
     * @return A list of all of the end points of the parametric components.
     */
    public List<Point> generatePointList(){
        ArrayList<Point> points = new ArrayList<Point>();
        for(int i = 0; i <= xComponents.size(); i++)
            points.add(getPointAtT(i));
        return points;
    }

    public List<PolynomialSpline> getX(){
        return xComponents;
    }

    public List<PolynomialSpline> getY(){
        return yComponents;
    }
}
