package org.team114.lib.util;

import java.util.ArrayList;
import java.util.Arrays;

import org.ejml.simple.SimpleMatrix;

/**
 * This class contains general geometric functions mostly related to polynomials which are often
 * used in path finding but can also be used in other areas.
 */
public class Geometry {



    /**
     * Used when an accepted error is not specified for solvePolynomial().
     */
    private static final double defaultPolynomialErrorLimit = 1E-5;

    /**
     * Solves a polynomial.
     * @param P is the polynomial to solve
     * @param x to put into polynomial
     * @return the value of P(x)
     */
    public static double solvePolynomial(SimpleMatrix P, double x) {
        double mult = 1, end = 0;
        for(int i = 0; i < P.numRows(); i++, mult *= x)
            end += mult * P.get(i, 0);
        return end;
    } 
    
    /**
     * Solves a parametric function for a point.
     * @param P is the polynomial to solve
     * @param x to put into polynomial
     * @return the value of P(x)
     */
    public static Point solveParametricPolynomial(SimpleMatrix P, double x) {
        return new Point(solvePolynomial(P.cols(0, 1), x), solvePolynomial(P.cols(1, 2), x));
    }
    
    /**
     * Solves a parametric function for a point containing the x and y derivative.
     * @param P is the polynomial to solve
     * @param x to put into polynomial
     * @return the value of P(x)
     */
    public static Point solveParametricDerivative(SimpleMatrix P, double x) {
        return new Point(getDerivative(P.cols(0, 1), x), getDerivative(P.cols(1, 2), x));
    }
    
    /**
     * Solves a polynomial.
     * @param P is the polynomial to solve
     * @param x to put into polynomial
     * @return the value of P(x)
     */
    public static SimpleMatrix getDerivative(SimpleMatrix P) {
        double[][] end = new double[P.numRows() - 1][1];
        for(int i = 1; i < P.numRows(); i++)
            end[i - 1][0] = i * P.get(i, 0);  
        return new SimpleMatrix(end);
    }

    /**
     * Solves for the derivative. Works the same as solvePolynomial(getDerivative(A)), but a tiny
     * bit more efficient.
     * @param P is the polynomial to solve
     * @param x to put into polynomial
     * @return the value of P(x)
     */
    public static double getDerivative(SimpleMatrix P, double x) {
        double mult = 1, end = 0;        
        for(int i = 1; i < P.numRows(); i++, mult *= x)
            end += mult * i * P.get(i, 0);  
        return end;
    }
    
    /**
     * Simply multiples two polynomials in matrix form.
     * @param a - First polynomial
     * @param b - Second polynomial
     * @return the produce of polynomials a and b
     */
    public static SimpleMatrix multiplyPolynomial(SimpleMatrix a, SimpleMatrix b) {
        double[][] result = new double[a.numRows() + b.numRows()-1][1];
        for(int i = 0; i < a.numRows(); i++)
            for(int j = 0; j < b.numRows(); j++)
                result[i+j][0] += a.get(i, 0) * b.get(j, 0);
        return new SimpleMatrix(result);
    }
    
    /**
     * Simply adds two polynomials in matrix form.
     * @param a - First polynomial
     * @param b - Second polynomial
     * @return the sum of polynomials a and b
     */
    public static SimpleMatrix addPolynomial(SimpleMatrix a, SimpleMatrix b) {
        double[][] result = new double[(int)Math.max(a.numRows(),b.numRows())][1];
        for(int i = 0; i < Math.max(a.numRows(),b.numRows()); i++)
                result[i][0] += (i < a.numRows() ? a.get(i, 0) : 0) + (i < b.numRows() ? b.get(i, 0) : 0);
        return new SimpleMatrix(result);
    }

    /**
     * Uses newton's method to solve for the roots of the given polynomial of any length.
     * Newton's method will not give exact results but will give more precision with more
     * iterations.
     * @param polynomial in form [a, b, c, d, e, f...]
     * @param iterations - Number of iterations to run newton's method with
     * @param acceptedError - The amount of error for a solution allowed to consider it correct
     * @return
     */
    public static double[] solvePolynomial(SimpleMatrix polynomial, int iterations, double acceptedError) {
        SimpleMatrix P = polynomial;
        double runner = 0;
        ArrayList<Double> realSolutions = new ArrayList<Double>();
        double fx = 0, dx = 0;

        //test each degree of polynomial
        for(int j = 0; j < polynomial.numRows(); j++) {
            runner = 0;
            for(int i = 0; i < iterations; i++) {

                fx = dx = 0;
                //I wanted to use matrices but they didn't really work for this
                for(int a = 0; a < P.numRows(); a++) {
                    fx += Math.pow(runner, a) * P.get(a, 0);
                    
                    double Dp = P.get(a, 0) * Math.pow(runner, a - 1) * a;
                    if(!Double.isNaN(Dp))
                        dx += Dp;
                }
                
                //to prevent the answers from being NaN
                if(dx == 0) {
                    runner *= 1.1;
                    continue;
                }
                
                //The heart of newton's method
                runner = runner - fx / dx;

                //break if perfect solution
                if(Math.abs(fx) <= acceptedError)
                    break;
            }
            //test predicted root against initial polynomial
            double test = 0;
            for(int a = 0; a < polynomial.numRows(); a++)
                test += Math.pow(runner, a) * polynomial.get(a, 0);

            if(Math.abs(test) <= acceptedError) {
                realSolutions.add(runner);
                if(P.numRows() > 2)
                    P = reducePolynomialByRoot(P, runner);
                else break;
            }else { 
                break;
            }
        }

        //convert to array for easier use
        double[] end = new double[realSolutions.size()];
        for(int i = 0; i < realSolutions.size(); i++)
            end[i] = realSolutions.get(i);
        return end;
    }

    /**
     * This function uses synthetic division to divide a root out of an n length polynomial.
     * Note that this method ignores the remainder. 
     * @param polynomial - The initial polynomial.
     * @param root - The root by which to divide the polynomial
     * @return - A new polynomial
     */
    public static SimpleMatrix reducePolynomialByRoot(SimpleMatrix polynomial, double root) {
        if(polynomial == null || polynomial.numRows() < 1)
            throw new RuntimeException("Invalid polynomial");

        double[][] coefficients = new double[polynomial.numRows() - 1][1];

        coefficients[coefficients.length - 1][0] = polynomial.get(polynomial.numRows() - 1,0);
        
        for(int i = polynomial.numRows() - 3; i >= 0; i--) {
            coefficients[i][0] = root * coefficients[i + 1][0] + polynomial.get(i + 1, 0);
        }

        return new SimpleMatrix(coefficients);
    }

    /**
     * Uses newton's method to solve for the roots of the given polynomial of any length.
     * Newton's method will not give exact results but will give more precision with more
     * iterations.
     * @param polynomial in form [a, b, c, d, e, f...]
     * @param iterations - Number of iterations to run newton's method with
     * @return
     */
    public static double[] solvePolynomial(SimpleMatrix polynomial, int iterations) {
        return solvePolynomial(polynomial, iterations, defaultPolynomialErrorLimit);
    }

    /**
     * A simple distance formula between points 1 and 2 located at (x1, y1) and (x2, y2).
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return the distance between the two points.
     */
    public static double dist(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x1-x2, 2) + Math.pow(y1-y2, 2));
    }

}
