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
			double angle, BodyInSpace center) {
		this.name = name;
		this.diameter = diameter;
		this.mass = mass;
		this.orbit = orbit;
		this.period = period;
		this.angle = angle;
		this.center = center;
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
	
	public void incrementAngle() {
		this.angle += Math.toRadians( (2 * Math.PI) / (this.period / 10));
		
		if (this.angle >= (2 * Math.PI)) {
			this.angle -= 2 * Math.PI;
        }
	}
	
	public void setGUIOrbit(Circle gui_orbit) {
		this.gui_orbit = gui_orbit;
		this.gui_orbit.getStyleClass().add("planet-orbit-path");
	}
	
	public Circle getGUIOrbit() {
		return this.gui_orbit;
	}
	
	public void setGUIPlanet(Circle gui_planet) {
		this.gui_object = gui_planet;
		this.gui_object.getStyleClass().add(this.getStyle());
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
	
}
