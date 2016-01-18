package solarsystem.math;

import java.util.Map;

import solarsystem.model.SpaceObjects;
import solarsystem.objects.BodyInSpace;

public class Calculator {

	public static double dv1, dv2, t;
	public static String type;

	public static void main(String[] args) {

		double G = 0.6612e-10; /* m3.s-2.kg-1 */
		double PI  = 3.14159;

		//System.out.printf("%10s %10s %10s %6s %6s\n", "", "DISTANCE", "RADIUS", "G", "ESCAPE");

		Map<String, BodyInSpace> p = SpaceObjects.getDictionary();
		BodyInSpace earth = SpaceObjects.getEarth();
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

		Calculator c = new Calculator();

		// convert from input (km above surface) to internal
		// (m from centre)
		double r1 = earth.getRadius() + 1.0e3 * 200;
		double r2 = earth.getRadius() + 1.0e3 * 200;
		//System.out.println(r1 + " " + r2);
		MathEllipse x = new MathEllipse(earth.getMass(), r1, r2);
		//System.out.println(x.getEllipseData());
		//c.transfer_slow(earth, earth, x);


		MathEllipse x1 = new MathEllipse(earth.getMass(), 520, 200);
		System.out.println(x1.getEllipseData());

	}

	public void transfer_slow(BodyInSpace cur_p, BodyInSpace p, MathEllipse target){
		double d1, d2, ts; // increments for take off and landing
		String tys;

		/* same primary */

		if (cur_p.equals(p))
		{
			if (true) // currently landed on planet
			{
				MathEllipse e1 = new MathEllipse(cur_p.getMass(), cur_p.getRadius());
				transfer(cur_p, e1, target);
				d1 = dv1 + e1.speed_p();
				d2 = dv2;
				ts = t;
				tys = type;
			}
			System.out.printf("%6.0f %6.0f %6.0f (%s) \n", d1, d2, ts, tys);
		}
	}

	public void transfer(BodyInSpace p, MathEllipse current, MathEllipse target) {
		double tot = 1.0e20; 
		horiz_trans("pp", tot, p.getMass(), current.periapse(), current.speed_p(), target.periapse() , target.speed_p());
		horiz_trans("aa", tot, p.getMass(), current.apoapse(), current.speed_a(), target.apoapse(), target.speed_a());
		horiz_trans("pa", tot, p.getMass(), current.periapse(), current.speed_p(), target.apoapse(), target.speed_a());
		horiz_trans("ap", tot, p.getMass(), current.apoapse(), current.speed_a(), target.periapse(), target.speed_p());

	}


	public void horiz_trans (String ty, double tot, double mass, double r1, double v1, double r2, double v2)
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
