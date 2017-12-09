package org.team114.lib.pathgenerator;

import org.junit.*;

public class CubicHermiteSplineTest {

    @Test
    public void testDegreeChecking() {


        //for values
        try {
            CubicHermiteSpline a = new CubicHermiteSpline(new double[]{7, 3, 5, 1});
            CubicHermiteSpline b = new CubicHermiteSpline(new double[]{0, 3, 5, 0});
            CubicHermiteSpline c = new CubicHermiteSpline(new double[]{0, 0, 0, 0});
        } catch (Exception e) {
            Assert.assertTrue(false);
        }

        try {
            CubicHermiteSpline a = new CubicHermiteSpline(new double[]{7, 3, 5});
            Assert.assertTrue(false);
        } catch (Exception e) {
        }

        try {
            CubicHermiteSpline a = new CubicHermiteSpline(new double[]{7, 3, 5, 19, 21});
            Assert.assertTrue(false);
        } catch (Exception e) {
        }
    }
}
