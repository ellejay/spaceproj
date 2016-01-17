package solarsystem.objects;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;

public class Spaceship {
	
	private Circle gui_object;
	private Ellipse gui_trail;
	private double angle;
	private double radius_x;
	private double radius_y;
	private double center_x;
	private double center_y;
	private BodyInSpace parent;
	private int rotation;
	
	public Spaceship(double radius_x, double radius_y) {
		
		this.gui_object = new Circle(0, 0, 3);
		this.gui_object.getStyleClass().add("spacecraft");
		
		this.gui_trail = new Ellipse(radius_x, radius_y);
		this.gui_trail.getStyleClass().add("planet-orbit-path");
		this.radius_x = radius_x;
		this.radius_y = radius_y;
		angle = 0;
		
		this.rotation = 1;
		
	}
	
	public Ellipse getGUITrail(){
		return this.gui_trail;
	}
	
	public Circle getGUIShip() {
		return this.gui_object;
	}
	
	public void setAngle(double angle) {
		this.angle = angle;
	}
	
	public void setRotation(int rotate) {
		this.rotation = rotate;
	}

	public int getRotation() {
		return this.rotation;
	}
	
	public void incrementAngle() {
		this.angle += this.rotation * Math.toRadians( (2 * Math.PI) / (10));
		
		if (this.angle >= (2 * Math.PI)) {
			this.angle -= 2 * Math.PI;
        }
		else if (this.angle < 0) {
			this.angle += 2 * Math.PI;
		}
	}
	
	public void setRadius(double radius_x, double radius_y) {
		this.radius_x = radius_x;
		this.radius_y = radius_y;
		this.gui_trail.setRadiusX(radius_x);
		this.gui_trail.setRadiusY(radius_y);
	}
	
	public double getRadiusX(){
		return this.radius_x;
	}
	
	public double getRadiusY(){
		return this.radius_y;
	}
	
	public double getAngle() {
		return this.angle;
	}
	
	public void setCenterPoint(double x, double y) {
		this.center_x = x;
		this.center_y = y;
		this.gui_trail.setCenterX(x);
		this.gui_trail.setCenterY(y);
	}
	
	public double getCenterX() {
		return this.center_x;
	}
	
	public double getCenterY() {
		return this.center_y;
	}
	
	public void setParent(BodyInSpace body) {
		this.parent = body;
	}
	
	public BodyInSpace getParent() {
		return this.parent;
	}
}
