package org.team114.lib.pathgenerator;

import java.util.Arrays;

public class Polynomial {
    protected int degree;
    protected double[] coefficients;
    protected Polynomial(double[] a) {
        coefficients = Arrays.copyOf(a, a.length);
        degree = coefficients.length-1;
    }

    public double eval(double x) {
        double val = 0;
        for (int i = 0; i < coefficients.length; i++) {
            val += coefficients[i] * Math.pow(x, i);
        }
        return val;
    }

    public Polynomial ddx() {
        double[] g = new double[this.coefficients.length-1];
        for (int i = 1; i < coefficients.length; i++) {
            g[i-1] = coefficients[i] * i;
        }
        return new Polynomial(g);
    }
}
