package solarsystem.math;

import java.security.InvalidParameterException;

/**
 * Class used to keep information about an elliptical orbit around a given mass
 * for mathematical calculations.
 *
 * @author Laura McGhie
 */
public class MathEllipse {

	private final double mass;
	private final double semimajorAxis;
	private final double eccentricity;
	private static double GRAVITATIONAL_C = 0.6612e-10;

	/**
	 * Constructor to create an Ellipse with the given apoapse and periapse measurements,
	 * and the provided centre mass
	 * @param mass given in kg
	 * @param periapse closest distance to the centre mass on the orbit in m
	 * @param apoapse furthest distance from the centre mass on the orbit in m
     */
	public MathEllipse(double mass, double periapse, double apoapse) {
		this.mass = mass;
		semimajorAxis = 0.5 * (periapse + apoapse);
		eccentricity = Math.abs(apoapse - periapse) / (2 * semimajorAxis);
	}

	/**
	 * Constructor to create a circular Ellipse with the given centre mass
	 * @param mass given in kg
	 * @param radius given in m
     */
	public MathEllipse (double mass, double radius) 
	{
		this.mass = mass;
		semimajorAxis = radius;

		// A circle by definition has an eccentricity of 0
		eccentricity = 0.0;
	}


	/**
	 * Get a string with all the data about this ellipse
	 * @return data string
     */
	public String getEllipseData() {
		String s;
		s =  periapse() + " " + apoapse() + " " + period();
		s += " " + eccentricity + " " + semiMajor() + " " + semiMinor();
		return s;
	}

	/**
	 * Return the semi-minor axis of the given ellipse
	 * @return semi-minor axis in m
     */
	public double semiMinor() { return semimajorAxis * Math.sqrt(1 - (eccentricity * eccentricity)); }

	/**
	 * Return the semi-major axis of the given ellipse
	 * @return semi-major axis in m
     */
	public double semiMajor() {
		return semimajorAxis;
	}

	/**
	 * Return the periapsis of the ellipse, or the closest radial distance around the elliptical path
	 * @return periapse distance in m
     */
	public double periapse() {
		return semimajorAxis * (1.0 - eccentricity);
	}

	/**
	 * Return the apoapsis of the ellipse, or the furthest radial distance around the elliptical path
	 * @return apoapsis distance in m
     */
	public double apoapse() {
		return semimajorAxis * (1.0 + eccentricity);
	}

	/**
	 * Return the period of this ellipse
	 * @return period
     */
	public double period() {
		return 2.0 * Math.PI * Math.sqrt((semimajorAxis * semimajorAxis * semimajorAxis) / (GRAVITATIONAL_C * mass));
	}

	/**
	 * Get the speed of an object at the periapsis of the ellipse, or the closest point from the centre of mass
	 * @return speed in m/s
     */
	public double speed_p()
	{
		double H = Math.sqrt(GRAVITATIONAL_C * mass * semimajorAxis * (1.0 - eccentricity * eccentricity));
		return H / periapse();
	}

	/**
	 * Get the speed of an object at the apoapsis of the ellipse, or the furthest point from the centre of mass
	 * @return speed in m/s
     */
	public double speed_a()
	{
		double H = Math.sqrt(GRAVITATIONAL_C * mass * semimajorAxis * (1.0 - eccentricity * eccentricity));
		return H / apoapse();
	}

}
