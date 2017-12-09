package org.team114.lib.pathgenerator;

import org.junit.*;

public class PolynomialSplineTest {

    @Test
    public void testBoundsChecking() {
        PolynomialSpline p = new PolynomialSpline(new double[]{7, 3, 5, 15, 2.2, -5.5, 0.0145, -0.234});

        //for values
        try {
            p.getValueAt(0);
            p.getValueAt(0.5);
            p.getValueAt(1);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }

        try {
            p.getValueAt(-0.1);
            Assert.assertTrue(false);
        } catch (Exception e) {
        }

        try {
            p.getValueAt(1.1);
            Assert.assertTrue(false);
        } catch (Exception e) {
        }

        try {
            p.getValueAt(5);
            Assert.assertTrue(false);
        } catch (Exception e) {
        }

        try {
            p.getValueAt(-5);
            Assert.assertTrue(false);
        } catch (Exception e) {
        }

        //for derivatives
        try {
            p.dfdt(0);
            p.dfdt(0.5);
            p.dfdt(1);
        } catch (Exception e) {
            Assert.assertTrue(false);
        }

        try {
            p.dfdt(-0.1);
            Assert.assertTrue(false);
        } catch (Exception e) {
        }

        try {
            p.dfdt(1.1);
            Assert.assertTrue(false);
        } catch (Exception e) {
        }

        try {
            p.dfdt(5);
            Assert.assertTrue(false);
        } catch (Exception e) {
        }

        try {
            p.dfdt(-5);
            Assert.assertTrue(false);
        } catch (Exception e) {
        }
    }
}
