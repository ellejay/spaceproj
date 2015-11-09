package solarsystem.model;
 
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
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
		
		
		ArrayList<String[]> route = new ArrayList<String[]>();
		
		
		EventHandler<MouseEvent> planetLander = new EventHandler<MouseEvent>() {
			
			MenuItem orbitItem = new MenuItem("Orbit");
			MenuItem landItem = new MenuItem("Land");
			ContextMenu contextFileMenu = new ContextMenu(orbitItem, landItem);

			@Override
			public void handle(MouseEvent event) {	
				String name = null;
				String[] dest = new String[2];
				
				for (String[] blah: route) {
					System.out.print(blah[0] + " " + blah[1] + " / ");
				}
				System.out.print("\n");
				
				for (BodyInSpace current: planets) {
					if (event.getTarget().equals(current.getGUIObject())){
						name = current.getName();
						//System.out.println(name);
						dest[0] = name;
						
						contextFileMenu.show(systemPane, event.getScreenX(), event.getScreenY());
						orbitItem.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								//System.out.println("--> Orbit");
								dest[1] = "orbit";
								route.add(dest);
							}
						});
						
						landItem.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								//System.out.println("---> Land");
								dest[1] = "land";
								route.add(dest);
							}
						});
						
						try{
							String[] prev = route.get(route.size() - 1);
							
							System.out.println(prev[0] + " " + prev[1]);
							if (prev[0] != name || prev[1] != "orbit") {
								landItem.setDisable(true);
								orbitItem.setDisable(false);
							}
							else {
								landItem.setDisable(false);
							}
						} catch (ArrayIndexOutOfBoundsException e) {
							orbitItem.setDisable(true);
						}
					}
				}
				if (name == null) {
			        contextFileMenu.hide();
				}
				
				event.consume();
			}
			
		};
		

		systemPane.addEventHandler(MouseEvent.MOUSE_CLICKED, planetLander);
		
	}
	
	@FXML protected void centerPlanets(ActionEvent event){
		systemPane.setTranslateX(0);
		systemPane.setTranslateY(0);
	}
}
