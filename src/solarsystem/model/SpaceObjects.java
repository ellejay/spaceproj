package solarsystem.model;

import solarsystem.objects.BodyInSpace;

public class SpaceObjects {
	
	static double SCREEN_SCALE = 0.7075;
	
	static BodyInSpace sun = new BodyInSpace("Sun", 1392530, 1.9891e30, 0.0, 0.0, 0.0, null, SCREEN_SCALE);
	
	static BodyInSpace mercury = new BodyInSpace("Mercury", 4878, 3.302e23, 57.92, 58.65, 5.2, sun, SCREEN_SCALE);
	static BodyInSpace venus = new BodyInSpace("Venus", 12104, 4.8689e24, 108.2, 224.7, 1.8, sun, SCREEN_SCALE);
	static BodyInSpace earth = new BodyInSpace("Earth", 12756, 5.9742e24, 149.6, 365.2, 1.4, sun, SCREEN_SCALE);
	static BodyInSpace mars = new BodyInSpace("Mars", 6794, 6.4191e23, 228.0, 687.0, 3.6, sun, SCREEN_SCALE);
	static BodyInSpace jupiter = new BodyInSpace("Jupiter", 142984, 1.899e27, 779.1, 4333.0, 1.6, sun, SCREEN_SCALE);
	static BodyInSpace saturn = new BodyInSpace("Saturn", 120000, 5.684e26, 1426.0, 10759.0, 4.5, sun, SCREEN_SCALE);
	static BodyInSpace uranus = new BodyInSpace("Uranus", 51800, 8.698e25, 2870.0, 30685.0, 1.6, sun, SCREEN_SCALE);
	static BodyInSpace neptune = new BodyInSpace("Neptune", 49500, 1.028e26, 4493.0, 60200.0, 2.4, sun, SCREEN_SCALE);
	static BodyInSpace pluto = new BodyInSpace("Pluto", 2200, 1.1e22, 5898.0, 90465.0, 2.1, sun, SCREEN_SCALE);
	
	private static BodyInSpace[] planets = {mercury, venus, earth, mars, jupiter, saturn, uranus, neptune, pluto};
	
	public static BodyInSpace[] getPlanets() {
		return planets;
	}
	
	public static BodyInSpace getSun() {
		return sun;
	}
	
	public static BodyInSpace getEarth() {
		return earth;
	}
}
