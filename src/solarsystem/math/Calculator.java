package solarsystem.math;

import java.util.Map;

import solarsystem.objects.BodyInSpace;
import solarsystem.objects.SpaceObjects;

public class Calculator {

	private double dv1, dv2, t, ph1, ph2;
	private String type;
	private BodyInSpace current_p, new_p;
	private MathEllipse current_e, new_e;
	private double G = 0.6612e-10;
	
	private Calculator(BodyInSpace initialPlanet, MathEllipse initialOrbit){
		this.current_p = initialPlanet;
		this.current_e = initialOrbit;
	}
	
	public Calculator(BodyInSpace initialPlanet){
		this.current_p = initialPlanet;
		this.current_e = null;
	}

	public static void main(String[] args) {

		double G = 0.6612e-10; /* m3.s-2.kg-1 */
		double PI  = 3.14159;

		//System.out.printf("%10s %10s %10s %6s %6s\n", "", "DISTANCE", "RADIUS", "G", "ESCAPE");

		Map<String, BodyInSpace> p = SpaceObjects.getPlanets();
		BodyInSpace earth = SpaceObjects.getBody("Earth");
		for (BodyInSpace planet: p.values()) {

			//System.out.println(planet.getName() + " " + planet.getRadius() + " " + planet.getOrbit() + " " + planet.getOrbitInM());

			double r = 1.0e-9 * planet.getOrbitInM();
			double d = 1.0e-3 * planet.getRadius();
			double me = planet.getMass() / earth.getMass();
			double re = planet.getRadius() / earth.getRadius();
			double escape = Math.sqrt((2.0 * G * planet.getMass()) / planet.getRadius());

			//System.out.printf("%-10s %10.2f %10.2f %6.2f %6.0f \n", planet.getName(), r, d, me / (re * re), escape);

		}

		//wait time
		double av1 = p.get("Mars").getAngularV();
		double av2 = p.get("Earth").getAngularV();
		double angle = 24.6;
		double days = (av1 > av2) ? angle / (av1 - av2) : angle / (av2 - av1);
		//System.out.println(days);

		// TRANSFER WORK

		MathEllipse start = null;
		
		Calculator c = new Calculator(p.get("Earth"), start);

		// convert from input (km above surface) to internal
		// (m from centre)
		double r1 = earth.getRadius() + 1.0e3 * 200;
		double r2 = earth.getRadius() + 1.0e3 * 200;
		//System.out.println(r1 + " " + r2);
		MathEllipse x = new MathEllipse(earth.getMass(), r1, r2);
		//System.out.println(x.getEllipseData());
		c.transfer_slow(earth, x);

		BodyInSpace mars = p.get("Earth");
		double s1 = mars.getRadius() + 1.0e3 * 0;
		double s2 = mars.getRadius() + 1.0e3 * 0;
		MathEllipse y = new MathEllipse(mars.getMass(), s1, s2);
		c.transfer_slow(mars, null);

		//MathEllipse x1 = new MathEllipse(earth.getMass(), 520, 200);
		//System.out.println(x1.getEllipseData());

	}
	
	public double getTime() {
		return t;
	}
	
	public String getTransferData() {
		
		double days = Math.floor(t / 86400);
		double hours = Math.floor((t % 86400) / 3600);
		double minutes = Math.floor(((t % 86400) % 3600) / 60);
		double seconds = Math.floor(((t % 86400) % 3600) % 60);
		
		return String.format("Δv Exit = %6.0fm/s\tΔv Entry = %6.0fm/s\r\n\tTransfer Time = %6.0f days %6.0f hours %6.0f mins %6.0f s\n", dv1, dv2, days, hours, minutes, seconds);
	}

	public void transfer_slow(BodyInSpace p, MathEllipse target){
		//double d1, d2, ts; // increments for take off and landing
		//String tys;

		// Orbital Transfer

		if (current_p.equals(p))
		{
			if (current_e == null) // currently landed on planet
			{
				System.out.println("landed");
				MathEllipse e1 = new MathEllipse(current_p.getMass(), current_p.getRadius());
				transfer(current_p, e1, target, true);
				dv1 += e1.speed_p();
			}
			else if (target == null) // landing on planet surface
			{
				System.out.println("landing on destination");
				MathEllipse e1 = new MathEllipse(p.getMass(), p.getRadius());
				transfer(current_p, current_e, e1, false);
				dv2 += e1.speed_p();
			}
			else {
				// TEST TRUE, FIX FOR ACTUAL A & P
				boolean outward_journey = !(current_e.periapse() == target.periapse() ||
						current_e.periapse() < target.periapse());

				transfer(current_p, current_e, target, outward_journey);
			}
			System.out.printf("%6.0f %6.0f %6.0f (%s) \n", dv1, dv2, t, type);
		}
		
		// Sibling Transfer
		
		else if (current_p.getParent().equals(p.getParent())) {
			transferToSibling(current_p, p, current_e, target);
		}
		
		// Transfer From Child
		else if (current_p.getParent().equals(p)) {
			transferToChild(p, current_p, target, current_e, false);
		}
		
		// Transfer To Child
		else if (current_p.equals(p.getParent())) {
			transferToChild(current_p, p, current_e, target, true);
		}
		
		else {
			System.out.println("You cannot make this transfer");
		}
		
		current_p = p;
		current_e = target;
	}


	private void transferToSibling(BodyInSpace startPlanet, BodyInSpace endPlanet, MathEllipse startOrbit, MathEllipse endOrbit) {
		double w1, w2, T;
		
		if (!startPlanet.getParent().equals(endPlanet.getParent())) {
			return;
		}
		
		BodyInSpace parent = startPlanet.getParent();
		MathEllipse stage1 = new MathEllipse(parent.getMass(), startPlanet.getOrbitInM());
		MathEllipse stage2 = new MathEllipse(parent.getMass(), endPlanet.getOrbitInM());
		
		boolean outward_journey = startPlanet.getOrbitInM() < endPlanet.getOrbitInM();
		
		transfer(parent, stage1, stage2, outward_journey);
		
		Hyper h1 = new Hyper(startPlanet.getMass(), startOrbit.periapse(), dv1);
		Hyper h2 = new Hyper(endPlanet.getMass(), endOrbit.periapse(), dv2);
		dv1 = h1.speed_p() - startOrbit.speed_p();
		dv2 = h2.speed_p() - endOrbit.speed_p();
		
		
		w1 = startPlanet.getAngularV();
		w2 = endPlanet.getAngularV();
		T = t;
		
		
		if (w1 > w2) // inner to outer
		{
			ph1 = 180.0 + w2 * T;
			while (ph1 > 360.0)
				ph1 -= 360.0;
			ph2 = 180.0 + w1 * T;
			while (ph2 > 360.0)
				ph2 -= 360.0;
		}
		else // outer to inner
		{
			ph1 = 180.0 - w2 * T;
			while (ph1 < 0.0)
				ph1 += 360.0;
			ph2 = 180.0 - w1 * T;
			while (ph2 < 0.0)
				ph2 += 360.0;
		}
		
		String test = "";
		if (outward_journey) {
			test = "ap";
		}
		else {
			test = "pa";
		}
				
		System.out.println(dv1 + " " + dv2 + " " + t + " (" + test + ")");
		System.out.printf("%s-%s phase angle before %1.0f after %1.0f\n", startPlanet.getName(), endPlanet.getName(), ph1, ph2);
	
	}
	
	public double getStartPhaseAngle() {
		return ph1;
	}
	
	private void transferToChild(BodyInSpace parent, BodyInSpace child, MathEllipse parentOrbit, MathEllipse childOrbit,
								 boolean outward_journey) {
		
		if (!parent.equals(child.getParent())) {
			return;
		}
		
		MathEllipse transfer = new MathEllipse (parent.getMass(), child.getOrbitInM());
		transfer(parent, parentOrbit, transfer, outward_journey);
		
		Hyper h2 = new Hyper(child.getMass(), childOrbit.periapse(), dv2);
		dv2 = h2.speed_p() - childOrbit.speed_p();
		
		System.out.println(dv1 + " " + dv2 + " " + t);
		
	}

	private void transfer(BodyInSpace p, MathEllipse current, MathEllipse target, boolean outward_journey) {
		double tot = 1.0e20; 
		//horiz_trans("pp", tot, p.getMass(), current.periapse(), current.speed_p(), target.periapse() , target.speed_p());
		//horiz_trans("aa", tot, p.getMass(), current.apoapse(), current.speed_a(), target.apoapse(), target.speed_a());
		//horiz_trans("pa", tot, p.getMass(), current.periapse(), current.speed_p(), target.apoapse(), target.speed_a());
		
		//System.out.println(current.apoapse() + " " + current.speed_a());
		
		if (outward_journey) {
			horiz_trans("ap", tot, p.getMass(), current.apoapse(), current.speed_a(), target.periapse(), target.speed_p());
		}
		else {
			horiz_trans("pa", tot, p.getMass(), current.periapse(), current.speed_p(), target.apoapse(), target.speed_a());
		}
	}


	private void horiz_trans(String ty, double tot, double mass, double r1, double v1, double r2, double v2)
	{
		MathEllipse e = new MathEllipse(mass, r1, r2);
		double t1 = e.speed_p();
		double t2 = e.speed_a();

		if (r1 > r2) {
			double temp = t1;
			t1 = t2;
			t2 = temp;
		}
		double d1 = t1 - v1;
		double d2 = v2 - t2;
		if (Math.abs(d1) + Math.abs(d2) < tot)
		{
			tot = Math.abs(d1) + Math.abs(d2);
			dv1 = d1;
			dv2 = d2;
			t = 0.5 * e.period();
			type = ty;
		}
	}

}
