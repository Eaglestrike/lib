package org.team114.lib.pathgenerator;
import java.lang.Math;
public class QuinticHermiteSpline implements ParametricOneVariableSpline {
    private double[] a; //function of the form SUM(a(i)*x^i)

    public QuinticHermiteSpline(double p0, double v0, double a0, double p1, double v1, double a1) {
        //see https://www.rose-hulman.edu/~finn/CCLI/Notes/day09.pdf for explanation
        a = new double[6];
        evalBasis50(p0);
        evalBasis51(v0);
        evalBasis52(a0);
        evalBasis53(a1);
        evalBasis54(v1);
        evalBasis55(p1);
    }


    private void evalBasis50(double p0) {
        a[0] += p0;
        a[3] += -10*p0;
        a[4] += 14*p0;
        a[5] += -6*p0;
    }

    private void evalBasis51(double v0) {
        a[1] += v0;
        a[3] += -6*v0;
        a[4] += 8*v0;
        a[5] += -3*v0;
    }

    private void evalBasis52(double a0) {
        a[2] += 0.5*a0;
        a[3] += -1.5*a0;
        a[4] += 1.5*a0;
        a[5] += -0.5*a0;
    }

    private void evalBasis53(double a1) {
        a[3] += 0.5*a1;
        a[4] += -a1;
        a[5] += 0.5*a1;
    }

    private void evalBasis54(double v1) {
        a[3] += -4*v1;
        a[4] += 7*v1;
        a[5] += -3*v1;
    }

    private void evalBasis55(double p1) {
        a[3] += 10*p1;
        a[4] += -15*p1;
        a[5] += 6*p1;
    }

    @Override
    public double getValueAt(double t) {
        double total = 0;
        for (int i = 0; i < 6; i++) {
            total += a[i] * Math.pow(t,i);
        }
        return total;
    }

    @Override
    public double dfdt(double t) {
        double total = 0;
        for (int i = 1; i < 6; i++) {
            total += i * a[i] * Math.pow(t,i-1);
        }
        return total;
    }
}
