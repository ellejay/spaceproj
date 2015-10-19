package solarsystem.objects;

import javafx.scene.shape.Circle;

public class BodyInSpace {
	
	public String name;
	public double diameter;
	public double mass;
	public Circle gui_object;
	public double pos_x;
	public double pos_y;
	
	public BodyInSpace(String name, double diameter, double mass) {
		this.name = name;
		this.diameter = diameter;
		this.mass = mass;
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
	
}
