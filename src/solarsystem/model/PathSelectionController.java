package solarsystem.model;
 
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import solarsystem.objects.BodyInSpace;
import solarsystem.model.SpaceObjects;
 
public class PathSelectionController extends SuperController implements Initializable {
	
    @FXML private Pane systemPane;
    @FXML private Slider zoomSlide;
    @FXML private Label routeList;
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		BodyInSpace[] planets = SpaceObjects.getPlanets();

        zoomSlide.setValue(SCREEN_SCALE);
		
		zoomSlide.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov,
						Number old_val, Number new_val) {
					SCREEN_SCALE = (double) new_val;
					
					for (BodyInSpace current: planets) {
						current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
						
						if ((double) new_val > 0.455) {
							current.getGUIObject().setRadius(8);
						}
						else {
							current.getGUIObject().setRadius(3);
						}
						
						current.moveGUIObject(
								(current.getParent().getX() + 
								(current.getOrbit() * SCREEN_SCALE) * 
								Math.sin(current.getAngle())), 
								
								(current.getParent().getY() - 
								(current.getOrbit() * SCREEN_SCALE) * 
								Math.cos(current.getAngle())));
					}
				}
			});

		systemPane.getChildren().add(SpaceObjects.getSun().getGUIObject());
		
		for (BodyInSpace current: planets) {
			current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
			systemPane.getChildren().add(current.getGUIOrbit());
		}
		
		for (BodyInSpace current: planets) {   
			current.resetPlanet();
			
			current.moveGUIObject(
					(current.getParent().getX() + 
					(current.getOrbit() * SCREEN_SCALE) * 
					Math.sin(current.getAngle())), 
					
					(current.getParent().getY() - 
					(current.getOrbit() * SCREEN_SCALE) * 
					Math.cos(current.getAngle())));
			
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
		
		
		ArrayList<String> routePlanets = new ArrayList<String>();
		ArrayList<double[]> routeOrbit = new ArrayList<double[]>();
		
		
		EventHandler<MouseEvent> planetLander = new EventHandler<MouseEvent>() {
			
			MenuItem orbitItem = new MenuItem("Orbit");
			MenuItem landItem = new MenuItem("Land");
			ContextMenu contextFileMenu = new ContextMenu(orbitItem, landItem);

			@Override
			public void handle(MouseEvent event) {	
				double[] landed = {0, 0};
				boolean planetFound = false;
				
				for (BodyInSpace current: planets) {
					if (event.getTarget().equals(current.getGUIObject())){
						String name = current.getName();
						planetFound = true;
						
						contextFileMenu.show(systemPane, event.getScreenX(), event.getScreenY());
						
						orbitItem.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								
								routeList.setText(routeList.getText() + " " + name + " Orbit :");

								Parent root;
								try {
									root = FXMLLoader.load(getClass().getResource("orbitdialog.fxml"));
									Stage dialog = new Stage();
									dialog.setTitle("FXML Space");
									dialog.setScene(new Scene(root, 200, 150));
									dialog.showAndWait();
									
									routePlanets.add(name);
									routeOrbit.add(orbitParams);
									
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}

							}
						});
						
						landItem.setOnAction(new EventHandler<ActionEvent>() {
							@Override
							public void handle(ActionEvent event) {
								routePlanets.add(name);
								routeOrbit.add(landed);
								routeList.setText(routeList.getText() + " " + name + " Surface :");
							}
						});
						
						try{
							int lastItem = routePlanets.size() - 1;
							String prev = routePlanets.get(lastItem);
							double[] prevOrbit = routeOrbit.get(lastItem);
							
							if (prev != name || (prevOrbit[0] == 0.0 && prevOrbit[1] == 0.0)) {
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
				if (!planetFound) {
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
