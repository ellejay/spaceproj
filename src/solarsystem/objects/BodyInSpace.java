package solarsystem.objects;

import javafx.scene.shape.Circle;

public class BodyInSpace {
	
	public String name;
	public double diameter;
	public double mass;
	public Circle gui_object;
	public double pos_x;
	public double pos_y;
	private double orbit; // 10e6 km
	private double period; // days
	private double angle; // radians
	private Circle gui_orbit;
	private BodyInSpace center;
	
	public BodyInSpace(String name, double orbit, double period, 
			double angle, BodyInSpace center, double orbit_scale) {
		this.name = name;
		this.diameter = diameter;
		this.mass = mass;
		this.orbit = orbit;
		this.period = period;
		this.angle = angle;
		this.center = center;

		this.gui_object = new Circle(0, 0, 3);
		this.gui_object.getStyleClass().add(this.getStyle());
		
		this.gui_orbit = new Circle(295, 295, orbit * orbit_scale);
		this.gui_orbit.getStyleClass().add("planet-orbit-path");
	}

	public void resetPlanet() {
		this.gui_object = new Circle(0, 0, 3);
		this.gui_object.getStyleClass().add(this.getStyle());
	}
	
	public String getName() {
		return this.name;
	}
	
	public double getDiameter() {
		return this.diameter;
	}
	
	public double getMass() {
		return this.mass;
	}
	
	public Circle getGUIObject() {
		return this.gui_object;
	}
	
	public double getX() {
		return pos_x;
	}
	
	public double getY() {
		return pos_y;
	}
	
	public void setPosition(double x, double y) {
		pos_x = x;
		pos_y = y;
	}
	
	public void incrementAngle() {
		this.angle += Math.toRadians( (2 * Math.PI) / (this.period / 10));
		
		if (this.angle >= (2 * Math.PI)) {
			this.angle -= 2 * Math.PI;
        }
	}
	
	public void adjustGUIOrbit(double radius) {
		this.gui_orbit.setRadius(radius);
		this.gui_orbit.setCenterX(this.center.getX());
		this.gui_orbit.setCenterY(this.center.getY());
	}
	
	public void moveGUIObject(double point_x, double point_y){
		this.gui_object.setCenterX(point_x);
		this.gui_object.setCenterY(point_y);
		
		this.pos_x = point_x;
		this.pos_y = point_y;
	}
	
	public Circle getGUIOrbit() {
		return this.gui_orbit;
	}
	
	public String getStyle() {
		return "body-" + this.name;
	}
	
	public double getOrbit() {
		return this.orbit;
	}
	
	public double getPeriod() {
		return this.period;
	}
	
	public double getAngle() {
		return this.angle;
	}
	
	public BodyInSpace getParent() {
		return this.center;
	}
}
