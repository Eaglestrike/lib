package org.team114.lib.pathgenerator;
import java.lang.Math;
public class QuinticHermiteSpline implements ParametricOneVariableSpline {
    private Polynomial a;
    private Polynomial ddx = null;

    public QuinticHermiteSpline(double p0, double v0, double a0, double p1, double v1, double a1) {
        //see https://www.rose-hulman.edu/~finn/CCLI/Notes/day09.pdf for explanation
        a = new Polynomial(new double[6]);
        evalBasis50(p0);
        evalBasis51(v0);
        evalBasis52(a0);
        evalBasis53(a1);
        evalBasis54(v1);
        evalBasis55(p1);
    }

    private void evalBasis50(double p0) {
        a.coefficients[0] += p0;
        a.coefficients[3] += -10*p0;
        a.coefficients[4] += 14*p0;
        a.coefficients[5] += -6*p0;
    }

    private void evalBasis51(double v0) {
        a.coefficients[1] += v0;
        a.coefficients[3] += -6*v0;
        a.coefficients[4] += 8*v0;
        a.coefficients[5] += -3*v0;
    }

    private void evalBasis52(double a0) {
        a.coefficients[2] += 0.5*a0;
        a.coefficients[3] += -1.5*a0;
        a.coefficients[4] += 1.5*a0;
        a.coefficients[5] += -0.5*a0;
    }

    private void evalBasis53(double a1) {
        a.coefficients[3] += 0.5*a1;
        a.coefficients[4] += -a1;
        a.coefficients[5] += 0.5*a1;
    }

    private void evalBasis54(double v1) {
        a.coefficients[3] += -4*v1;
        a.coefficients[4] += 7*v1;
        a.coefficients[5] += -3*v1;
    }

    private void evalBasis55(double p1) {
        a.coefficients[3] += 10*p1;
        a.coefficients[4] += -15*p1;
        a.coefficients[5] += 6*p1;
    }

    @Override
    public double getValueAt(double t) {
        if (t < 0 || t > 1) {
            throw new IndexOutOfBoundsException("The parameter must be between 0 and 1.");
        }
        return a.eval(t);
    }

    @Override
    public double dfdt(double t) {
        if (t < 0 || t > 1) {
            throw new IndexOutOfBoundsException("The parameter must be between 0 and 1.");
        } else if (ddx == null) {
            ddx = a.ddx();
        }
        return ddx.eval(t);
    }
}
