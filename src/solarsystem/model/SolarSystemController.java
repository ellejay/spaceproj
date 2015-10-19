package solarsystem.model;
 
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.util.Duration;
import solarsystem.objects.Planet;
import solarsystem.objects.Star;
 
public class SolarSystemController implements Initializable {
	
	// Planet scale factor
	private static double SCREEN_SCALE = 0.7075;
	// Rotation speed
	private static double STEP_DURATION = 2; //milliseconds
	private static double midPoint = 295;
	private final List<Planet> planets = new ArrayList<>();
	private Timeline timeline = null;
	
    @FXML private Text actiontarget;
    @FXML private Pane systemPane;
    @FXML private Slider zoomSlide;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUp();
    	
        zoomSlide.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				SCREEN_SCALE = (double) new_val;
				for (Planet current: planets) {        	
					systemPane.getChildren().remove(current.getGUIOrbit());
					Circle orbit = new Circle(midPoint, midPoint, current.getOrbit() * SCREEN_SCALE);
					current.setGUIOrbit(orbit);
					systemPane.getChildren().add(orbit);
				}
			}
		});
    }
    
    
    public void setUp() {
	
		Circle sun_obj = new Circle(midPoint, midPoint, 3);
		sun_obj.getStyleClass().add("sun");
		final Star sun = new Star("sun", 1392530, 1.9891e30, sun_obj);
	
		planets.add(new Planet("mercury", 57.92, 58.65, 5.2, sun));
		planets.add(new Planet("venus", 108.2, 224.7, 1.8, sun));
		planets.add(new Planet("earth", 149.6, 365.2, 1.4, sun));
		planets.add(new Planet("mars", 228.0, 687.0, 3.6, sun));
		planets.add(new Planet("jupiter", 779.1, 4333.0, 1.6, sun));
		planets.add(new Planet("saturn", 1426.0, 10759.0, 4.5, sun));
		planets.add(new Planet("uranus", 2870.0, 30685.0, 1.6, sun));
		planets.add(new Planet("neptune", 4493.0, 60200.0, 2.4, sun));
		
		for (Planet current: planets) {
			Circle planet = new Circle(0, 0, 3);
			current.setGUIPlanet(planet);

			Circle orbit = new Circle(midPoint, midPoint, current.getOrbit() * SCREEN_SCALE);
			current.setGUIOrbit(orbit);
		}

		EventHandler<ActionEvent> planetMovement = new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {

				for (Planet current: planets) {

					current.incrementAngle();

					// p(x) = x(0) + r * sin(a)
					// p(y) = y(y) - r * cos(a)
					moveBall(current.getGUIObject(),

							midPoint + (current.getOrbit() * SCREEN_SCALE) * 
							Math.sin(current.getAngle()),

							midPoint - (current.getOrbit() * SCREEN_SCALE) * 
							Math.cos(current.getAngle()));

				}

			}
		};


		timeline = new Timeline(new KeyFrame(Duration.ZERO, planetMovement), 
				new KeyFrame(Duration.millis(STEP_DURATION)));

		timeline.setCycleCount(Timeline.INDEFINITE);
		
		systemPane.getChildren().add(sun_obj);

		for (Planet current: planets) {        	
			systemPane.getChildren().add(current.getGUIOrbit());
			systemPane.getChildren().add(current.getGUIObject());
		}
		

		timeline.play();
	
    }

    
    private void moveBall(Circle ball, double x, double y) {
		TranslateTransition move = new TranslateTransition(
				Duration.millis(STEP_DURATION), ball);
		move.setToX(x);
		move.setToY(y);
		move.playFromStart();
	}
    
    @FXML protected void buttonSpeedSlow(ActionEvent event) {    	
    	updateSpeed(1);
    }
    
    @FXML protected void buttonSpeedMed(ActionEvent event) {    	
    	updateSpeed(2);
    }
    
    @FXML protected void buttonSpeedFast(ActionEvent event) {    	
    	updateSpeed(3);
    }
    
    @FXML protected void buttonSpeedLightning(ActionEvent event) {    	
    	updateSpeed(4);
    }
    
    private void updateSpeed(double speed) {
    	timeline.setRate(speed);
    	STEP_DURATION = speed;
    }
    
    @FXML protected void stopTimeline(ActionEvent event) {    	
    	timeline.pause();
    }
    
}
