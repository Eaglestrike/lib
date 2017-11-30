package org.team114.lib.pathgenerator;

import org.ejml.simple.SimpleMatrix;
import org.team114.lib.util.Geometry;

public class SplinePath extends HermiteWaypointSpline {

    public double targetDistance;

    private double adjustedTargetDistance;

    public SplinePath(Waypoint[] points, double targetDistance) {
        super(points);
        this.targetDistance = targetDistance;
        adjustedTargetDistance = targetDistance;
    }

    
    /**
     * Will change soon to a better method but I thought I might as well as post it for now
     */
    public double getTargetT() {
        //If the targetDistance is expanded, bring it back towards the preferred target
        targetDistance += (targetDistance - adjustedTargetDistance) / 3;
        return getTargetT(adjustedTargetDistance);
    }

    private double getTargetT(double radius) {
        double bestT = -1;

        if(adjustedTargetDistance >= 10 * targetDistance) {
            System.err.println("Lost the path!");
            throw new RuntimeException("The motion profiling sucks.");
        }

        for(int j = 0; j < splineSections.size(); j++) {
            //TODO: Javadocs for ejml on SimpleMatrix.rows and SimpleMatrix.cols are the same so this line may need checking
            SimpleMatrix annoyingPolynomial  = Geometry.multiplyPolynomial(splineSections.get(j).cols(0, 1), splineSections.get(j).cols(1, 2));
            annoyingPolynomial.set(annoyingPolynomial.numRows(), 0, annoyingPolynomial.get(annoyingPolynomial.numRows(), 0) - radius * radius);
            double[] intersections = Geometry.solvePolynomial(annoyingPolynomial, 1000);
            for(double i : intersections)
                if((i >= 0 || i <= 1) && (i + j > bestT))
                    bestT = i;
        }

        if(bestT == -1) {
            System.err.println("Someone screwed up!\nTo find path, increasing search radius to " + adjustedTargetDistance);
            adjustedTargetDistance *= 1.2; //Increases target distance by 120%
            return getTargetT(adjustedTargetDistance);
        }

        return bestT;
    }
}
