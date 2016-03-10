package solarsystem.math;

/**
 * Class used to calculate information about a hyperbolic path in the solar system
 *
 * @author Laura McGhie
 */
class Hyper {
	
	private final double mass;
	private final double periapse;
	private final double a;
	private final double GRAVITATIONAL_C = 0.6612e-10;

	/**
	 * Constructor to make a hyperbola with the specified mass, periapse and speed at this point
	 * @param mass of the centre point in kg
	 * @param periapse distance in m
	 * @param speed in m/s
     */
	public Hyper(double mass, double periapse, double speed) {
		this.mass = mass;
		this.periapse = periapse;
		a = (GRAVITATIONAL_C * this.mass) / (speed * speed);
	}

	/**
	 * Return the periapse distance of the hyperbola
	 * @return periapse in m
     */
	public double periapse() {
		return periapse;
	}

	/**
	 * Return the speed at the periapse point in m/s
	 * @return speed in m/s
     */
	public double speed_p() {
		return Math.sqrt(GRAVITATIONAL_C * mass * (2.0 / periapse + 1.0 / a));
	}

	/**
	 * Some other speed
	 * @return speed in m/s
     */
	public double speed_i() {
		return Math.sqrt((GRAVITATIONAL_C * mass) / a);
	}

}
