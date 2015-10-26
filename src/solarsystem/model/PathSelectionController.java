package src.solarsystem.model;
 
import java.net.URL;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
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
						systemPane.getChildren().remove(current.getGUIOrbit());
						Circle orbit = new Circle(midPoint, midPoint, current.getOrbit() * SCREEN_SCALE);
						current.setGUIOrbit(orbit);
						systemPane.getChildren().add(orbit);
						
						//current.moveGUIObject(0, 0);
						current.getGUIObject().relocate(0.0, 0.0);
					}
				}
			});

		BodyInSpace sun = SpaceObjects.getSun();
		sun.moveGUIObject(midPoint, midPoint);
		systemPane.getChildren().add(sun.getGUIObject());
		
		for (BodyInSpace current: planets) {        	
			systemPane.getChildren().add(current.getGUIOrbit());
			systemPane.getChildren().add(current.getGUIObject());
		}
		
	}

}
