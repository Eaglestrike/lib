package org.team114.lib.pathgenerator;

import org.junit.*;

public class CubicHermiteSplineTest {

    @Test
    public void testDegreeChecking() {
        try {
            CubicHermiteSpline a = new CubicHermiteSpline(new double[]{7, 3, 5, 1});
            CubicHermiteSpline b = new CubicHermiteSpline(new double[]{0, 3, 5, 0});
            CubicHermiteSpline c = new CubicHermiteSpline(new double[]{0, 0, 0, 0});
        } catch (Exception e) {
            Assert.fail();
        }

        try {
            CubicHermiteSpline a = new CubicHermiteSpline(new double[]{7, 3, 5});
            Assert.fail();
        } catch (Exception e) {
        }

        try {
            CubicHermiteSpline a = new CubicHermiteSpline(new double[]{7, 3, 5, 19, 21});
            Assert.fail();
        } catch (Exception e) {
        }
    }
}
