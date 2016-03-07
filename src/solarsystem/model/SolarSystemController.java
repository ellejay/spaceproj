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
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import solarsystem.objects.BodyInSpace;
import solarsystem.objects.SpaceObjects;
 
public class SolarSystemController extends SuperController implements Initializable {

    @FXML private Pane systemPane;
    @FXML private Slider zoomSlide;
    @FXML private Button switchScene;
	private Timeline timeline;
	private Map<String, BodyInSpace> selection = SpaceObjects.getPlanets();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUp();

        SCREEN_SCALE = 0.045; 
		zoomSlide.setValue(SCREEN_SCALE);

		zoomSlide.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				SCREEN_SCALE = (double) new_val;
				for (BodyInSpace current: selection.values()) {
					current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
				}
			}
		});
    }
    
    
    private void setUp() {
    	
    	for (BodyInSpace current : selection.values()) {
            current.resetPlanet();
        }
		
		EventHandler<ActionEvent> planetMovement = new EventHandler<ActionEvent>() { 
			@Override
			public void handle(ActionEvent event) {

				for (BodyInSpace current: selection.values()) {

					current.incrementAngle(SPEED_FACTOR);

					// p(x) = x(0) + r * sin(a)
					// p(y) = y(y) - r * cos(a)
					
					double moveX = current.getParent().getX() + (current.getOrbit() * SCREEN_SCALE) * 
							Math.sin(current.getAngle());
					
					double moveY = current.getParent().getY() - (current.getOrbit() * SCREEN_SCALE) * 
							Math.cos(current.getAngle());
					
					current.setPosition(moveX, moveY);
					moveBall(current.getGUIObject(), moveX, moveY);
				}

			}
		};


		timeline = new Timeline(new KeyFrame(Duration.ZERO, planetMovement),
				new KeyFrame(Duration.millis(STEP_DURATION)));

		timeline.setCycleCount(Timeline.INDEFINITE);
        //timeline.setRate(STEP_DURATION);
		
		BodyInSpace sun = SpaceObjects.getSun();
		sun.moveGUIObject(midPoint, midPoint);
		systemPane.getChildren().add(sun.getGUIObject());

		for (BodyInSpace current: selection.values()) {
			current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
			systemPane.getChildren().add(current.getGUIOrbit());
			systemPane.getChildren().add(current.getGUIObject());
		}
		
		EventHandler<MouseEvent> moveSystem = new EventHandler<MouseEvent>() {
			
			double startX;
			double startY;

			@Override
			public void handle(MouseEvent event) {
				
				if (event.getEventType() == MouseDragEvent.MOUSE_DRAG_ENTERED) {
					startX = event.getX();
					startY = event.getY();
				}
				else {
					systemPane.setTranslateX(systemPane.getTranslateX() + (event.getX() - startX));
					systemPane.setTranslateY(systemPane.getTranslateY() + (event.getY() - startY));
				}
				
				event.consume();
				
			}
			
		};
		
		EventHandler<MouseEvent> startDrag = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				systemPane.startFullDrag();
				
			}
			
		};
		
		systemPane.addEventHandler(MouseDragEvent.DRAG_DETECTED, startDrag);
		systemPane.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, moveSystem);	
		systemPane.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER, moveSystem);
		systemPane.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, moveSystem);
		

		timeline.play();
	
    }

    
    private void moveBall(Circle ball, double x, double y) {
		TranslateTransition move = new TranslateTransition(
				Duration.millis(STEP_DURATION), ball);
		move.setToX(x);
		move.setToY(y);
		move.playFromStart();
	}
    
    @FXML protected void slowMovement() {
    	updateSpeed(false);
    }
    
    @FXML protected void speedUpMovement() {
    	updateSpeed(true);
    }
    
    private void updateSpeed(boolean increase) {

		if (increase && timeline.getRate() < 32) {
			//timeline.setRate(timeline.getRate() * 2);
			SPEED_FACTOR = SPEED_FACTOR * 2;
		}
		else if (!increase && timeline.getRate() > (1/32)){
			//timeline.setRate(timeline.getRate() / 2);
			SPEED_FACTOR = SPEED_FACTOR / 2;
		}

    }
    
    @FXML protected void stopTimeline() throws IOException {
    	timeline.pause();
    	
    	Stage stage; 
    	Parent root;
    	stage=(Stage) switchScene.getScene().getWindow();
    	
    	root = FXMLLoader.load(getClass().getResource("/solarsystem/resources/xml/pathselect.fxml"));
    	Scene scene = new Scene(root);
    	
        stage.setScene(scene);
        stage.show();
    }

}
