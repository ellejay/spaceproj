package solarsystem.objects;

import javafx.scene.shape.Circle;

/**
 * BodyInSpace class used to represent a planet or moon and all associated properties
 * for both calculation and GUI purposes.
 * 
 * @author 2028023m
 */

public class BodyInSpace {
	
	public String name; // body name
	public double diameter; // diameter of body in 
	public double mass; // mass of body in
	public Circle gui_object; // GUI object to represent body
	public double pos_x; // current position of GUI body
	public double pos_y; // current position of GUI body
	private double orbit; // radius of orbit from body's parent in km
	private double period; // time taken for one full rotation around body orbit in days
	private double angle; // current angle of body around circle, measured from vertical in radians
	private Circle gui_orbit; // GUI object for the orbit path
	private BodyInSpace center; // parent object of body (eg. sun to the earth)
	
	/**
	 * Constructor method to create planet given all required data.
	 * 
	 * @param name
	 * @param diameter
	 * @param mass
	 * @param orbit
	 * @param period
	 * @param angle
	 * @param center
	 * @param orbit_scale
	 */
	public BodyInSpace(String name, double diameter, double mass, double orbit, double period, 
			double angle, BodyInSpace center, double orbit_scale) {
		// Assign parameters to specified properties
		this.name = name;
		this.diameter = diameter;
		this.mass = mass;
		this.orbit = orbit;
		this.period = period;
		this.angle = angle;
		this.center = center;

		/* Create default body object, add at origin point of 
		* canvas (top left) for calculation purposes, and add css style to object. */
		this.gui_object = new Circle(0, 0, 4);
		this.gui_object.getStyleClass().add(this.getStyle());
		
		/* Establish GUI object for the orbit of the planet, centred at the
		 * the middle of the window. Add style class so only border of the 
		 * object is displayed on screen.
		 * 
		 */
		this.gui_orbit = new Circle(295, 295, orbit/1e6 * orbit_scale);
		this.gui_orbit.getStyleClass().add("planet-orbit-path");
	}

	/**
	 * Resets the body object by replacing it with a new object 
	 * centred at the origin, and adding styles. Necessary when 
	 * moving to new scene due to displacement of existing objects.
	 */
	public void resetPlanet() {
		this.gui_object = new Circle(0, 0, 3);
		this.gui_object.getStyleClass().add(this.getStyle());
	}
	
	/**
	 * Set current position of body.
	 * @param x
	 * @param y
	 */
	public void setPosition(double x, double y) {
		pos_x = x;
		pos_y = y;
	}
	
	/**
	 * Increment the current angle of the body by a number of degrees.
	 * This number is calculated from the period of the object so that
	 * speeds of planet objects are relative in the GUI animation.
	 */
	public void incrementAngle() {
		//
		this.angle += Math.toRadians( (2 * Math.PI) / (this.period / 10) );
		
		if (this.angle >= (2 * Math.PI)) {
			this.angle -= 2 * Math.PI;
        }
	}
	
	/**
	 * Reposition the GUI orbit object with the given radius, and
	 * re-centre it given the position of the parent body.
	 * @param radius
	 */
	public void adjustGUIOrbit(double radius) {
		this.gui_orbit.setRadius(radius);
		this.gui_orbit.setCenterX(this.center.getX());
		this.gui_orbit.setCenterY(this.center.getY());
	}
	
	/**
	 * Reposition the GUI orbit object with the given radius, and
	 * establish a new parent body for the current body. Orbit will 
	 * then centre around this body.
	 * 
	 * @param radius
	 * @param center
	 */
	public void adjustGUIOrbit(double radius, BodyInSpace center) {
		this.center = center;
		this.gui_orbit.setRadius(radius);
		this.gui_orbit.setCenterX(this.center.getX());
		this.gui_orbit.setCenterY(this.center.getY());
	}
	
	/**
	 * Readjusts planet for a new center point, by relocating GUI 
	 * object and updating the position x & y attributes of the object.
	 * 
	 * @param point_x
	 * @param point_y
	 */
	public void moveGUIObject(double point_x, double point_y){
		this.gui_object.setCenterX(point_x);
		this.gui_object.setCenterY(point_y);
		
		this.pos_x = point_x;
		this.pos_y = point_y;
	}
	
	/**
	 * Get name of body
	 * @return
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get diameter of body
	 * @return
	 */
	public double getDiameter() {
		return this.diameter;
	}
	
	/**
	 * Get radius of body
	 * @return
	 */
	public double getRadius() {
		return this.diameter * 500;
	}
	
	/**
	 * Get mass of body
	 * @return
	 */
	public double getMass() {
		return this.mass;
	}
	
	/**
	 * Get GUI object which represents the body
	 * @return
	 */
	public Circle getGUIObject() {
		return this.gui_object;
	}
	
	/** 
	 * Get x co-ordinate of body's current position
	 * @return
	 */
	public double getX() {
		return pos_x;
	}
	
	/** 
	 * Get y co-ordinate of body's current position
	 * @return
	 */
	public double getY() {
		return pos_y;
	}
	
	/**
	 * Get GUI object which represents orbital path of body
	 * @return
	 */
	public Circle getGUIOrbit() {
		return this.gui_orbit;
	}
	
	/**
	 * Get the name of the css style to be applied to this body.
	 * @return
	 */
	public String getStyle() {
		return "body-" + this.name;
	}
	
	/**
	 * Get orbital radius of body from its parent
	 * @return
	 */
	public double getOrbit() {
		return this.orbit / 1e6;
	}
	
	/**
	 * Get orbital radius of the body from its parent in metres
	 * @return
	 */
	public double getOrbitInM() {
		return this.orbit * 1000;
	}
	
	/**
	 * Get orbital period of the body in days
	 * @return
	 */
	public double getPeriod() {
		return this.period;
	}
	
	/**
	 * Get orbital period of the body in seconds
	 * @return
	 */
	public double getPeriodAsSeconds() {
		return this.period * 24 * 60 * 60;
	}
	
	/**
	 * Get the angular velocity of the body
	 * @return
	 */
	public double getAngularV() {
		return 360 / this.period;
	}
	
	/**
	 * Get current position angle of the body
	 * @return
	 */
	public double getAngle() {
		return this.angle;
	}
	
	/** 
	 * Get parent body of the current body
	 * @return
	 */
	public BodyInSpace getParent() {
		return this.center;
	}
}
