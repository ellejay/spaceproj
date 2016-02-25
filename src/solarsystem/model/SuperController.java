package solarsystem.model;

import java.util.ArrayList;
import java.util.Map;

import javafx.animation.Timeline;
import solarsystem.objects.BodyInSpace;
import solarsystem.objects.SpaceObjects;

class SuperController {
	
    // Planet scale factor
 	static double SCREEN_SCALE = 0.045;
 	static final double midPoint = 300;
 	final Map<String, BodyInSpace> planets = SpaceObjects.getDictionary();
 	Timeline timeline = null;
 	// Rotation speed
 	static final double STEP_DURATION = 2; //milliseconds
 	static final double[] orbitParams = new double[2];
 	static final ArrayList<String> routePlanets = new ArrayList<>();
 	static final ArrayList<double[]> routeOrbit = new ArrayList<>();

}
