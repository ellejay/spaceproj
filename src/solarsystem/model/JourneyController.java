package solarsystem.model;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import solarsystem.objects.BodyInSpace;
import solarsystem.objects.Spaceship;
import solarsystem.math.MathEllipse;
import solarsystem.model.SpaceObjects;

public class JourneyController extends SuperController implements Initializable {

	@FXML private Text actiontarget;
	@FXML private Pane systemPane;
	@FXML private Slider zoomSlide;
	@FXML private Button switchScene;
	@FXML private Label routeData;
	private int steps;
	private boolean newStep;
	private int rotateCount = 0;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		setUp();

		zoomSlide.setValue(SCREEN_SCALE);
		zoomSlide.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				SCREEN_SCALE = (double) new_val;
				for (BodyInSpace current: planets.values()) {        	
					current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
				}
			}
		});

	}


	public void setUp() {

		planets = SpaceObjects.getDictionary();

		for (BodyInSpace current: planets.values()) {  
			current.resetPlanet();
		}

		EventHandler<ActionEvent> planetMovement = new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {

				for (BodyInSpace current: planets.values()) {

					current.incrementAngle();

					// p(x) = x(0) + r * sin(a)
					// p(y) = y(y) - r * cos(a)

					double moveX = current.getParent().getX() + (current.getOrbit() * SCREEN_SCALE) * 
							Math.sin(current.getAngle());

					double moveY = current.getParent().getY() - (current.getOrbit() * SCREEN_SCALE) * 
							Math.cos(current.getAngle());

					current.setPosition(moveX, moveY);
					current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);

					moveBall(current.getGUIObject(), moveX, moveY);

					//current.moveGUIObject(moveX, moveY);

				}

			}
		};


		BodyInSpace sun = SpaceObjects.getSun();
		sun.moveGUIObject(midPoint, midPoint);

		Spaceship enterprise = new Spaceship(0,0);

		steps = 0;

		Ellipse route = new Ellipse();
		route.getStyleClass().add("planet-orbit-path");
		
		EventHandler<ActionEvent> spaceshipMove = new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {

				int planetIndex = steps / 2;
				int movement = 1;

				String phaseStart = routePlanets.get(planetIndex);
				double[] startOrbit = routeOrbit.get(planetIndex);

				BodyInSpace startPlanet, endPlanet;
				String phaseEnd;
				double[] endOrbit = null;

				startPlanet = planets.get(phaseStart);

				try {
					phaseEnd = routePlanets.get(planetIndex + 1);
					endOrbit = routeOrbit.get(planetIndex + 1);
					
					if (endOrbit[0] < startOrbit[0]) {
						movement = -1;
					}
					else {
						movement = 1;
					}
				}
				catch (IndexOutOfBoundsException e) {
					phaseEnd = "";
				}

				double moveX, moveY = 0;

				int direction = 1;

				if (steps % 2 == 1) {
					endPlanet = planets.get(phaseEnd);
					MathEllipse e1 = new MathEllipse(startPlanet.getMass(), startPlanet.getRadius());

					//System.out.println(e1.semi_major() + " " + e1.semi_minor());

					//route.setRadiusX(e1.semi_major()/1e5 * SCREEN_SCALE);
					//route.setRadiusY(e1.semi_minor()/1e5 * SCREEN_SCALE);
					route.setRadiusX(Math.abs(endOrbit[0] - startOrbit[0]) / 2 * SCREEN_SCALE);
					route.setRadiusY(Math.abs(endOrbit[1] - startOrbit[1]) / 2 * SCREEN_SCALE);
					
					double transferRadius = (endOrbit[0] / 2 - startOrbit[0] / 2);
					
					route.setCenterX(startPlanet.getX());
					route.setCenterY(startPlanet.getY() + (startOrbit[1] + transferRadius) * SCREEN_SCALE);

					routeData.setText(route.getRadiusX() + " " + route.getRadiusY() + " " + route.getCenterY() + " " + route.getCenterX());

					enterprise.setParent(startPlanet);
					
					enterprise.setRadius(route.getRadiusX() / SCREEN_SCALE, route.getRadiusY() / SCREEN_SCALE);
					
					
					if (newStep) {
						
						if (movement == -1) {
							enterprise.setAngle(4.72);
							direction = -1;
						}
						else {
							enterprise.setAngle(1.57);
							
							if ((steps / 2) % 2 == 1) {
								enterprise.setRotation(direction * -1);
							}
							else {
								enterprise.setRotation(direction * 1);
							}
							
						}
						
						System.out.println(enterprise.getRotation());
						
						newStep = false;
						rotateCount = 0;
					}
					else {
						enterprise.incrementAngle();
					}

					enterprise.setCenterPoint(startPlanet.getX(), startPlanet.getY() + (startOrbit[1] + transferRadius) * SCREEN_SCALE);
					rotateCount++;
					if (rotateCount == 285) {
						timeline.pause();
						//steps++;
					}
				}

				else {				
					
					if (movement == -1) {
						
						/*direction = 1;
						int inc = 2;
						
						if (endOrbit[0] < startOrbit[0]) {
							direction = -1;
							inc = 1;
							System.out.println("in");
						}*/
						
						if ((steps / 2) % 2 == 1) {
							enterprise.setRotation(direction * 1);
						}
						else {
							enterprise.setRotation(direction * -1);
						}
						
						
					}
					
					if (newStep) {
						newStep = false;
						System.out.println(enterprise.getRotation());
						
						if (movement == -1) {
							enterprise.setAngle(4.72);
						}
					}

					
					enterprise.setRadius(startOrbit[0], startOrbit[1]);
					enterprise.incrementAngle();
					enterprise.setCenterPoint(startPlanet.getX(), startPlanet.getY());

				}

				moveX = enterprise.getCenterX() + (enterprise.getRadiusX() * SCREEN_SCALE) * 
						Math.cos(enterprise.getAngle());

				moveY = enterprise.getCenterY() - (enterprise.getRadiusY() * SCREEN_SCALE) * 
						Math.sin(enterprise.getAngle());

				enterprise.setRadius(enterprise.getRadiusX() * SCREEN_SCALE, enterprise.getRadiusY() * SCREEN_SCALE);

				moveBall(enterprise.getGUIShip(), moveX, moveY);

			}
		};
		
		//

		timeline = new Timeline( new KeyFrame(Duration.ZERO, planetMovement),
				new KeyFrame(Duration.ZERO, spaceshipMove),
				new KeyFrame(Duration.millis(STEP_DURATION)));

		timeline.setCycleCount(Timeline.INDEFINITE);

		systemPane.getChildren().add(sun.getGUIObject());

		for (BodyInSpace current: planets.values()) {  
			current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
			systemPane.getChildren().add(current.getGUIOrbit());
			systemPane.getChildren().add(current.getGUIObject());
		}

		systemPane.getChildren().add(enterprise.getGUIShip());
		systemPane.getChildren().add(enterprise.getGUITrail());

		//systemPane.getChildren().add(route);

		timeline.play();

	}


	private void moveBall(Shape ball, double x, double y) {
		TranslateTransition move = new TranslateTransition(
				Duration.millis(STEP_DURATION), ball);
		move.setToX(x);
		move.setToY(y);
		move.playFromStart();
	}

	@FXML protected void nextPhase(ActionEvent event) throws IOException { 
		if (steps / 2 < routePlanets.size() - 1) {
			steps++;
			newStep = true;
			timeline.play();
		}
		else {
			System.out.println("journey complete");
		}
	}


	@FXML protected void stopTimeline(ActionEvent event) throws IOException { 
		timeline.pause();

		Stage stage; 
		Parent root;
		stage=(Stage) switchScene.getScene().getWindow();

		routePlanets.clear();
		routeOrbit.clear();

		root = FXMLLoader.load(getClass().getResource("pathselect.fxml"));
		Scene scene = new Scene(root, 650, 650);

		stage.setScene(scene);
		stage.show();
	}

}
