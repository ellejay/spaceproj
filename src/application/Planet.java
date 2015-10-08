package application;

import javafx.scene.shape.Circle;

public class Planet {
	
	private String name;
	private double orbit;
	private double period;
	private double angle;
	private Circle gui_object;
	
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
	
	public void setGUIObject(Circle gui_object) {
		this.gui_object = gui_object;
		this.gui_object.getStyleClass().add(this.getStyle());
	}
	
	public Circle getGUIObject() {
		return this.gui_object;
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
