package solarsystem.model;

import java.util.ArrayList;
import javafx.animation.Timeline;

class SuperController {
	
    // Planet scale factor
 	static double SCREEN_SCALE = 0.045;
 	static final double midPoint = 300;
 	Timeline timeline = null;
 	// Rotation speed
 	static final double STEP_DURATION = 2; //milliseconds
 	static final ArrayList<String> routePlanets = new ArrayList<>();
 	static final ArrayList<double[]> routeOrbit = new ArrayList<>();

}
