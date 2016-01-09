package solarsystem.model;

import java.util.ArrayList;
import java.util.Map;

import javafx.animation.Timeline;
import solarsystem.objects.BodyInSpace;

public class SuperController {
	
    // Planet scale factor
 	protected static double SCREEN_SCALE = 0.7075;
 	protected static double midPoint = 295;
 	protected Map<String, BodyInSpace> planets;
 	protected Timeline timeline = null;
 	// Rotation speed
 	protected static double STEP_DURATION = 2; //milliseconds
 	protected static double[] orbitParams = new double[2];
 	protected static ArrayList<String> routePlanets = new ArrayList<String>();
 	protected static ArrayList<double[]> routeOrbit = new ArrayList<double[]>();

}
