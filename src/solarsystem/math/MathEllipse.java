package solarsystem.math;

public class MathEllipse {

	public double m; //mass
	public double a; // semimajor axis
	public double e; // eccentricity
	
	// The ellipse in the drawing has an eccentricity = 0.8 so the width of the ellipse (twice the semi-minor axis) is
	//	( 1- 0.8x0.8)1/2 = 0.6 of the length

	public MathEllipse(double mass, double periapse, double apoapse) {
		m = mass;
		a = 0.5 * (periapse + apoapse);
		e = Math.abs(apoapse - periapse) / (2.0 * a);
	}
	
	//Establish a circle ellipse with a given radius & mass
	public MathEllipse (double mass, double radius) 
	{
		m = mass;
		a = radius;
		e = 0.0;
	}

	public String getEllipseData() {
		String s;
		s =  periapse() + " " + apoapse() + " " + period() + " " +  escape();
		s += " " + e + " " + semi_major() + " " + semi_minor();
		return s;
	}
	
	public double semi_minor() {
		double res = a * Math.sqrt(1-(e * e));
		return res;
	}

	public double semi_major() {
		return a;
	}
	
	public double periapse() {
		return a * (1.0 - e);
	}

	public double apoapse() {
		return a * (1.0 + e);
	}

	public double period() {
		double G = 0.6612e-10;
		return 2.0 * Math.PI * Math.sqrt((a*a*a) / (G * m));
	}

	public double escape() {
		double G = 0.6612e-10;
		double vp = Math.sqrt((2.0 * G * m) / periapse()) - speed_p();
		// double va = Math.sqrt((2.0 * G * m) / apoapse()) - speed_a();
		// periapse -- min printf ("At periapse %6.0f, At apoapse %6.0f\n", vp, va);
		return vp;
	}

	public double speed_p()
	{
		double G = 0.6612e-10;
		double H = Math.sqrt(G * m * a * (1.0 - e*e));
		return H / periapse();
	}

	public double speed_a()
	{
		double G = 0.6612e-10;
		double H = Math.sqrt(G * m * a * (1.0 - e*e));
		return H / apoapse();
	}

}
