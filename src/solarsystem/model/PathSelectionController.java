package solarsystem.model;
 
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import solarsystem.objects.BodyInSpace;
import solarsystem.model.SpaceObjects;
 
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
							current.getGUIObject().setRadius(8);
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
		
	}
	
	@FXML protected void centerPlanets(ActionEvent event){
		systemPane.setTranslateX(0);
		systemPane.setTranslateY(0);
	}
}
