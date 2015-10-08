package application;

import javafx.scene.shape.Circle;

public class Planet {
	
	private String name;
	private double orbit;
	private double period;
	private double angle;
	private Circle gui_planet;
	private Circle gui_orbit;
	
	public Planet(String name, double orbit, double period, double angle) {
		this.name = name;
		this.orbit = orbit;
		this.period = period;
		this.angle = angle;
	}
	
	public void incrementAngle() {
		this.angle += Math.toRadians( (2 * Math.PI) / this.period );
		
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
		this.gui_planet = gui_planet;
		this.gui_planet.getStyleClass().add(this.getStyle());
	}
	
	public Circle getGUIPlanet() {
		return this.gui_planet;
	}
	
	public String getStyle() {
		return "planet-" + this.name;
	}

	public String getName() {
		return this.name;
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
