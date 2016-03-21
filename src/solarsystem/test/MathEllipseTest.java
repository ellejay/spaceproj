package solarsystem.test;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import solarsystem.math.MathEllipse;

/**
 * Test class for the MathEllipse object
 * @author Laura McGhie
 */
public class MathEllipseTest {

    double a, b, c;

    @Before
    public void setUp() throws Exception {
        // Use random numbers to test on variety of cases
        a = (int) (Math.random() * 1000000 + 1);
        b = (int) (Math.random() * 1000000 + 1);
        c = (int) (Math.random() * 1000000 + 1);

        if (b > c) {
            double temp = b;
            b = c;
            c = temp;
        }
    }

    @Test
    public void testCircle() throws Exception {
        MathEllipse ellipse = new MathEllipse(a, b);
        Assert.assertTrue(ellipse.semiMajor() == ellipse.semiMinor());
    }

    @Test
    public void testSemiMajor() throws Exception {
        MathEllipse ellipse = new MathEllipse(a, b, c);
        Assert.assertTrue(ellipse.semiMajor() == (( b + c ) / 2));
    }

    @Test
    public void testSemiMinor() throws Exception {
        MathEllipse ellipse = new MathEllipse(a, b, c);

        double semimajor = ((b + c) / 2 );
        double e = Math.abs(c - b) / (2 * semimajor);
        double res = semimajor * Math.sqrt(1 - (e * e));

        Assert.assertTrue(Math.abs(ellipse.semiMinor() - res) < ( res / 5 ));
    }

    @Test
    public void testPeriapse() throws Exception {
        MathEllipse ellipse = new MathEllipse(a, b, c);

        Assert.assertTrue(Math.abs(ellipse.periapse() - b) < (c/5));
    }

    @Test
    public void testApoapse() throws Exception {
        MathEllipse ellipse = new MathEllipse(a, b, c);

        Assert.assertTrue(Math.abs(ellipse.apoapse() - c) < (c/5));
    }

    @Test
    public void testPeriod() throws Exception {
        MathEllipse ellipse = new MathEllipse(a, b, c);

        double semiMajor = ( b + c ) / 2;
        double period = 2.0 * Math.PI * Math.sqrt(semiMajor * semiMajor * semiMajor / (0.6612e-10 * a));

        Assert.assertTrue(ellipse.period() == period);
    }

    @Test
    public void testCircleSpeed() throws Exception {
        MathEllipse ellipse = new MathEllipse(a, b);
        Assert.assertTrue(ellipse.speed_p() == ellipse.speed_a());
    }

    @Test
    public void testCircleApsis() throws Exception {
        MathEllipse ellipse = new MathEllipse(a, b);
        Assert.assertTrue(ellipse.periapse() == ellipse.apoapse());
    }

    @Test
    public void testSpeed_p() throws Exception {
        MathEllipse ellipse = new MathEllipse(a, b, c);

        double semiMajor = ( b + c ) / 2;
        double res = Math.sqrt(0.6612e-10 * a * (2.0 / b - 1.0 / semiMajor));

        Assert.assertTrue(Math.abs(ellipse.speed_p() - res) < (res/5));
    }

    @Test
    public void testSpeed_a() throws Exception {
        MathEllipse ellipse = new MathEllipse(a, b, c);

        double semiMajor = ( b + c ) / 2;
        double res = Math.sqrt(0.6612e-10 * a * (2.0 / c - 1.0 / semiMajor));

        Assert.assertTrue(Math.abs(ellipse.speed_a() - res) < (res/5));
    }
}