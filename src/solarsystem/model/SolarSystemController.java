package solarsystem.model;
 
import java.io.IOException;
import java.net.URL;
import java.util.Map;
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
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import solarsystem.objects.BodyInSpace;
import solarsystem.objects.SpaceObjects;

/**
 * Controller class used to animate the solar system view of the program.
 * @author Laura McGhie
 */
public class SolarSystemController extends SuperController implements Initializable {

    @FXML private Pane systemPane;
    @FXML private Slider zoomSlide;
    @FXML private Button switchScene;
	@FXML private Button resetButton;
	@FXML private Button speedButton;
	@FXML private Button slowButton;
	private Timeline timeline;
	private Map<String, BodyInSpace> selection = SpaceObjects.getPlanets();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

		setUpPlanets();

		// Initialise the slider to the Sun scale values
		zoomSlide.setValue(SCREEN_SCALE);
		zoomSlide.setMin(SpaceObjects.getScale("Sun").get(0));
		zoomSlide.setMax(SpaceObjects.getScale("Sun").get(1));

		/* Give control of the screen scale to the displayed slider, and adjust the orbits if the slider is moved */
		zoomSlide.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				SCREEN_SCALE = (double) new_val;
				for (BodyInSpace current: selection.values()) {
					current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
				}
			}
		});
    }
    
    
    private void setUpPlanets() {

		// Redraw the planet set at 0,0 to counteract object displacement
    	for (BodyInSpace current : selection.values()) {
            current.resetPlanet();
        }

		/* Event handler to move the planets around their orbits. For each planet shown on the screen, we
		 * increment their current angle by the speed factor, work out the x and y co-ords on their orbit circle
		 * that correspond to this angle, and then move the planet to this location. */
		EventHandler<ActionEvent> planetMovement = new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {

				for (BodyInSpace current: selection.values()) {

					// Increment the angle of the planet by the speed factor
					current.incrementAngle(SPEED_FACTOR);

					/* Work out the x & y co-ords that correspond to the new angle. Multiply by the screen scale
					 * to ensure x & y apply to the current display ratio. */
					double moveX = current.getParent().getX() + (current.getOrbit() * SCREEN_SCALE) * 
							Math.sin(current.getAngle());
					
					double moveY = current.getParent().getY() - (current.getOrbit() * SCREEN_SCALE) * 
							Math.cos(current.getAngle());

					// Update the planet position and move the planet
					current.setPosition(moveX, moveY);
					moveBall(current.getGUIObject(), moveX, moveY);
				}

			}
		};


		// Set up a timeline to display the animation
		timeline = new Timeline(new KeyFrame(Duration.ZERO, planetMovement),
				new KeyFrame(Duration.millis(STEP_DURATION)));

		timeline.setCycleCount(Timeline.INDEFINITE);

		// Add the sun at the centre of the display
		BodyInSpace sun = SpaceObjects.getSun();
		sun.moveGUIObject(systemPane.getPrefWidth() / 2, systemPane.getPrefHeight() / 2);
		systemPane.getChildren().add(sun.getGUIObject());

		// Set scale to end of the scale for the sun
		SCREEN_SCALE = SpaceObjects.getScale("Sun").get(0);

		// Draw the orbit at the scale indicated and add the orbit path and the planet to the screen
		for (BodyInSpace current: selection.values()) {
			current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
			systemPane.getChildren().add(current.getGUIOrbit());
			systemPane.getChildren().add(current.getGUIObject());
		}

		timeline.play();
    }


    /**
	 * Internal method to animate the movement of a body from one location to the next.
	 * @param ball The GUI object to be moved
	 * @param x The x position to move this object to
	 * @param y The y position to move this object to
     */
    private void moveBall(Circle ball, double x, double y) {
		TranslateTransition move = new TranslateTransition(
				Duration.millis(STEP_DURATION), ball);
		move.setToX(x);
		move.setToY(y);
		move.playFromStart();
	}

    /**
	 * Decreases the current movement speed of the system, caps speed at x1/64 and disables button if at cap
	 */
    @FXML protected void slowMovement() {
		if (SPEED_FACTOR > 0.015625) {
			SPEED_FACTOR = SPEED_FACTOR / 2;
		}

		if (SPEED_FACTOR == 0.015625) {
			slowButton.setDisable(true);
		} else {
			slowButton.setDisable(false);
		}
    }

    /**
	 * Increases the current movement speed of the system, caps speed at x512 and disables button if at cap
	 */
    @FXML protected void speedUpMovement() {
		if (SPEED_FACTOR < 512) {
			SPEED_FACTOR = SPEED_FACTOR * 2;
		}

		if (SPEED_FACTOR == 512) {
			speedButton.setDisable(true);
		} else {
			speedButton.setDisable(false);
		}
    }

	/**
	 * Reset the speed factor to its original value, enable both buttons
	 */
	@FXML protected void resetMovement() {
		SPEED_FACTOR = 1;

		speedButton.setDisable(false);
		slowButton.setDisable(false);
	}

    /**
	 * Method used to move from the current view to the Path Selector
	 * @throws IOException
     */
    @FXML protected void mapJourney() throws IOException {
		// Pause the current animation
    	timeline.pause();

		// Use the button clicked on to get a reference to the window
    	Stage stage; 
    	Parent root;
    	stage=(Stage) switchScene.getScene().getWindow();

		// Load the XML file for the Path Selector into a scene
    	root = FXMLLoader.load(getClass().getResource("/solarsystem/resources/xml/pathselect.fxml"));
    	Scene scene = new Scene(root);

		// Replace the current window contents with the Path Selector
        stage.setScene(scene);
        stage.show();
    }

}
