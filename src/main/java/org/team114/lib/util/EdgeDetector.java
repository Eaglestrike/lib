package org.team114.lib.util;

import java.util.concurrent.Callable;

public class EdgeDetector {
    public enum EdgeType {
        FLAT, RISING, FALLING
    }

    private Callable<Boolean> lambda;
    private boolean lastValue = false;

    public EdgeDetector(Callable<Boolean> lambda) {
        this.lambda = lambda;
    }

    /*
     * Updates the latest known value by calling the lambda.
     */
    public boolean update() {
        try {
            lastValue = lambda.call();
        } catch (Exception e) {
            lastValue = false;
        }
        return lastValue;
    }

    public EdgeType getEdge() {
        boolean newValue;
        try {
            newValue = lambda.call();
        } catch (Exception e) {
            newValue = false;
        }

        boolean lastValue = this.lastValue;
        this.lastValue = newValue;

        // true -> false
        if (lastValue && !newValue)
            return EdgeType.FALLING;

        // false -> true
        if (!lastValue && newValue)
            return EdgeType.RISING;

        // (false -> false) | (true -> true)
        return EdgeType.FLAT;
    }

    public boolean falling() {
        return getEdge() == EdgeType.FALLING;
    }

    public boolean rising() {
        return getEdge() == EdgeType.RISING;
    }

    public boolean flatlining() {
        return getEdge() == EdgeType.FLAT;
    }
}
