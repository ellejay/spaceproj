package solarsystem.model;

import javafx.animation.Timeline;
import solarsystem.objects.BodyInSpace;

public class SuperController {
	
    // Planet scale factor
 	protected static double SCREEN_SCALE = 0.7075;
 	protected static double midPoint = 295;
 	protected BodyInSpace[] planets;
 	protected Timeline timeline = null;
 	// Rotation speed
 	protected static double STEP_DURATION = 2; //milliseconds
 	protected static double[] orbitParams = new double[2];

}
