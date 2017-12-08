package org.team114.lib.pathgenerator;

import java.util.List;
import java.util.ArrayList;

public class CubicHermiteSpline extends PolynomialSpline {
    public CubicHermiteSpline(double[] a) {
        super(a);
        if (super.degree() != 3) {
            throw new IllegalArgumentException("The passed array would not create a cubic function");
        }
    }
}
