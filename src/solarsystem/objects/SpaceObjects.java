package solarsystem.objects;

import java.util.*;

public class SpaceObjects {

	private static final double SCREEN_SCALE = 0.7075;

	private static final BodyInSpace sun;

	private static final Map<String, BodyInSpace> planetsList = new HashMap<>();

	private static final Map<String, List<Double>> planetScales = new HashMap<>();

	private static final Map<String, BodyInSpace> earthChild = new HashMap<>();
	private static final Map<String, BodyInSpace> marsChild = new HashMap<>();
	private static final Map<String, BodyInSpace> jupiterChild = new HashMap<>();
	private static final Map<String, BodyInSpace> saturnChild = new HashMap<>();
	private static final Map<String, BodyInSpace> uranusChild = new HashMap<>();
	private static final Map<String, BodyInSpace> neptuneChild = new HashMap<>();
	private static final Map<String, BodyInSpace> plutoChild = new HashMap<>();

	static {
		planetScales.put("Sun", new LinkedList<>(Arrays.asList(0.045, 1.25)));
		planetScales.put("Earth", new LinkedList<>(Arrays.asList(450.0, 450.0)));
		planetScales.put("Mars", new LinkedList<>(Arrays.asList(1e4, 1e4)));
		planetScales.put("Jupiter", new LinkedList<>(Arrays.asList(1e1, 1e3)));

		sun = new BodyInSpace("Sun", 1392530, 1.9891e30, 0.0, 0.0, 0.0, null, SCREEN_SCALE);

		planetsList.put("Mercury", new BodyInSpace("Mercury", 4879, 3.302e23, 57.92e6, 58.65, 5.2, sun, SCREEN_SCALE));
		planetsList.put("Venus", new BodyInSpace("Venus", 12104, 4.8689e24, 108.2e6, 224.7, 1.8, sun, SCREEN_SCALE));
		planetsList.put("Earth", new BodyInSpace("Earth", 12756, 5.9742e24, 149.6e6, 365.2, 1.4, sun, SCREEN_SCALE));
		planetsList.put("Mars", new BodyInSpace("Mars", 6792, 6.4191e23, 228.0e6, 687.0, 3.6, sun, SCREEN_SCALE));
		planetsList.put("Jupiter", new BodyInSpace("Jupiter", 142984, 1.899e27, 779.1e6, 4333.0, 1.6, sun, SCREEN_SCALE));
		planetsList.put("Saturn", new BodyInSpace("Saturn", 120536, 5.684e26, 1426.0e6, 10759.0, 4.5, sun, SCREEN_SCALE));
		planetsList.put("Uranus", new BodyInSpace("Uranus", 51118, 8.698e25, 2870.0e6, 30685.0, 1.6, sun, SCREEN_SCALE));
		planetsList.put("Neptune", new BodyInSpace("Neptune", 49528, 1.028e26, 4493.0e6, 60200.0, 2.4, sun, SCREEN_SCALE));
		planetsList.put("Pluto", new BodyInSpace("Pluto", 2370, 1.1e22, 5898.0e6, 90465.0, 2.1, sun, SCREEN_SCALE));

		earthChild.put("Moon", new BodyInSpace("Moon", 3475, 0.073e24, 0.384e6, 27.3, 1.3, planetsList.get("Earth"), SCREEN_SCALE));

		marsChild.put("Deimos", new BodyInSpace("Deimos", 12.7, 2.4e15, 23459, 1.26244, 3.8, planetsList.get("Mars"), SCREEN_SCALE));
		marsChild.put("Phobos", new BodyInSpace("Phobos", 22.6, 10.6e15, 9378, 0.31891, 0.4, planetsList.get("Mars"), SCREEN_SCALE));

		jupiterChild.put("Io", new BodyInSpace("Io", 3643, 893.2e20, 421.6e3, 1.76, 1.3, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Europa", new BodyInSpace("Europa", 3122, 480e20, 670.9e3, 3.55, 0.6, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Ganymede", new BodyInSpace("Ganymede", 5262, 1481.9e20, 1070.4e3, 7.15, 5.7, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Callisto", new BodyInSpace("Callisto", 4821, 1075.9e20, 1882.7e3, 16.69, 2.4, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Adrastea", new BodyInSpace("Adrastea", 35, 2e16, 129e3, 0.3, 1.9, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Amalthea", new BodyInSpace("Amalthea", 250, 7.5e18, 181.4e3, 0.5, 3.8, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Metis", new BodyInSpace("Metis", 40, 1e17, 128e3, 0.29, 0.2, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Thebe", new BodyInSpace("Thebe", 75, 8e17, 221.9e3, 0.67, 4.3, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Leda", new BodyInSpace("Leda", 10, 6e15, 11170e3, 240.92, 4.7, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Himalia", new BodyInSpace("Himalia", 170, 9.5e18, 11460e3, 250.6, 3.1, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Lysithea", new BodyInSpace("Lysithea", 24, 8e16, 11720e3, 259.2, 2.8, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Elara", new BodyInSpace("Elara", 80, 8e17, 11740e3, 259.6, 0.1, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Ananke", new BodyInSpace("Ananke", 20, 4e16, 21280e3, 630, 5.1, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Carme", new BodyInSpace("Carme", 30, 1e17, 23400e3, 734.2, 1.2, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Pasiphae", new BodyInSpace("Pasiphae", 36, 3e17, 23620e3, 744, 0.8, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Sinope", new BodyInSpace("Sinope", 28, 8e16, 23940e3, 759, 4.5, planetsList.get("Jupiter"), SCREEN_SCALE));
	}
	
	public static Map<String, BodyInSpace> getPlanets() {
		return planetsList;
	}

	public static Map<String, BodyInSpace> getChildren(String planet) {
		if (planet.equals("Earth")) {
			return earthChild;
		}
		else if (planet.equals("Mars")) {
			return marsChild;
		}
		else if (planet.equals("Jupiter")) {
			return jupiterChild;
		}
		else {
			return new HashMap<>();
		}
	}

	public static BodyInSpace getSun() {
		return sun;
	}

	public static BodyInSpace getBody(String name) {
		Map<String, BodyInSpace> combined = new HashMap<>();
		combined.putAll(planetsList);
		combined.putAll(earthChild);
		combined.putAll(marsChild);
		combined.putAll(jupiterChild);

		return combined.get(name);
	}

	public static List<Double> getScale(String name) {
		return planetScales.get(name);
	}
}
