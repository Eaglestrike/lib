package org.team114.lib.util;

import org.junit.*;

/**
 * Tests for {@link EdgeDetector}.
 */
public class EdgeDetectorTest  {

    private boolean value;
    private EdgeDetector edgeDetector = new EdgeDetector(this::getValue);
    private boolean getValue() {
        return value;
    }

    /**
     * Tests the enum response of the getEdge function.
     */
    @Test
    public void testGetEdge() {

        value = false;
        edgeDetector.update();
        value = true;
        Assert.assertEquals(edgeDetector.getEdge(), EdgeDetector.EdgeType.RISING);

        value = true;
        edgeDetector.update();
        value = false;
        Assert.assertEquals(edgeDetector.getEdge(), EdgeDetector.EdgeType.FALLING);

        value = false;
        edgeDetector.update();
        value = false;
        Assert.assertEquals(edgeDetector.getEdge(), EdgeDetector.EdgeType.FLAT);

        value = true;
        edgeDetector.update();
        value = true;
        Assert.assertEquals(edgeDetector.getEdge(), EdgeDetector.EdgeType.FLAT);
    }

    /**
     * Tests the boolean response of the falling (true -> false) function.
     */
    @Test
    public void testFalling() {
        value = false;
        edgeDetector.update();
        value = true;
        Assert.assertFalse(edgeDetector.falling());

        value = true;
        edgeDetector.update();
        value = false;
        Assert.assertTrue(edgeDetector.falling());

        value = false;
        edgeDetector.update();
        value = false;
        Assert.assertFalse(edgeDetector.falling());

        value = true;
        edgeDetector.update();
        value = true;
        Assert.assertFalse(edgeDetector.falling());
    }

    /**
     * Tests the boolean response of the rising (false -> true) function.
     */
    @Test
    public void testRising() {
        value = false;
        edgeDetector.update();
        value = true;
        Assert.assertTrue(edgeDetector.rising());

        value = true;
        edgeDetector.update();
        value = false;
        Assert.assertFalse(edgeDetector.rising());

        value = false;
        edgeDetector.update();
        value = false;
        Assert.assertFalse(edgeDetector.rising());

        value = true;
        edgeDetector.update();
        value = true;
        Assert.assertFalse(edgeDetector.rising());
    }

    /**
     * Tests the boolean response of the flatlining (true -> true | false -> false)
     * function.
     */
    @Test
    public void testFlatlining() {
        value = false;
        edgeDetector.update();
        value = true;
        Assert.assertFalse(edgeDetector.flatlining());

        value = true;
        edgeDetector.update();
        value = false;
        Assert.assertFalse(edgeDetector.flatlining());

        value = false;
        edgeDetector.update();
        value = false;
        Assert.assertTrue(edgeDetector.flatlining());

        value = true;
        edgeDetector.update();
        value = true;
        Assert.assertTrue(edgeDetector.flatlining());
    }
}
