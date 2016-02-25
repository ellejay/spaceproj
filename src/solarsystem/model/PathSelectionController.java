package solarsystem.model;
 
import java.io.IOException;
import java.net.URL;
import java.util.Map;
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
import javafx.scene.control.*;
import javafx.scene.input.MouseDragEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import solarsystem.objects.BodyInSpace;
import solarsystem.objects.SpaceObjects;
 
public class PathSelectionController extends SuperController implements Initializable {
	
    @FXML private Pane systemPane;
    @FXML private Slider zoomSlide;
    @FXML private TextArea routeList;
    @FXML private Button startButton;
	@FXML private Pane help;
	@FXML private HBox orbit;
	@FXML private Label planetName;
	@FXML private Button landControl;
	@FXML private Button orbitControl;
	private BodyInSpace currentParent = SpaceObjects.getSun();
	private Map<String, BodyInSpace> selection = SpaceObjects.getDictionary();
    
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

        zoomSlide.setValue(SCREEN_SCALE);
		
		zoomSlide.valueProperty().addListener(new ChangeListener<Number>() {
				public void changed(ObservableValue<? extends Number> ov,
						Number old_val, Number new_val) {
					SCREEN_SCALE = (double) new_val;
					
					for (BodyInSpace current: selection.values()) {
						current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
						
						if ((double) new_val > 0.455) {
							current.getGUIObject().setRadius(8);
						}
						else {
							current.getGUIObject().setRadius(4);
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

		displaySystem();

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



		EventHandler<MouseEvent> planetLander = new EventHandler<MouseEvent>() {

			final MenuItem orbitItem = new MenuItem("Orbit");
			final MenuItem landItem = new MenuItem("Land");
			final ContextMenu contextFileMenu = new ContextMenu(orbitItem, landItem);

			@Override
			public void handle(MouseEvent event) {	
				//final double[] landed = {0, 0};
				boolean planetFound = false;

				help.toFront();

				help.setTranslateX(event.getSceneX());

                double yShift = event.getSceneY();
                if (event.getSceneY() > 500) {
                    yShift = 500;
                }
				help.setTranslateY(yShift);

				System.out.println(event.getTarget());
				
				for (BodyInSpace current: selection.values()) {
					if (event.getTarget().equals(current.getGUIObject())){
						final String name = current.getName();
						planetFound = true;
						
						planetName.setText(name);
						
						disableButtons(name);		
					}
				}

				if (event.getTarget().equals(currentParent) && !currentParent.getName().equals("Sun")){
					final String name = currentParent.getName();
					planetFound = true;

					planetName.setText(name);

					disableButtons(name);
				}
				
				if (!planetFound) {
			        contextFileMenu.hide();
					help.toBack();
					orbit.getChildren().clear();
				}
				
				event.consume();
			}
			
		};
		systemPane.addEventHandler(MouseEvent.MOUSE_CLICKED, planetLander);
		
	}
	
	private void disableButtons(String name) {
				try {
					int lastItem = routePlanets.size() - 1;
					String prev = routePlanets.get(lastItem);
					double[] prevOrbit = routeOrbit.get(lastItem);
					
					// If landed and selecting new planet, disable all
					if (!prev.equals(name) && (prevOrbit[0] == 0.0 && prevOrbit[1] == 0.0)) {
						landControl.setDisable(true);
						orbitControl.setDisable(true);
					}
					// If landed and selecting same planet, allow orbit
					else if (prevOrbit[0] == 0.0 && prevOrbit[1] == 0.0) {
						orbitControl.setDisable(false);
						landControl.setDisable(true);
					}
					else if (prev.equals(name)) {
						landControl.setDisable(false);
					}
					else {
						landControl.setDisable(true);
					}
				// When selecting start point
				} catch (ArrayIndexOutOfBoundsException e) {
                    landControl.setDisable(false);
					orbitControl.setDisable(true);
				}
			
		
	}

	private void markForRoute(String planet) {
		Circle planetObj = selection.get(planet).getGUIObject();
		planetObj.setStrokeWidth(2);
		planetObj.setStroke(Paint.valueOf("white"));
	}
	
	private void unmarkForRoute(String planet) {
		Circle planetObj = selection.get(planet).getGUIObject();
		planetObj.setStrokeWidth(0);
	}

	@FXML protected void centerPlanets(ActionEvent event){
		systemPane.setTranslateX(0);
		systemPane.setTranslateY(0);
	}
	
	@FXML protected void startJourney(ActionEvent event) throws IOException { 
		
		if (routePlanets.size() < 2) {
			
		}
		
		else {
			
	    	Stage stage; 
	    	Parent root;
	    	stage=(Stage) startButton.getScene().getWindow();
	    	
	    	root = FXMLLoader.load(getClass().getResource("../resources/xml/journeyanimation.fxml"));
	    	Scene scene = new Scene(root);
	    	
	        stage.setScene(scene);
	        stage.show();
		}
    }
	
	@FXML protected void displayOrbit() {
		if (orbit.getChildren().isEmpty()) {
			TextField text = new TextField();
			TextField text2 = new TextField();
			Button submit = new Button("Y");
			submit.setOnAction(new EventHandler<ActionEvent>() {
			    @Override public void handle(ActionEvent e) {
			        orbit.getChildren().clear();
			        
			        String planet = planetName.getText();
			        
			        routePlanets.add(planet);
			        double first = Double.parseDouble(text.getText());
			        double second = Double.parseDouble(text2.getText());
			        double pass[] = new double[2];
			        if (first > second) {
			        	pass[0] = first;
			        	pass[1] = second;
			        } else {
			        	pass[0] = second;
			        	pass[1] = first;
			        }
					routeOrbit.add(pass);
					
					routeList.setText(routeList.getText() + " " + planet + " Orbit\r\n\t" + pass[0] + " " +
								      pass[1] + "\r\n");
					markForRoute(planet);
					disableButtons(planet);
			    }
			});
			orbit.getChildren().add(text);
			orbit.getChildren().add(text2);
			orbit.getChildren().add(submit);
		}
	}

	@FXML protected void focusOnPlanet() {
		String planet = planetName.getText();

		help.toBack();
		currentParent = SpaceObjects.getDictionary().get(planet);
		selection = SpaceObjects.getChildren(planet);

		systemPane.getChildren().clear();

		SCREEN_SCALE = 200;
		displaySystem();
	}

	@FXML protected void focusOnSun() {
		help.toBack();
		currentParent = SpaceObjects.getSun();
		selection = SpaceObjects.getDictionary();

		systemPane.getChildren().clear();

		SCREEN_SCALE = zoomSlide.getValue();
		displaySystem();
	}
	
	@FXML protected void landOnPlanet() {
		if (!orbit.getChildren().isEmpty()) {
			orbit.getChildren().clear();
		}
		String planet = planetName.getText();
		
		double[] landed = {0, 0};
		routePlanets.add(planet);
		routeOrbit.add(landed);
		
		routeList.setText(routeList.getText() + " " + planet + " Surface\r\n");
		markForRoute(planet);
		disableButtons(planet);
	}
	
	@FXML protected void removeLast() {
		int lastItem = routePlanets.size() - 1;

        System.out.println(lastItem);

		if (!(lastItem < 0))  {
			
			unmarkForRoute(routePlanets.get(lastItem));
			
			routePlanets.remove(lastItem);
			routeOrbit.remove(lastItem);
			
			//double[] landed = {0,0};
			StringBuilder newRouteList = new StringBuilder();
			for (int i = 0; i < routePlanets.size(); i++) {
				newRouteList.append(routePlanets.get(i) + " ");
				
				if (routeOrbit.get(i)[0] == 0 && routeOrbit.get(i)[1] == 0) {
					newRouteList.append("Surface\r\n");
				}
				else {
					newRouteList.append("Orbit\r\n\t");
					newRouteList.append(routeOrbit.get(i)[0] + " " + routeOrbit.get(i)[1]);
				}
				
				markForRoute(routePlanets.get(i));
			}
			routeList.setText(newRouteList.toString());

            if (lastItem != 0) {
                disableButtons(routePlanets.get(lastItem - 1));
            } else {
                disableButtons("start");
            }
		}
	}

	private void displaySystem() {

		systemPane.getChildren().add(currentParent.getGUIObject());

		currentParent.moveGUIObject(midPoint, midPoint);

		if (!selection.isEmpty()) {
			for (BodyInSpace current : selection.values()) {
				current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
				systemPane.getChildren().add(current.getGUIOrbit());
			}

			for (BodyInSpace current : selection.values()) {
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
		}
	}
	
}
