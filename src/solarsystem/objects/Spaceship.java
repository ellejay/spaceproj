package solarsystem.objects;

import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;

public class Spaceship {
	
	private final Circle gui_object;
	private final Ellipse gui_trail;
	private double angle;
	private double radius_x;
	private double radius_y;
	private double center_x;
	private double center_y;
	private BodyInSpace parent;
	private double period;
	
	public Spaceship() {

		this.radius_x = 0;
		this.radius_y = 0;
		angle = 0;

		this.period = 100;

		this.gui_object = new Circle(0, 0, 3);
		this.gui_object.getStyleClass().add("spacecraft");
		
		this.gui_trail = new Ellipse(0, 0);
		this.gui_trail.getStyleClass().add("spacecraft-path");
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
	
	public void setPeriod(double period) {
		this.period = period;
	}

	public void setPathRotation(double rotate) { this.gui_trail.setRotate(rotate);}
	
	public void incrementAngle() {
		this.angle += Math.toRadians( (2 * Math.PI) / (this.period));
		
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