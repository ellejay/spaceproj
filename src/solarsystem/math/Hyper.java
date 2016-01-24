package solarsystem.math;

public class Hyper {
	
	public double m, p, a;
	private double G = 0.6612e-10;
	
	public Hyper(double mass, double periapse, double speed) {
		m = mass;
		p = periapse;
		a = (G * m) / (speed * speed);
	}
	
	public double periapse() {
		return p;
	}
	
	public double speed_p() {
		return Math.sqrt(G * m * (2.0 / p + 1.0 / a));
	}
	
	public double speed_i() {
		return Math.sqrt((G * m) / a);
	}

}
