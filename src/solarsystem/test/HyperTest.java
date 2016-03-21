package solarsystem.test;


import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;
import solarsystem.math.Hyper;

/**
 * Test class for the Hyper object
 * @author Laura McGhie
 */
public class HyperTest {

    double a, b, c;

    @Before
    public void setUp() throws Exception {
        // Use random numbers to test on variety of cases
        a = Math.random() * 1000000000 + 1;
        b = Math.random() * 1000000000 + 1;
        c = Math.random() * 1000000000 + 1;
    }

    @Test
    public void testPeriapse() throws Exception {
        Hyper hyper = new Hyper(a, b, c);
        Assert.assertTrue(hyper.periapse() == b);
    }

    @Test
    public void testSpeed_p() throws Exception {
        Hyper hyper = new Hyper(a, b, c);

        // Work out the excepted speed
        double semiMajorAxis = (0.6612e-10 * a) / (c * c);
        double res = Math.sqrt(0.6612e-10 * a * (2.0 / b + 1.0 / semiMajorAxis));

        Assert.assertTrue(hyper.speed_p() == res);
    }
}