package solarsystem.objects;

import javafx.scene.shape.Circle;

/**
 * BodyInSpace class used to represent a planet or moon and all associated properties
 * for both calculation and GUI purposes.
 * 
 * @author 2028023m
 */

public class BodyInSpace {
	
	private final String name; // body name
	private final double diameter; // diameter of body in
	private final double mass; // mass of body in
	private Circle gui_object; // GUI object to represent body
	private double pos_x; // current position of GUI body
	private double pos_y; // current position of GUI body
	private final double orbit; // radius of orbit from body's parent in km
	private final double period; // time taken for one full rotation around body orbit in days
	private double angle; // current angle of body around circle, measured from vertical in radians
	private final Circle gui_orbit; // GUI object for the orbit path
	private final BodyInSpace center; // parent object of body (eg. sun to the earth)
	private final double factor; // rotation factor to help control movement speeds
	
	/**
	 * Constructor method to create planet given all required data.
	 * 
	 * @param name Name of the body created
	 * @param diameter Diameter of the body in km
	 * @param mass Mass of the body in kg
	 * @param orbit Radius of the orbital distance from the Body's parent in km
	 * @param period Number of days body takes to complete one full orbit
	 * @param angle Current angle of the body along its orbital path
	 * @param center Parent object of the body
	 * @param orbit_scale Current scale displayed on the GUI
	 */
	public BodyInSpace(String name, double diameter, double mass, double orbit, double period, 
			double angle, double factor, BodyInSpace center, double orbit_scale) {
		// Assign parameters to specified properties
		this.name = name;
		this.diameter = diameter;
		this.mass = mass;
		this.orbit = orbit;
		this.period = period;
		this.angle = angle;
		this.center = center;
		this.factor = factor;

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
		this.gui_object = new Circle(0, 0, 4);
		this.gui_object.getStyleClass().add(this.getStyle());
	}
	
	/**
	 * Set current position of body.
	 * @param x co-ordinate of the body
	 * @param y co-ordinate of the body
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
		this.angle += Math.toRadians( (2 * Math.PI) / (this.period * this.factor / 10) );
		
		if (this.angle >= (2 * Math.PI)) {
			this.angle -= 2 * Math.PI;
        }
	}

	public double angleIncrease(){
		return Math.toRadians( (2 * Math.PI) / (this.period * this.factor / 10) );
	}
	
	/**
	 * Reposition the GUI orbit object with the given radius, and
	 * re-centre it given the position of the parent body.
	 * @param radius orbital distance from parent body in km
	 */
	public void adjustGUIOrbit(double radius) {
		this.gui_orbit.setRadius(radius);
		this.gui_orbit.setCenterX(this.center.getX());
		this.gui_orbit.setCenterY(this.center.getY());
	}
	
	/**
	 * Readjusts planet for a new center point, by relocating GUI 
	 * object and updating the position x & y attributes of the object.
	 * 
	 * @param point_x new x co-ordinate of planet
	 * @param point_y new y co-ordinate of planet
	 */
	public void moveGUIObject(double point_x, double point_y){
		this.gui_object.setCenterX(point_x);
		this.gui_object.setCenterY(point_y);
		
		this.pos_x = point_x;
		this.pos_y = point_y;
	}

	/**
	 * Get name of body
	 * @return name of body
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * Get diameter of body
	 * @return diameter of body
	 */
	public double getDiameter() {
		return this.diameter;
	}
	
	/**
	 * Get radius of body
	 * @return radius of body in km
	 */
	public double getRadius() {
		return this.diameter * 500;
	}
	
	/**
	 * Get mass of body
	 * @return mass of body in kg
	 */
	public double getMass() {
		return this.mass;
	}
	
	/**
	 * Get GUI object which represents the body
	 * @return GUI object to represent planet
	 */
	public Circle getGUIObject() {
		return this.gui_object;
	}
	
	/** 
	 * Get x co-ordinate of body's current position
	 * @return x co-ordinate
	 */
	public double getX() {
		return pos_x;
	}
	
	/** 
	 * Get y co-ordinate of body's current position
	 * @return y co-ordinate
	 */
	public double getY() {
		return pos_y;
	}
	
	/**
	 * Get GUI object which represents orbital path of body
	 * @return gui object to display orbital path
	 */
	public Circle getGUIOrbit() {
		return this.gui_orbit;
	}
	
	/**
	 * Get the name of the css style to be applied to this body.
	 * @return name of the css class
	 */
	private String getStyle() {
		return "body-" + this.name;
	}
	
	/**
	 * Get orbital radius of body from its parent
	 * @return orbital distance from parent in 1e6km
	 */
	public double getOrbit() {
		return this.orbit / 1e6;
	}
	
	/**
	 * Get orbital radius of the body from its parent in metres
	 * @return orbital distance from parent in m
	 */
	public double getOrbitInM() {
		return this.orbit * 1000;
	}
	
	/**
	 * Get orbital period of the body in days
	 * @return days taken to complete orbit
	 */
	public double getPeriod() {
		return this.period;
	}
	
	/**
	 * Get orbital period of the body in seconds
	 * @return seconds take to complete orbit
	 */
	public double getPeriodAsSeconds() {
		return this.period * 24 * 60 * 60;
	}
	
	/**
	 * Get the angular velocity of the body
	 * @return angular velocity of planet
	 */
	public double getAngularV() {
		return 360 / this.period;
	}
	
	/**
	 * Get current position angle of the body
	 * @return current angle
	 */
	public double getAngle() {
		return this.angle;
	}
	
	/** 
	 * Get parent body of the current body
	 * @return parent body
	 */
	public BodyInSpace getParent() {
		return this.center;
	}

	/**
	 *
	 * @param other
	 * @return
     */
	public boolean isSibling(BodyInSpace other) {
		return this.getParent().equals(other.getParent());
	}

	/**
	 *
	 * @param other
	 * @return
     */
	public boolean isChild(BodyInSpace other) {
		return this.getParent().equals(other);
	}

	/**
	 *
	 * @param other
	 * @return
     */
	public boolean isParent(BodyInSpace other) {
		return other.getParent().equals(this);
	}
}
