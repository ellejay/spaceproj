package src.solarsystem.model;
 
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import src.solarsystem.objects.BodyInSpace;
import src.solarsystem.model.SpaceObjects;
 
public class PathSelectionController extends SuperController implements Initializable {
	
    @FXML private Pane systemPane;
    @FXML private Slider zoomSlide;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		BodyInSpace[] planets = SpaceObjects.getPlanets();

        zoomSlide.setValue(SCREEN_SCALE);
		
		zoomSlide.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov,
						Number old_val, Number new_val) {
					SCREEN_SCALE = (double) new_val;
					
					for (BodyInSpace current: planets) {
						current.getGUIOrbit().setRadius(current.getOrbit() * SCREEN_SCALE);
						
						if ((double) new_val > 0.455) {
							current.getGUIObject().setRadius(4);
						}
						else {
							current.getGUIObject().setRadius(3);
						}
						
						current.getGUIObject().setCenterX(midPoint + (current.getOrbit() * SCREEN_SCALE) * 
								Math.sin(current.getAngle()));
				
						current.getGUIObject().setCenterY(midPoint - (current.getOrbit() * SCREEN_SCALE) * 
								Math.cos(current.getAngle()));
					}
				}
			});

		BodyInSpace sun = SpaceObjects.getSun();
		sun.moveGUIObject(midPoint, midPoint);
		systemPane.getChildren().add(sun.getGUIObject());
		
		for (BodyInSpace current: planets) {
			current.getGUIOrbit().setRadius(current.getOrbit() * SCREEN_SCALE);
			systemPane.getChildren().add(current.getGUIOrbit());
		}
		
		for (BodyInSpace current: planets) {   
			current.resetPlanet();
			
			current.getGUIObject().setCenterX(midPoint + (current.getOrbit() * SCREEN_SCALE) * 
					Math.sin(current.getAngle()));
	
			current.getGUIObject().setCenterY(midPoint - (current.getOrbit() * SCREEN_SCALE) * 
					Math.cos(current.getAngle()));
			
			systemPane.getChildren().add(current.getGUIObject());
			
			current.getGUIObject().setOnMouseClicked(new EventHandler<MouseEvent>()
	        {
	            @Override
	            public void handle(MouseEvent t) {
	                System.out.println(current.getName());
	            }
	        });
		}
		
	}
	
	@FXML protected void moveLeft(ActionEvent event){
		systemPane.setTranslateX(systemPane.getTranslateX() + 20);
	}
	
	@FXML protected void moveRight(ActionEvent event){
		systemPane.setTranslateX(systemPane.getTranslateX() - 20);
	}

	@FXML protected void moveUp(ActionEvent event){
		systemPane.setTranslateY(systemPane.getTranslateY() + 20);
	}
	
	@FXML protected void moveDown(ActionEvent event){
		systemPane.setTranslateY(systemPane.getTranslateY() - 20);
	}
}
