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
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import solarsystem.objects.BodyInSpace;
import solarsystem.model.SpaceObjects;
 
public class JourneyController extends SuperController implements Initializable {
		
    @FXML private Text actiontarget;
    @FXML private Pane systemPane;
    @FXML private Slider zoomSlide;
    @FXML private Button switchScene;
    
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
		
		BodyInSpace spaceship = new BodyInSpace("Spaceship", 0, 0, 10.0, 600, 1.4, sun, SCREEN_SCALE);
		
		int routeIndex = 0;
		
		EventHandler<ActionEvent> spaceshipMove = new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {
				
					String phaseStart = routePlanets.get(routeIndex);
					String phaseEnd = routePlanets.get(routeIndex + 1);
					
					BodyInSpace startPlanet, endPlanet;
					
					startPlanet = planets.get(phaseStart);
					endPlanet = planets.get(phaseEnd);

					
					double moveX = startPlanet.getX() + (spaceship.getOrbit() * SCREEN_SCALE) * 
							Math.sin(spaceship.getAngle());
					
					double moveY = startPlanet.getY() - (spaceship.getOrbit() * SCREEN_SCALE) * 
							Math.cos(spaceship.getAngle());
					
					spaceship.setPosition(moveX, moveY);
					spaceship.adjustGUIOrbit(spaceship.getOrbit() * SCREEN_SCALE);
					
					moveBall(spaceship.getGUIObject(), moveX, moveY);

			}
		};

		timeline = new Timeline(new KeyFrame(Duration.ZERO, planetMovement),
				new KeyFrame(Duration.ZERO, spaceshipMove),
				new KeyFrame(Duration.millis(STEP_DURATION)));

		timeline.setCycleCount(Timeline.INDEFINITE);
		
		systemPane.getChildren().add(sun.getGUIObject());
		
		systemPane.getChildren().add(spaceship.getGUIObject());

		for (BodyInSpace current: planets.values()) {  
			current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
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
    
    
    @FXML protected void stopTimeline(ActionEvent event) throws IOException { 
    	timeline.pause();
    	
    	Stage stage; 
    	Parent root;
    	stage=(Stage) switchScene.getScene().getWindow();
    	
    	root = FXMLLoader.load(getClass().getResource("pathselect.fxml"));
    	Scene scene = new Scene(root, 650, 650);
    	
        stage.setScene(scene);
        stage.show();
    }

}
