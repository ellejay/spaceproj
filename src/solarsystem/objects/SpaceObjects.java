package solarsystem.objects;

import java.util.HashMap;
import java.util.Map;

public class SpaceObjects {

	private static final double SCREEN_SCALE = 0.7075;

	private static final BodyInSpace sun = new BodyInSpace("Sun", 1392530, 1.9891e30, 0.0, 0.0, 0.0, null, SCREEN_SCALE);

	private static final BodyInSpace mercury = new BodyInSpace("Mercury", 4878, 3.302e23, 57.92e6, 58.65, 5.2, sun, SCREEN_SCALE);
	private static final BodyInSpace venus = new BodyInSpace("Venus", 12104, 4.8689e24, 108.2e6, 224.7, 1.8, sun, SCREEN_SCALE);
	private static final BodyInSpace earth = new BodyInSpace("Earth", 12756, 5.9742e24, 149.6e6, 365.2, 1.4, sun, SCREEN_SCALE);
	private static final BodyInSpace mars = new BodyInSpace("Mars", 6794, 6.4191e23, 228.0e6, 687.0, 3.6, sun, SCREEN_SCALE);
	private static final BodyInSpace jupiter = new BodyInSpace("Jupiter", 142984, 1.899e27, 779.1e6, 4333.0, 1.6, sun, SCREEN_SCALE);
	private static final BodyInSpace saturn = new BodyInSpace("Saturn", 120000, 5.684e26, 1426.0e6, 10759.0, 4.5, sun, SCREEN_SCALE);
	private static final BodyInSpace uranus = new BodyInSpace("Uranus", 51800, 8.698e25, 2870.0e6, 30685.0, 1.6, sun, SCREEN_SCALE);
	private static final BodyInSpace neptune = new BodyInSpace("Neptune", 49500, 1.028e26, 4493.0e6, 60200.0, 2.4, sun, SCREEN_SCALE);
	private static final BodyInSpace pluto = new BodyInSpace("Pluto", 2200, 1.1e22, 5898.0e6, 90465.0, 2.1, sun, SCREEN_SCALE);
	
	private static final Map<String, BodyInSpace> planetsList = new HashMap<>();
	
	public static Map<String, BodyInSpace> getDictionary() {
		if (planetsList.isEmpty()) {
			planetsList.put("Mercury", mercury);
			planetsList.put("Venus", venus);
			planetsList.put("Earth", earth);
			planetsList.put("Mars", mars);
			planetsList.put("Jupiter", jupiter);
			planetsList.put("Saturn", saturn);
			planetsList.put("Uranus", uranus);
			planetsList.put("Neptune", neptune);
			planetsList.put("Pluto", pluto);
		}
		return planetsList;
	}
	
	public static BodyInSpace getSun() {
		return sun;
	}
	
	public static BodyInSpace getEarth() {
		return earth;
	}
}
