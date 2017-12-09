package org.team114.lib.util;

import org.junit.*;

public class EpsilonTest {
    @Test
    public void testEpsilonEqualsNearZero() {
        generalEpsilonTest(0);
        generalEpsilonTest(1e-5);
        generalEpsilonTest(-1e-3);
    }

    @Test
    public void testEpsilonNegativeSmall() {
        generalEpsilonTest(-5);
    }

    @Test
    public void testEpsilonPositiveSmall() {
        generalEpsilonTest(7);
    }

    @Test
    public void testEpsilonNegativeLarge() {
        generalEpsilonTest(-1e10+31278);
    }

    @Test
    public void testEpsilonPositiveLarge() {
        generalEpsilonTest(1e10+3123);
    }


    public void generalEpsilonTest(double start) {
        Assert.assertTrue(Epsilon.epsilonEquals(start,start)); //reflexive

        Assert.assertFalse(Epsilon.epsilonEquals(start,start+1)); //converse reflexive
        Assert.assertFalse(Epsilon.epsilonEquals(start+1,start));
        Assert.assertFalse(Epsilon.epsilonEquals(start,start-1));
        Assert.assertFalse(Epsilon.epsilonEquals(start-1,start));

        Assert.assertTrue(Epsilon.epsilonEquals(start + 1e-20,start)); //reflexive
        Assert.assertTrue(Epsilon.epsilonEquals(start - 1e-20,start));
        Assert.assertTrue(Epsilon.epsilonEquals(start, start + 1e-20));
        Assert.assertTrue(Epsilon.epsilonEquals(start, start - 1e-20));

        Assert.assertTrue(Epsilon.epsilonEquals(start, start+1, 1.1));
        Assert.assertTrue(Epsilon.epsilonEquals(start+1, start, 1.1));//select epsilon
        Assert.assertTrue(Epsilon.epsilonEquals(start, start-1, 1.1));
        Assert.assertTrue(Epsilon.epsilonEquals(start-1, start, 1.1));

        Assert.assertFalse(Epsilon.epsilonEquals(start, start+1, 0.99));
        Assert.assertFalse(Epsilon.epsilonEquals(start+1, start, 0.99));//select epsilon
        Assert.assertFalse(Epsilon.epsilonEquals(start, start-1, 0.99));
        Assert.assertFalse(Epsilon.epsilonEquals(start-1, start, 0.99));
    }
}
