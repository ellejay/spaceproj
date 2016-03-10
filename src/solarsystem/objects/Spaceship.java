package solarsystem.objects;

import javafx.scene.shape.Ellipse;

/**
 * Spaceship class used to represent a spacecraft and all associated properties
 * for GUI display purposes.
 *
 * @author 2028023m
 */
public class Spaceship {
	
	private final Ellipse gui_object;
	private final Ellipse gui_trail;
	private double angle;
	private double radius_x;
	private double radius_y;
	private double center_x;
	private double center_y;
	private BodyInSpace parent;
	private double period;

	/**
	 * Default constructor to make a spaceship object for the GUI
	 */
	public Spaceship() {

		/* Used to store the radius of the current path of the spacecraft. Always starts from
		 * a landed position, which is a fixed position on a body. Thus we can set the radius
		 * and current angle around the path to 0 on construction.
		 */
		this.radius_x = 0;
		this.radius_y = 0;
		angle = 0;

		/* Give the spaceship an arbitrary period of 30 days to complete 1 orbit. This isn't really accurate
		 * but it used for display purposes, as a true period would be too fast to see properly*/
		this.period = 30;

		/* Create an object to represent the spacecraft itself, add at origin point of
		* canvas (top left) for calculation purposes and use the spacecraft css style to colour this. */
		this.gui_object = new Ellipse(0, 0, 3, 5);
		this.gui_object.getStyleClass().add("spacecraft");

		/* Establish GUI object for the orbit path of the spacecraft, centred at the
		 * origin point of the canvas (top left) for calculation purposes. Add style
		 * class so only border of the object is displayed on screen. */
		this.gui_trail = new Ellipse(0, 0);
		this.gui_trail.getStyleClass().add("spacecraft-path");
	}

	/**
	 * Get the GUI object which represents the orbit path of the spacecraft
	 * @return Ellipse for spacecraft orbit
     */
	public Ellipse getGUITrail(){
		return this.gui_trail;
	}

	/**
	 * Get the GUI object which represents the spacecraft itself
	 * @return Ellipse representing spacecraft
     */
	public Ellipse getGUIShip() { return this.gui_object; }

	/**
	 * Set the rotational period of the spacecraft to the provided parameter
	 * @param period number of days to complete a single orbit
     */
	public void setPeriod(double period) { this.period = period; }

	/**
	 * Set the angle of the orbit to the given parameter, so the x and y axises of the ellipse are not
	 * horizontal and vertical
	 * @param rotate the angle to rotate the path by
     */
	public void setPathRotation(double rotate) { this.gui_trail.setRotate(rotate);}

	/**
	 * Retrieve the current angle of rotation for the orbital path of the spacecraft.
	 * @return angle of rotation in Radians
     */
	public double getPathRotation() { return Math.toRadians(this.gui_trail.getRotate()); }

	/**
	 * Set the current angle of the spacecraft around its orbit to the parameter value
	 * @param angle angle around the orbit path in Radians
     */
	public void setAngle(double angle) {
		this.angle = angle;
	}

	/**
	 * Return the current angle of the spacecraft around its orbit
	 * @return current angle in Radians
     */
	public double getAngle() {
		return this.angle;
	}

	/**
	 * Increment the current angle of the body by a number of degrees.
	 * This number is calculated from the period of the object so that
	 * speeds of planet objects are relative in the GUI animation.
	 * @param factor - the distance to move the planet in this increment
	 */
	public void incrementAngle(double factor) {
		this.angle += factor * Math.toRadians( (2 * Math.PI) / (this.period));
		
		if (this.angle >= (2 * Math.PI)) {
			this.angle -= 2 * Math.PI;
        }
	}

	/**
	 * Set the radius of the path the spacecraft is currently on to the given parameters. Also adjusts
	 * the GUI path object to match this new radius
	 * @param radius_x the radius of the orbit along the x-axis
	 * @param radius_y the radius of the orbit along the y-axis
     */
	public void setRadius(double radius_x, double radius_y) {
		this.radius_x = radius_x;
		this.radius_y = radius_y;
		this.gui_trail.setRadiusX(radius_x);
		this.gui_trail.setRadiusY(radius_y);
	}

	/**
	 * Get the current radius of the orbital path of the spacecraft along the x-axis
	 * @return radial distance in x
     */
	public double getRadiusX(){
		return this.radius_x;
	}

	/**
	 * Get the current radius of the orbital path of the spacecraft along the y-axis
	 * @return radial distance in y
     */
	public double getRadiusY(){
		return this.radius_y;
	}

	/**
	 * Set the center point of the current orbital path of the spacecraft to the given point
	 * @param x the new centre point of the spacecraft's path on the x-axis
	 * @param y the new centre point of the spacecraft's path on the y-axis
     */
	public void setCenterPoint(double x, double y) {
		this.center_x = x;
		this.center_y = y;
		this.gui_trail.setCenterX(x);
		this.gui_trail.setCenterY(y);
	}

	/**
	 * Get the current center point of the spacecraft's path along the x-axis
	 * @return center point in x
     */
	public double getCenterX() {
		return this.center_x;
	}

	/**
	 * Get the current center point of the spacecraft's path along the y-axis
	 * @return center point in y
	 */
	public double getCenterY() {
		return this.center_y;
	}

	/**
	 * Set the parent body of the spacecraft, which is the body it is currently in orbit around.
	 * @param body BodyInSpace that the spacecraft is orbiting
     */
	public void setParent(BodyInSpace body) {
		this.parent = body;
	}

	/**
	 * Get the current parent of the spacecraft, i.e. the body it is currently orbiting
	 * @return BodyInSpace parent object
     */
	public BodyInSpace getParent() {
		return this.parent;
	}
}