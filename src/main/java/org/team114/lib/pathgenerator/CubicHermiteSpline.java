package org.team114.lib.pathgenerator;

import java.util.List;
import java.util.ArrayList;

public class CubicHermiteSpline extends Polynomial implements ParametricOneVariableSpline {

    public static List<ParametricOneVariableSpline> generate() {return null;}

    public static CubicHermiteSpline fromSecondDerivative(double p0, double a0, double p1, double a1) {
        return null;
    }

    public static CubicHermiteSpline fromFirstDerivative(double p0, double v0, double p1, double v1) {
        //see https://www.rose-hulman.edu/~finn/CCLI/Notes/day09.pdf for explanation

        return new CubicHermiteSpline(new double[]{
                p0, //a0
                v0, //a1
                -3*p0 - 2*v0 - v1 + 3*p1, //a2
                2*p0 + v0 + v1 - 2*p1 //a3
        });
    }

    private CubicHermiteSpline(double[] a) {
        super(a);
    }

    @Override
    public double getValueAt(double t) {
        if (t < 0 || t > 1) {
            throw new IndexOutOfBoundsException("The parameter must be between 0 and 1.");
        }
        return super.eval(t);
    }

    @Override
    public double dfdt(double t) {
        if (t < 0 || t > 1) {
            throw new IndexOutOfBoundsException("The parameter must be between 0 and 1.");
        }
        return super.ddx().eval(t);
    }
}
