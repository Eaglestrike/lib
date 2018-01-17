package org.team114.lib.pathgenerator;


import org.junit.*;
import org.team114.lib.util.Epsilon;

// NOTE: this test depends on EpsilonTest!
public class PolynomialTest {

    @Test
    public void lowCoefficientPolynomialsEvaluation() {
        Polynomial p = new Polynomial(new double[]{7, 3, 5, 15, 2.2, -5.5, 0.0145, -0.234});
        Assert.assertEquals(35312.3125, p.eval(-5), 1.0);
        Assert.assertEquals(-757174244.355, p.eval(22.7), 1.0);
        Assert.assertEquals(7.03987801579, p.eval(0.013), 0.1);
        Assert.assertEquals(4.87014855263, p.eval(-0.547), 0.1);
    }

    @Test
    public void lowCoefficientPolynomialsDerivatives() {
        Polynomial p = new Polynomial(new double[]{7, 3, 5, 15, 2.2, -5.5, 0.0145, -0.234}).ddx();
        System.out.println(p.coefficients[1]);
        Assert.assertEquals(-43075.125, p.eval(-5), 1.0);
        Assert.assertEquals(-230765196.422, p.eval(22.7), 1.0);
        p = p.ddx();
        Assert.assertEquals(30.7530816704, p.eval(0.23), 0.5);
        Assert.assertEquals(53407.243561, p.eval(-5.2), 1.0);
    }
}
