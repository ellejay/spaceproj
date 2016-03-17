package solarsystem.objects;

import javafx.scene.shape.Circle;

/**
 * BodyInSpace class used to represent a planet or moon and all associated properties
 * for both calculation and GUI purposes.
 *
 * @author Laura McGhie
 */

public class BodyInSpace {
	
	private final String name;
	private final double diameter;
	private final double mass;
	private Circle gui_object;
	private double pos_x;
	private double pos_y;
	private final double orbit;
	private final double period;
	private double angle;
	private final Circle gui_orbit;
	private final BodyInSpace center;
	
	/**
	 * Constructor method to create planet given all required data.
	 * 
	 * @param name Name of the body created
	 * @param diameter Diameter of the body in km
	 * @param mass Mass of the body in kg
	 * @param orbit Distance from the Body's parent in km
	 * @param period Number of days body takes to complete one full orbit
	 * @param angle Current angle of the body along its orbital path
	 * @param center Parent object of the body
	 */
	public BodyInSpace(String name, double diameter, double mass, double orbit, double period,
					   double angle, BodyInSpace center) {
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
		 * object is displayed on screen. */
		this.gui_orbit = new Circle(295, 295, orbit/1e6);
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
	 * @param factor - the distance to move the planet in this increment
	 */
	public void incrementAngle(double factor) {
		//
		this.angle += factor * Math.toRadians( (2 * Math.PI) / (this.period));
		
		if (this.angle >= (2 * Math.PI)) {
			this.angle -= 2 * Math.PI;
        }
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
	 * Check if the current body shares a parent with the parameter body, thus is a sibling
	 * @param other - BodyInSpace to compare this one to
	 * @return boolean to indicate if body is sibling of the parameter body
     */
	public boolean isSibling(BodyInSpace other) {
		return this.getParent().equals(other.getParent());
	}

	/**
	 * Check if the current body is the child of the parameter body
	 * @param other - BodyInSpace to compare this one to
	 * @return boolean to indicate if body is child of the parameter body
     */
	public boolean isChild(BodyInSpace other) {
		return this.getParent().equals(other);
	}

	/**
	 * Check if the current body is the parent of the parameter body
	 * @param other - BodyInSpace to compare this one to
	 * @return boolean to indicate if body is parent of the parameter body
     */
	public boolean isParent(BodyInSpace other) {
		return other.getParent().equals(this);
	}
}
