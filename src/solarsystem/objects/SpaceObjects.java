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
		planetScales.put("Saturn", new LinkedList<>(Arrays.asList(80.0, 1e3)));
		planetScales.put("Uranus", new LinkedList<>(Arrays.asList(5e2, 4e3)));
		planetScales.put("Neptune", new LinkedList<>(Arrays.asList(5e2, 5e3)));
		planetScales.put("Pluto", new LinkedList<>(Arrays.asList(1e4, 1e4)));

		sun = new BodyInSpace("Sun", 1392530, 1.9891e30, 0.0, 0.0, 0.0, 1, null, SCREEN_SCALE);

		planetsList.put("Mercury", new BodyInSpace("Mercury", 4879, 3.302e23, 57.92e6, 58.65, 5.2, 1, sun, SCREEN_SCALE));
		planetsList.put("Venus", new BodyInSpace("Venus", 12104, 4.8689e24, 108.2e6, 224.7, 1.8, 1, sun, SCREEN_SCALE));
		planetsList.put("Earth", new BodyInSpace("Earth", 12756, 5.9742e24, 149.6e6, 365.2, 1.4, 1, sun, SCREEN_SCALE));
		planetsList.put("Mars", new BodyInSpace("Mars", 6792, 6.4191e23, 228.0e6, 687.0, 3.6, 1, sun, SCREEN_SCALE));
		planetsList.put("Jupiter", new BodyInSpace("Jupiter", 142984, 1.899e27, 779.1e6, 4333.0, 1.6, 1, sun, SCREEN_SCALE));
		planetsList.put("Saturn", new BodyInSpace("Saturn", 120536, 5.684e26, 1426.0e6, 10759.0, 4.5, 1, sun, SCREEN_SCALE));
		planetsList.put("Uranus", new BodyInSpace("Uranus", 51118, 8.698e25, 2870.0e6, 30685.0, 1.6, 1, sun, SCREEN_SCALE));
		planetsList.put("Neptune", new BodyInSpace("Neptune", 49528, 1.028e26, 4493.0e6, 60200.0, 2.4, 1, sun, SCREEN_SCALE));
		planetsList.put("Pluto", new BodyInSpace("Pluto", 2370, 1.1e22, 5898.0e6, 90465.0, 2.1, 1, sun, SCREEN_SCALE));

		earthChild.put("Moon", new BodyInSpace("Moon", 3475, 0.073e24, 0.384e6, 27.3, 1.3, 20, planetsList.get("Earth"), SCREEN_SCALE));

		marsChild.put("Deimos", new BodyInSpace("Deimos", 12.7, 2.4e15, 23459, 1.26244, 3.8, 100, planetsList.get("Mars"), SCREEN_SCALE));
		marsChild.put("Phobos", new BodyInSpace("Phobos", 22.6, 10.6e15, 9378, 0.31891, 0.4, 100, planetsList.get("Mars"), SCREEN_SCALE));

		jupiterChild.put("Io", new BodyInSpace("Io", 3643, 893.2e20, 421.6e3, 1.76, 1.3, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Europa", new BodyInSpace("Europa", 3122, 480e20, 670.9e3, 3.55, 0.6, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Ganymede", new BodyInSpace("Ganymede", 5262, 1481.9e20, 1070.4e3, 7.15, 5.7, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Callisto", new BodyInSpace("Callisto", 4821, 1075.9e20, 1882.7e3, 16.69, 2.4, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Adrastea", new BodyInSpace("Adrastea", 35, 2e16, 129e3, 0.3, 1.9, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Amalthea", new BodyInSpace("Amalthea", 250, 7.5e18, 181.4e3, 0.5, 3.8, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Metis", new BodyInSpace("Metis", 40, 1e17, 128e3, 0.29, 0.2, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Thebe", new BodyInSpace("Thebe", 75, 8e17, 221.9e3, 0.67, 4.3, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Leda", new BodyInSpace("Leda", 10, 6e15, 11170e3, 240.92, 4.7, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Himalia", new BodyInSpace("Himalia", 170, 9.5e18, 11460e3, 250.6, 3.1, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Lysithea", new BodyInSpace("Lysithea", 24, 8e16, 11720e3, 259.2, 2.8, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Elara", new BodyInSpace("Elara", 80, 8e17, 11740e3, 259.6, 0.1, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Ananke", new BodyInSpace("Ananke", 20, 4e16, 21280e3, 630, 5.1, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Carme", new BodyInSpace("Carme", 30, 1e17, 23400e3, 734.2, 1.2, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Pasiphae", new BodyInSpace("Pasiphae", 36, 3e17, 23620e3, 744, 0.8, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
		jupiterChild.put("Sinope", new BodyInSpace("Sinope", 28, 8e16, 23940e3, 759, 4.5, 20, planetsList.get("Jupiter"), SCREEN_SCALE));
	
		saturnChild.put("Mimas", new BodyInSpace("Mimas", 399, 0.379e20, 185.52e3, 0.942, 0.4, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Enceladus", new BodyInSpace("Enceladus", 505, 1.08e20, 238.02e3, 1.37, 3.4, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Tethys", new BodyInSpace("Tethys", 1066, 6.18e20, 294.66e3, 1.887, 5.8, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Dione", new BodyInSpace("Dione", 1124, 11.0e20, 377.04e3, 2.74, 1.4, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Rhea", new BodyInSpace("Rhea", 1526, 23.1e20, 527.04e3, 4.52, 2.9, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Titan", new BodyInSpace("Titan", 5150, 1345.5e20, 1221.83e3, 15.95, 2.1, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Hyperion", new BodyInSpace("Hyperion", 277, 0.056e20, 1481.1e3, 21.3, 3.8, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Iapetus", new BodyInSpace("Iapetus", 1442, 18.1e20, 3561.3e3, 79.33, 5.1, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Pan", new BodyInSpace("Pan", 26, 5e15, 133.583e3, 0.575, 1.1, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Daphnis", new BodyInSpace("Daphnis", 8, 1e14, 136.5e3, 0.594, 2.4, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Atlas", new BodyInSpace("Atlas", 31, 7e15, 137.6e3, 0.602, 3.8, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Prometheus", new BodyInSpace("Prometheus", 86, 1.6e17, 139.3e3, 0.613, 4.8, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Pandora", new BodyInSpace("Pandora", 80, 1.4e17, 141.7e3, 0.6285, 2.1, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Epimetheus", new BodyInSpace("Epimetheus", 120, 5.3e17, 151.422e3, 0.6942, 3.4, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Janus", new BodyInSpace("Janus", 181, 0.19e20, 151.472e3, 0.6945, 0.2, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Calypso", new BodyInSpace("Calypso", 25, 4e15, 294.66e3, 1.88, 5.6, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Telesto", new BodyInSpace("Telesto", 26, 7e15, 294.66e3, 1.88, 2.5, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Helene", new BodyInSpace("Helene", 40, 3e16, 377.4e3, 2.74, 5.2, 500, planetsList.get("Saturn"), SCREEN_SCALE));
		saturnChild.put("Phoebe", new BodyInSpace("Phoebe", 220, 8.3e18, 12944e3, 548, 1.2, 500, planetsList.get("Saturn"), SCREEN_SCALE));
	
		uranusChild.put("Miranda", new BodyInSpace("Miranda", 480, 0.66e20, 129.3e3, 1.41, 0.6, 500, planetsList.get("Uranus"), SCREEN_SCALE));
		uranusChild.put("Ariel", new BodyInSpace("Ariel", 1160, 13.5e20, 191.0e3, 2.52, 5.7, 500, planetsList.get("Uranus"), SCREEN_SCALE));
		uranusChild.put("Umbriel", new BodyInSpace("Umbriel", 1190, 11.7e20, 266.3e3, 4.14, 4.9, 500, planetsList.get("Uranus"), SCREEN_SCALE));
		uranusChild.put("Titania", new BodyInSpace("Titania", 1580, 35.2e20, 435.9e3, 8.7, 2.8, 500, planetsList.get("Uranus"), SCREEN_SCALE));
		uranusChild.put("Oberon", new BodyInSpace("Oberon", 1522, 30.1e20, 583.5e3, 13.46, 3.4, 500, planetsList.get("Uranus"), SCREEN_SCALE));
		uranusChild.put("Cordelia", new BodyInSpace("Cordelia", 40, 0, 49.7e3, 0.33, 4.5, 500, planetsList.get("Uranus"), SCREEN_SCALE));
		uranusChild.put("Ophelia", new BodyInSpace("Ophelia", 42, 0, 53.8e3, 0.37, 1.2, 500, planetsList.get("Uranus"), SCREEN_SCALE));
		uranusChild.put("Bianca", new BodyInSpace("Bianca", 52, 0, 59.1e3, 0.43, 0.1, 500, planetsList.get("Uranus"), SCREEN_SCALE));
		uranusChild.put("Cressida", new BodyInSpace("Cressida", 80, 0, 61.78e3, 0.46, 5.9, 500, planetsList.get("Uranus"), SCREEN_SCALE));
		uranusChild.put("Desdemona", new BodyInSpace("Desdemona", 64, 0, 62.68e3, 0.47, 3.4, 500, planetsList.get("Uranus"), SCREEN_SCALE));
		uranusChild.put("Juliet", new BodyInSpace("Juliet", 94, 0, 64.3e3, 0.49, 6.1, 500, planetsList.get("Uranus"), SCREEN_SCALE));
		
		neptuneChild.put("Naiad", new BodyInSpace("Naiad", 70, 2e17, 48.22e3, 0.29, 1.2, 500, planetsList.get("Neptune"), SCREEN_SCALE));
		neptuneChild.put("Thalassa", new BodyInSpace("Thalassa", 87, 4e17, 50.1e3, 0.31, 5.4, 500, planetsList.get("Neptune"), SCREEN_SCALE));
		neptuneChild.put("Despina", new BodyInSpace("Despina", 152, 2e18, 52.5e3, 0.33, 3.6, 500, planetsList.get("Neptune"), SCREEN_SCALE));
		neptuneChild.put("Galatea", new BodyInSpace("Galatea", 174, 4e18, 61.9e3, 0.42, 0.8, 500, planetsList.get("Neptune"), SCREEN_SCALE));
		neptuneChild.put("Larissa", new BodyInSpace("Larissa", 185, 5e18, 73.5e3, 0.55, 6.1, 500, planetsList.get("Neptune"), SCREEN_SCALE));
		neptuneChild.put("Proteus", new BodyInSpace("Proteus", 420, 5e19, 117.6e3, 1.12, 4.8, 500, planetsList.get("Neptune"), SCREEN_SCALE));
		neptuneChild.put("Triton", new BodyInSpace("Triton", 2706, 3e19, 354.7e3, 5.87, 3.1, 500, planetsList.get("Neptune"), SCREEN_SCALE));
		
		plutoChild.put("Charon", new BodyInSpace("Charon", 1212, 1.586e21, 19596, 6.39, 2.3, 500, planetsList.get("Pluto"), SCREEN_SCALE));
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
		else if (planet.equals("Saturn")) {
			return saturnChild;
		}
		else if (planet.equals("Neptune")) {
			return neptuneChild;
		}
		else if (planet.equals("Uranus")) {
			return uranusChild;
		}
		else if (planet.equals("Pluto")) {
			return plutoChild;
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
		combined.putAll(saturnChild);
		combined.putAll(neptuneChild);
		combined.putAll(uranusChild);
		combined.putAll(plutoChild);
		
		return combined.get(name);
	}

	public static List<Double> getScale(String name) {
		return planetScales.get(name);
	}
}
