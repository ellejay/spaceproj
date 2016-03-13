package solarsystem.math;

import java.util.logging.Logger;
import solarsystem.objects.BodyInSpace;

/**
 * Object to work out the transfer times and speeds between bodies in a solar system.
 * @author Laura McGhie
 */
public class Calculator {

	private double dv1, dv2, t, startPhase;
	private String type;
	private BodyInSpace current_p;
	private MathEllipse current_e;

	private final static Logger LOGGER = Logger.getLogger(Calculator.class.getName());

	/**
	 * Constructor for the Calculator class which initialises the calculator with a planet to begin from
	 * @param initialPlanet - body the journey begins from
     */
	public Calculator(BodyInSpace initialPlanet){
		this.current_p = initialPlanet;
		this.current_e = null;
	}

	/**
	 * Method to return the phase angle needed between the two bodies to start the current transfer
	 * @return phase angle in degrees
     */
	public double getStartPhaseAngle() {
		return startPhase;
	}

	/**
	 * Method to return the time needed to complete the current transfer
	 * @return time in seconds
     */
	public double getTime() {
		return t;
	}

	/**
	 * Returns string with the data about the current transfer - the change in speed to start the journey, end the journey
	 * and the time taken displayed in days, hours, minutes and seconds
	 * @return data string
     */
	public String getTransferData() {
		
		double days = Math.floor(t / 86400);
		double hours = Math.floor((t % 86400) / 3600);
		double minutes = Math.floor(((t % 86400) % 3600) / 60);
		double seconds = Math.floor(((t % 86400) % 3600) % 60);
		
		return String.format("Δv Exit = %6.0fm/s\tΔv Entry = %6.0fm/s\r\n\tTransfer Time = %6.0f days " +
				"%6.0f hours %6.0f mins %6.0f s\n", dv1, dv2, days, hours, minutes, seconds);
	}

	/**
	 * Method to be called to make a transfer from the current body to the given body p, with an orbital
	 * ellipse target. If the target orbit is null, we are making a landing on the planet.
	 * @param p - target planet
	 * @param target - target orbit around p
     */
	public void transfer_slow(BodyInSpace p, MathEllipse target){
		// Transferring between orbits
		if (current_p.equals(p)) {
			if (current_e == null) {

				LOGGER.info("Landed on planet.");
				MathEllipse landedOrbit = new MathEllipse(current_p.getMass(), current_p.getRadius());
				transfer(current_p, landedOrbit, target, true);
				dv1 += landedOrbit.speed_p();

			} else if (target == null) {

				LOGGER.info("Landing on destination");
				MathEllipse landedOrbit = new MathEllipse(p.getMass(), p.getRadius());
				transfer(current_p, current_e, landedOrbit, false);
				dv2 += landedOrbit.speed_p();

			} else {

				/* As we are transferring between two orbits, we are transferring between two ellipses - thus transfer
				must always be a->p, and outward journey flag is true regardless. */
				transfer(current_p, current_e, target, true);

			}
			LOGGER.info(String.format("%6.0f %6.0f %6.0f (%s)", dv1, dv2, t, type));
		}
		
		// Sibling Transfer
		else if (current_p.getParent().equals(p.getParent())) {
			transferToSibling(current_p, p, current_e, target);
		}
		
		// Transfer From Child
		else if (current_p.getParent().equals(p)) {
			// Use the same method as transferring to a child, and simply swap the direction and the end points.
			transferToChild(p, current_p, target, current_e, false);
		}
		
		// Transfer To Child
		else if (current_p.equals(p.getParent())) {
			transferToChild(current_p, p, current_e, target, true);
		}

		// If the transfer does not meet any of the previous conditions, it is invalid
		else {
			LOGGER.severe("You cannot make this transfer");
		}

		// Once transfer is complete, update current planet to the target
		current_p = p;
		current_e = target;
	}


	/**
	 * Helper internal method to make transfers between two sibling bodies (bodies who share a parent). Takes parameters
	 * about the start and end points and updates the object variables instead of making a return.
	 * @param startPlanet body the transfer starts from
	 * @param endPlanet body the transfer ends at
	 * @param startOrbit the orbit around the starting body
	 * @param endOrbit the orbit around the destination body
     */
	private void transferToSibling(BodyInSpace startPlanet, BodyInSpace endPlanet, MathEllipse startOrbit,
								   MathEllipse endOrbit) {

		// Check that the two planets are siblings before proceeding
		if (!startPlanet.getParent().equals(endPlanet.getParent())) {
			return;
		}

		/* Get the parent body of the transfer planets, and set up two ellipses for the start
		 * and end points of the journey */
		BodyInSpace parent = startPlanet.getParent();
		MathEllipse stage1 = new MathEllipse(parent.getMass(), startPlanet.getOrbitInM());
		MathEllipse stage2 = new MathEllipse(parent.getMass(), endPlanet.getOrbitInM());

		// Boolean to indicate the direction of the journey - away from the parent or towards it
		boolean outward_journey = startPlanet.getOrbitInM() < endPlanet.getOrbitInM();

		// Make the transfer
		transfer(parent, stage1, stage2, outward_journey);

		/* Establish hyperbolas at the start and end points of the journey to patch conics
		 * and use the speed to amend the delta v required for the journey. */
		Hyper h1 = new Hyper(startPlanet.getMass(), startOrbit.periapse(), dv1);
		Hyper h2 = new Hyper(endPlanet.getMass(), endOrbit.periapse(), dv2);
		dv1 = h1.speed_p() - startOrbit.speed_p();
		dv2 = h2.speed_p() - endOrbit.speed_p();

		/* Calculate the phase angle between the two bodies at the start and end of the transfer journey */
		double endPhase;
		if (startPlanet.getAngularV() > endPlanet.getAngularV()) {

			// Get the phase angle needed to launch the spacecraft
			startPhase = 180.0 + endPlanet.getAngularV() * t;
			while (startPhase > 360.0)
				startPhase -= 360.0;

			// Get the phase angle after the journey has been completed
			endPhase = 180.0 + startPlanet.getAngularV() * t;
			while (endPhase > 360.0)
				endPhase -= 360.0;
		}
		else {
			// Get the phase angle needed to launch the spacecraft
			startPhase = 180.0 - endPlanet.getAngularV() * t;
			while (startPhase < 0.0)
				startPhase += 360.0;

			// Get the phase angle after the journey has been completed
			endPhase = 180.0 - startPlanet.getAngularV() * t;
			while (endPhase < 0.0)
				endPhase += 360.0;
		}

		// Log out phase angles
		LOGGER.info(String.format("%s-%s phase angle before %1.0f after %1.0f", startPlanet.getName(),
				endPlanet.getName(), startPhase, endPhase));
	
	}

	/**
	 * Helper internal method to make transfers from a parent body to a child body. Takes parameters
	 * about the start and end points and updates the object variables instead of making a return.
	 * @param parent parent/start point body in the transfer
	 * @param child child/end point body in the transfer
	 * @param parentOrbit orbit the spacecraft is in around the parent body
	 * @param childOrbit orbit the spacecraft is aiming for around the body
	 * @param outward_journey indication of the direction of the journey
     */
	private void transferToChild(BodyInSpace parent, BodyInSpace child, MathEllipse parentOrbit, MathEllipse childOrbit,
								 boolean outward_journey) {

		// Check that the given parent body is actually the parent of the child
		if (!parent.equals(child.getParent())) {
			return;
		}

		// Set up the orbit ellipse to transfer the spacecraft to
		MathEllipse transfer = new MathEllipse (parent.getMass(), child.getOrbitInM());

		// Make the transfer
		transfer(parent, parentOrbit, transfer, outward_journey);

		/* Create a hyperbola to patch the conic between the new orbit and the transfer path, and use the
		 * speed from this to adjust the delta v. */
		Hyper h2 = new Hyper(child.getMass(), childOrbit.periapse(), dv2);
		dv2 = h2.speed_p() - childOrbit.speed_p();
	}

	/**
	 * Private method to make the transfer between the start and end point. Gathers all the necessary information
	 * to perform the calculations and passes this on
	 * @param p parent body of the transfer ellipse
	 * @param current current orbit around a body
	 * @param target target orbit around a body
	 * @param outward_journey indication of the journey direction
     */
	private void transfer(BodyInSpace p, MathEllipse current, MathEllipse target, boolean outward_journey) {
		double tot = 1.0e20;

		if (outward_journey) {
			horiz_trans("ap", tot, p.getMass(), current.apoapse(), current.speed_a(), target.periapse(), target.speed_p());
		}
		else {
			horiz_trans("pa", tot, p.getMass(), current.periapse(), current.speed_p(), target.apoapse(), target.speed_a());
		}
	}


	/**
	 * Method to perform the transfer calculations itself. Gets the delta vs and the time required to make the journey.
	 * @param ty string to represent the type of transfer
	 * @param tot terminal velocity??
	 * @param mass mass of the parent body of the transfer in kg
	 * @param r1 radius of the starting point of the orbit
	 * @param v1 velocity of the starting point of the orbit
     * @param r2 radius of the end point of the orbit
     * @param v2 velocity at the end point of the orbit
     */
	private void horiz_trans(String ty, double tot, double mass, double r1, double v1, double r2, double v2)
	{
		// Create an ellipse to represent the transfer path itself
		MathEllipse e = new MathEllipse(mass, r1, r2);

		// Get the speed of the spacecraft at the periapsis and apoapsis of the orbit
		double t1 = e.speed_p();
		double t2 = e.speed_a();

		// Swap the speeds if required
		if (r1 > r2) {
			double temp = t1;
			t1 = t2;
			t2 = temp;
		}

		// Calculate the change in velocity needed to reach these speeds
		double d1 = t1 - v1;
		double d2 = v2 - t2;

		// If the speeds are acceptable, set the object variables and get the transfer time
		if (Math.abs(d1) + Math.abs(d2) < tot)
		{
			//tot = Math.abs(d1) + Math.abs(d2);
			dv1 = d1;
			dv2 = d2;
			t = 0.5 * e.period();
			type = ty;
		}
	}

}
