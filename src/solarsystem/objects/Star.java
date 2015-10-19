package solarsystem.objects;

import javafx.scene.shape.Circle;

public class Star extends BodyInSpace {
	
	public Star(String name, double diameter, double mass, Circle gui_obj) {
		super(name, diameter, mass);
		this.gui_object = gui_obj;
	}
	
}
