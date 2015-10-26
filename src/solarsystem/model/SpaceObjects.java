package src.solarsystem.model;

import src.solarsystem.objects.BodyInSpace;

public class SpaceObjects {
	
	static double SCREEN_SCALE = 0.7075;
	
	static BodyInSpace sun = new BodyInSpace("sun", 0.0, 0.0, 0.0, null, SCREEN_SCALE);
	
	static BodyInSpace mercury = new BodyInSpace("mercury", 57.92, 58.65, 5.2, sun, SCREEN_SCALE);
	static BodyInSpace venus = new BodyInSpace("venus", 108.2, 224.7, 1.8, sun, SCREEN_SCALE);
	static BodyInSpace earth = new BodyInSpace("earth", 149.6, 365.2, 1.4, sun, SCREEN_SCALE);
	static BodyInSpace mars = new BodyInSpace("mars", 228.0, 687.0, 3.6, sun, SCREEN_SCALE);
	static BodyInSpace jupiter = new BodyInSpace("jupiter", 779.1, 4333.0, 1.6, sun, SCREEN_SCALE);
	static BodyInSpace saturn = new BodyInSpace("saturn", 1426.0, 10759.0, 4.5, sun, SCREEN_SCALE);
	static BodyInSpace uranus = new BodyInSpace("uranus", 2870.0, 30685.0, 1.6, sun, SCREEN_SCALE);
	static BodyInSpace neptune = new BodyInSpace("neptune", 4493.0, 60200.0, 2.4, sun, SCREEN_SCALE);
	

	private static BodyInSpace[] planets = {mercury, venus, earth, mars, jupiter, saturn, uranus, neptune};
	
	public static BodyInSpace[] getPlanets() {
		return planets;
	}
	
	public static BodyInSpace getSun() {
		return sun;
	}

}
