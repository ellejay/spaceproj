package application;

import javafx.scene.shape.Circle;

public class Star {
	
	private String name;
	private Circle gui_star;
	
	public Star(String name) {
		this.name = name;
	}
	
	public void setGUIStar(Circle star) {
		this.gui_star = star;
	}
	
	public Circle getGUIStar() {
		return this.gui_star;
	}

}
