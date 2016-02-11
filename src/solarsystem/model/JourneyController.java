package solarsystem.model;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import solarsystem.objects.BodyInSpace;
import solarsystem.objects.SpaceObjects;
import solarsystem.objects.Spaceship;
import solarsystem.math.Calculator;
import solarsystem.math.MathEllipse;

public class JourneyController extends SuperController implements Initializable {

	@FXML private Text actiontarget;
	@FXML private Pane systemPane;
	@FXML private Pane sourcePane;
	@FXML private Pane destPane;
	@FXML private Button switchScene;
	@FXML private Text routeData;
    @FXML private Pane help;
	private int steps;
	private boolean newStep = true;
	private int rotateCount = 0;
	private boolean transferWindow = false;
	private double journeyMoveX = 0, journeyMoveY = 0;
	private double movementAngle, endAngle, transAngle;

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		setUp();
	}


	public void setUp() {

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

		final Spaceship enterprise = new Spaceship(0,0);

		steps = 0;

		final Ellipse route = new Ellipse();
		route.getStyleClass().add("planet-orbit-path");
		final Calculator calc = new Calculator(planets.get(routePlanets.get(0)));

		resetScale(285/planets.get(routePlanets.get(0)).getOrbit());

		EventHandler<ActionEvent> spaceshipMove = new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {

				int planetIndex = steps / 2;
				int movement = 1;
				
				BodyInSpace startPlanet, endPlanet = null;
				String phaseStart = routePlanets.get(planetIndex);
				double[] startOrbit = routeOrbit.get(planetIndex);
				String phaseEnd;
				double[] endOrbit = null;

				startPlanet = planets.get(phaseStart);

				try {
					phaseEnd = routePlanets.get(planetIndex + 1);
					endOrbit = routeOrbit.get(planetIndex + 1);

					endPlanet = planets.get(phaseEnd);
					
					if (startPlanet.getOrbit() > endPlanet.getOrbit()) {
						movement = -1;
						resetScale(285 / startPlanet.getOrbit());
					}
					else {
						movement = 1;
						resetScale(285 / endPlanet.getOrbit());
					}
				}
				catch (IndexOutOfBoundsException e) {
					phaseEnd = "";
				}

				double moveX, moveY;
				double periapse; 
				double apoapsis = startOrbit[0];
				BodyInSpace nearestPlanet = startPlanet;
				double radiusJourney = 0;

				if (steps % 2 == 1) {

					double transferRadius = 0;

					if (phaseStart.equals(phaseEnd)) {
						enterprise.setParent(startPlanet);

						route.setRadiusX((endOrbit[1] + startOrbit[0]) / 2 * SCREEN_SCALE);
						route.setRadiusY((endOrbit[1] + startOrbit[0]) / 2 * SCREEN_SCALE);

						transferRadius = (endOrbit[1] + startOrbit[0])/ 2;

						route.setCenterX(enterprise.getParent().getX() - ((transferRadius - startOrbit[0]) * SCREEN_SCALE));
						route.setCenterY(enterprise.getParent().getY());
						
						if (newStep) {
							movementAngle = Math.PI / 2;
							endAngle = Math.toRadians(270);
						}

					}
					else {
						enterprise.setParent(startPlanet.getParent());

						double distance = Math.abs(endPlanet.getOrbit() - startPlanet.getOrbit());
						
						if (movement == 1) {
							apoapsis = startOrbit[0];
							periapse = endOrbit[1];
							nearestPlanet = startPlanet;
						}
						else {
							periapse = startOrbit[1];
							apoapsis = endOrbit[0];
							nearestPlanet = endPlanet;
						}

						route.setRadiusX((apoapsis + periapse + distance) / 2 * SCREEN_SCALE);
						route.setRadiusY((apoapsis + periapse + distance) / 2 * SCREEN_SCALE);
						
						radiusJourney = route.getRadiusY() + (nearestPlanet.getOrbit() - apoapsis) * SCREEN_SCALE;

						route.setCenterX(enterprise.getParent().getX());
						route.setCenterY(enterprise.getParent().getY() - radiusJourney);
						
						if (newStep) {						
							journeyMoveX = enterprise.getParent().getX() + ((radiusJourney) * 
									Math.sin(startPlanet.getAngle()));	
							
							journeyMoveY = enterprise.getParent().getY() - ((radiusJourney) * 
									Math.cos(startPlanet.getAngle()));
							
						}

						route.setCenterX(journeyMoveX);
						route.setCenterY(journeyMoveY);

					}

					enterprise.setRadius(route.getRadiusX() / SCREEN_SCALE, route.getRadiusY() / SCREEN_SCALE);

					if (newStep) {

						if (movement == -1) {
							// PI
							enterprise.setAngle(movementAngle);
						}
						else {
							// 0
							enterprise.setAngle(movementAngle);
						}

						newStep = false;
						rotateCount = 0;
						routeData.setText(calc.getTransferData());
						double days = Math.floor(calc.getTime() / 86400);
						
						double transitionSpeed = 360 / ((endPlanet.getAngularV() * Math.PI) / transAngle);
						
						enterprise.setPeriod(transitionSpeed);
						
											
					}
					else {
						enterprise.incrementAngle();
					}

					if (phaseStart.equals(phaseEnd)) {
						enterprise.setCenterPoint(enterprise.getParent().getX() - ((transferRadius - startOrbit[0]) * SCREEN_SCALE), enterprise.getParent().getY());
					}
					else {
						//enterprise.setCenterPoint(enterprise.getParent().getX(), enterprise.getParent().getY() - route.getRadiusY() - (nearestPlanet.getOrbit() - apoapsis) *  SCREEN_SCALE);
						enterprise.setCenterPoint(journeyMoveX, journeyMoveY);
					}

					rotateCount++;
					//enterprise.getAngle() == 3.14
					if (Math.abs(Math.toDegrees(enterprise.getAngle() - endAngle)) < 1) {					
						//timeline.pause();
						steps++;
						newStep = true;
					}
					
				}

				else {

					if (newStep) {
						newStep = false;

						if (movement == -1) {
							enterprise.setAngle(3.14);
						}
						
						if (!(phaseEnd == "")) {
							double r1 = endPlanet.getRadius() + 1.0e3 * endOrbit[0];
							double r2 = endPlanet.getRadius() + 1.0e3 * endOrbit[1];
							
							if (startPlanet.getName() != endPlanet.getName()) { transferWindow = true; }
							else { transferWindow = false; }
							
							MathEllipse transfer = new MathEllipse(endPlanet.getMass(), r2, r1);
							calc.transfer_slow(endPlanet, transfer);
							
						}
					}
					
					if (transferWindow && !(phaseEnd == "")) {
						double phase = Math.toDegrees(startPlanet.getAngle() - endPlanet.getAngle());
						if (phase < 0) { phase += 360.0; }
						//System.out.println(phase + " | " + calc.getStartPhaseAngle());
						if (Math.abs(phase - calc.getStartPhaseAngle()) < 1) {
							movementAngle = startPlanet.getAngle() - Math.PI;
							if (movementAngle < 0) {
								movementAngle += 2 * Math.PI;
							}
							endAngle = startPlanet.getAngle();	
							transAngle = startPlanet.getAngle() - endPlanet.getAngle();
							System.out.println(Math.toDegrees(startPlanet.getAngle()) + " | " + Math.toDegrees(endPlanet.getAngle()));
							//timeline.pause();
							steps++;
							newStep = true;
						}
					}


					double offset;

					if (startOrbit[0] != 0 && startOrbit[1] != 0) {
						MathEllipse orbit = new MathEllipse(startPlanet.getMass(), startOrbit[0], startOrbit[1]);

						offset = (startOrbit[0] - startOrbit[1]) / 2;

						enterprise.setRadius(orbit.semi_major(), orbit.semi_minor());
						//enterprise.getGUITrail().setRotate(45);

					}

					else {
						enterprise.setRadius(0, 0);
						offset = 0;
					}

					enterprise.incrementAngle();
					
					enterprise.setCenterPoint(startPlanet.getX() + (offset * SCREEN_SCALE), startPlanet.getY());

				}

				moveX = enterprise.getCenterX() + (enterprise.getRadiusX() * SCREEN_SCALE) * 
						Math.sin(enterprise.getAngle());

				moveY = enterprise.getCenterY() - (enterprise.getRadiusY() * SCREEN_SCALE) * 
						Math.cos(enterprise.getAngle());

				enterprise.setRadius(enterprise.getRadiusX() * SCREEN_SCALE, enterprise.getRadiusY() * SCREEN_SCALE);

				moveBall(enterprise.getGUIShip(), moveX, moveY);

			}
		};


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

		systemPane.getChildren().add(route);

		timeline.play();

		double hey = sourcePane.getPrefWidth() / 2;
		System.out.println(hey);
		Circle x =  new Circle(hey, hey, 15);
		x.getStyleClass().add("body-Mars");


		sourcePane.getChildren().add(x);
		Circle y =  new Circle(hey, hey, 15);
		y.getStyleClass().add("body-Earth");
		destPane.getChildren().add(y);

	}


	private void moveBall(Shape ball, double x, double y) {
		TranslateTransition move = new TranslateTransition(
				Duration.millis(STEP_DURATION), ball);
		move.setToX(x);
		move.setToY(y);
		move.playFromStart();
	}

	private void resetScale(double new_val) {
        double val;
        if (new_val > 0.36) {
            val = 0.36;
        }
        else {
            val = new_val;
        }

		SCREEN_SCALE = val;
		for (BodyInSpace current: planets.values()) {
			current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
		}
	}

	@FXML protected void nextPhase(ActionEvent event) throws IOException { 
		if (steps / 2 < routePlanets.size() - 1) {
			steps++;
			newStep = true;
			timeline.play();
		}
		else {
            help.toFront();
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

		root = FXMLLoader.load(getClass().getResource("../resources/xml/pathselect.fxml"));
		Scene scene = new Scene(root);

		stage.setScene(scene);
		stage.show();
	}

}
