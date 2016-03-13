package solarsystem.controller;

import java.util.ArrayList;

/**
 * Static super controller class with variables shared across multiple screens of the program.
 * @author Laura McGhie
 */

class SuperController {

 	static double SCREEN_SCALE;
 	static final double STEP_DURATION = 2; //milliseconds
	static double SPEED_FACTOR = 1;
 	static final ArrayList<String> routePlanets = new ArrayList<>();
 	static final ArrayList<double[]> routeOrbit = new ArrayList<>();

}
