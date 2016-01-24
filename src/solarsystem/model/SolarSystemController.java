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
 
public class SolarSystemController extends SuperController implements Initializable {
		
    @FXML private Text actiontarget;
    @FXML private Pane systemPane;
    @FXML private Slider zoomSlide;
    @FXML private Button switchScene;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUp();
        
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


		timeline = new Timeline(new KeyFrame(Duration.ZERO, planetMovement), 
				new KeyFrame(Duration.millis(STEP_DURATION)));

		timeline.setCycleCount(Timeline.INDEFINITE);
		
		BodyInSpace sun = SpaceObjects.getSun();
		sun.moveGUIObject(midPoint, midPoint);
		systemPane.getChildren().add(sun.getGUIObject());

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
    
    @FXML protected void stopTimeline(ActionEvent event) throws IOException { 
    	timeline.pause();
    	
    	Stage stage; 
    	Parent root;
    	stage=(Stage) switchScene.getScene().getWindow();
    	
    	root = FXMLLoader.load(getClass().getResource("../resources/xml/pathselect.fxml"));
    	Scene scene = new Scene(root, 650, 650);
    	
        stage.setScene(scene);
        stage.show();
    }

}
