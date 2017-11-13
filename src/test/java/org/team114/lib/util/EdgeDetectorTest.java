package org.team114.lib.util;

import org.junit.*;

public class EdgeDetectorTest  {

    boolean value;
    public EdgeDetector edgeDetector = new EdgeDetector(this::getValue);

    private boolean getValue() {
        return value;
    }

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
