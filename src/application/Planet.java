package application;

import javafx.scene.shape.Circle;

public class Planet extends BodyInSpace {

	private double orbit; // 10e6 km
	private double period; // days
	private double angle; // radians
	private Circle gui_orbit;
	private Star center;
	
	public Planet(String name, double orbit, double period, double angle, Star center) {
		super(name, orbit, period);
		this.orbit = orbit;
		this.period = period;
		this.angle = angle;
		this.center = center;
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
		return "planet-" + this.name;
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
