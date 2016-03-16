package solarsystem.controller;
 
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

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

/**
 * Controller class for selecting a journey path to be animated
 * @author Laura McGhie
 */
public class PathSelectionController extends SuperController implements Initializable {

    @FXML private Pane systemPane;
    @FXML private Slider zoomSlide;
    @FXML private TextArea routeList;
    @FXML private Button startButton;
	@FXML private Pane inputPane;
	@FXML private HBox orbit;
	@FXML private Label planetName;
	@FXML private Button landControl;
	@FXML private Button orbitControl;
	@FXML private Button focusControl;
	@FXML private Button unfocusFrame;

	// Initialise the display to the sun and the set of planets
	private BodyInSpace currentParent = SpaceObjects.getSun();
	private Map<String, BodyInSpace> childBodies = SpaceObjects.getPlanets();

	// Variable to save the scale of the display between changes
	private double scaleSave;

	// Set up logger for console messages
	private final static Logger LOGGER = Logger.getLogger(PathSelectionController.class.getName());


	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		// Initialise the slider to the Sun scale values
		zoomSlide.setValue(SCREEN_SCALE);
		zoomSlide.setMin(SpaceObjects.getScale("Sun").get(0));
		zoomSlide.setMax(SpaceObjects.getScale("Sun").get(1));

		/* Give control of the screen scale to the displayed slider, and adjust the orbits
		 * and planet positions if the slider is moved */
		zoomSlide.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				SCREEN_SCALE = (double) new_val;

				for (BodyInSpace current: childBodies.values()) {
					current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);

					// Calculate the x & y positions of the planet at the new scale and move it to there
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

		// Render the current solar system on the screen
		displaySystem();

		/* By default we show the Sun at the start of the process, which has no parent system,
		 * so disable the return button */
		unfocusFrame.setDisable(true);

		// Set up the click and drag function for panning the solar system
		setUpSystemDrag();

		// Set up the click action for selecting a body in the system
		setUpPlanetSelector();
	}

	/**
	 * Internal method for setting up the drag functionality for panning around the current system
	 */
	private void setUpSystemDrag() {

		/* Method for performing the system move */
		EventHandler<MouseEvent> moveSystem = new EventHandler<MouseEvent>() {
			double startX;
			double startY;

			@Override
			public void handle(MouseEvent event) {

				// If the event is the start of a drag, then record the start co-ordinates of the drag
				if (event.getEventType() == MouseDragEvent.MOUSE_DRAG_ENTERED) {
					startX = event.getX();
					startY = event.getY();
				}
				else {
					// At the end of the drag, move the system the distance and the direction of the drag
					systemPane.setTranslateX(systemPane.getTranslateX() + (event.getX() - startX));
					systemPane.setTranslateY(systemPane.getTranslateY() + (event.getY() - startY));
				}

				event.consume();
			}
		};

		/* If we detect a drag, then we trigger a full drag to handle the movement */
		EventHandler<MouseEvent> startDrag = new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				systemPane.startFullDrag();

			}
		};

		// Add the event handlers to the appropriate event types
		systemPane.addEventHandler(MouseDragEvent.DRAG_DETECTED, startDrag);
		systemPane.addEventHandler(MouseDragEvent.MOUSE_DRAG_ENTERED, moveSystem);
		systemPane.addEventHandler(MouseDragEvent.MOUSE_DRAG_OVER, moveSystem);
		systemPane.addEventHandler(MouseDragEvent.MOUSE_DRAG_EXITED, moveSystem);
	}

	/**
	 * Internal method for setting up the planet selection event
	 */
	private void setUpPlanetSelector() {

		EventHandler<MouseEvent> planetLander = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				boolean planetFound = false;

				// Display the orbit entry dialog by bringing it to the front of the display
				inputPane.toFront();

				/* If the orbit entry dialog is going to display outside of the window, move it so that it is
				 * completely within the window. */
				double xShift = event.getSceneX();
				if (event.getSceneX() + 250 > 750) {
					xShift = 500;
				}
				inputPane.setTranslateX(xShift);

				double yShift = event.getSceneY();
				if (event.getSceneY() + 100 > 600) {
					yShift = 500;
				}
				inputPane.setTranslateY(yShift);

				/* If the current target is an object corresponding to a body, set the name of the planet
				 * in the dialog and disable buttons as required. */
				for (BodyInSpace current: childBodies.values()) {
					if (event.getTarget().equals(current.getGUIObject())){

						planetFound = true;

						// Set the dialog name to the body name
						final String name = current.getName();
						planetName.setText(name);

						disableButtons(name);
					}
				}

				/* If the current target is the current system parent, and this parent is not the sun, then
				 * set up the dialog for displaying information about the parent */
				if (event.getTarget().equals(currentParent.getGUIObject()) && !currentParent.getName().equals("Sun")){

					planetFound = true;

					// Set the dialog name to the parent name
					final String name = currentParent.getName();
					planetName.setText(name);

					disableButtons(name);
				}

				/* If we cannot find a planet corresponding to the target, then hide the dialog and
				 * clear out any fields added for entering the orbit. */
				if (!planetFound) {
					inputPane.toBack();
					orbit.getChildren().clear();
				}

				event.consume();
			}

		};

		// Run the event on a mouse click
		systemPane.addEventHandler(MouseEvent.MOUSE_CLICKED, planetLander);
	}

	/**
	 * Internal helper method to render the current system on the display
	 */
	private void displaySystem() {

		// If the current parent is the sun, add this first as we do not want this to be selectable.
		if(currentParent.getName().equals("Sun")) {
			systemPane.getChildren().add(currentParent.getGUIObject());
		}

		// Place the current parent in the center of the available space
		currentParent.moveGUIObject(systemPane.getPrefWidth() / 2, systemPane.getPrefHeight() / 2);

		// As long as the current parent has child bodies to display
		if (!childBodies.isEmpty()) {

			/* Iterate through the body list and add all planet orbit circles to the display first
			 * This is so they are at the bottom of the stack and do not cover the planet objects. */
			for (BodyInSpace current : childBodies.values()) {
				current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
				systemPane.getChildren().add(current.getGUIOrbit());
			}

			/* Iterate through the body list and add all planet objects at the appropriate locations.*/
			for (BodyInSpace current : childBodies.values()) {

				// Reset the body so that it is drawn at the origin point, counteracting any displacement issues
				current.resetPlanet();

				/* Move the body to the appropriate location by calculating it based on the angle
				 * and current screen scale	 */
				current.moveGUIObject(
						(current.getParent().getX() +
								(current.getOrbit() * SCREEN_SCALE) *
										Math.sin(current.getAngle())),

						(current.getParent().getY() -
								(current.getOrbit() * SCREEN_SCALE) *
										Math.cos(current.getAngle())));

				// Add the planet object to the display
				systemPane.getChildren().add(current.getGUIObject());

				// If the body is on the current route, mark it to show this
				if (routePlanets.contains(current.getName())) {
					markForRoute(current.getName());
				}
			}

			// If the current parent is not the sun, then add this last so that it can be selected to add to the route
			if(!currentParent.getName().equals("Sun")) {
				systemPane.getChildren().add(currentParent.getGUIObject());
			}
		}
	}

	/**
	 * Internal method to deactivate any buttons on the planet dialog which are not appropriate for the current situation
	 * @param name Name of the currently selected body
     */
	private void disableButtons(String name) {
		try {
			// Retrieve the last item in the current route
			int lastItem = routePlanets.size() - 1;
			String prev = routePlanets.get(lastItem);
			double[] prevOrbit = routeOrbit.get(lastItem);

			// Get the BodyInSpace objects for the last item on the route, and the currently selected body
			BodyInSpace previous = SpaceObjects.getBody(prev);
			BodyInSpace current = SpaceObjects.getBody(name);

			// If currently landed on the body and selecting the same body, allow orbit only
			if (prev.equals(name) && prevOrbit[0] == 0.0 && prevOrbit[1] == 0.0) {
				orbitControl.setDisable(false);
				landControl.setDisable(true);
			}
			/* If landed on a body and selecting a different body, then disable all as we
			 * cannot make this transfer - must go into orbit around the original planet first */
			else if (prevOrbit[0] == 0.0 && prevOrbit[1] == 0.0) {
				landControl.setDisable(true);
				orbitControl.setDisable(true);
			}
			/* If selecting the same body as previously, and we are not landed on that body, then we
			 * can either land on this body or go into a different orbit around it */
			else if (prev.equals(name)) {
				landControl.setDisable(false);
				orbitControl.setDisable(false);
			}
			/* If selecting a different body and this body is a sibling, parent or child of the previous,
			 * then allow the transfer */
			else if(previous.isSibling(current) || previous.isChild(current) || previous.isParent(current)) {
				landControl.setDisable(true);
				orbitControl.setDisable(false);
			}
			/* If we do not meet any of the previous conditions, then cannot make the
			 * transfer so disable all buttons */
			else {
				landControl.setDisable(true);
				orbitControl.setDisable(true);
			}
		} catch (ArrayIndexOutOfBoundsException e) {
			/* If we have this exception, it means there are no selected planets and we are at
			 * the start of the journey. Thus allow land only as must start at a body's surface. */
			landControl.setDisable(false);
			orbitControl.setDisable(true);
		}

		/* If the selected body has no children, or is the current parent of the system,
		 * then disable the focus button */
		if (SpaceObjects.getChildren(name).isEmpty() || name.equals(currentParent.getName())) {
			focusControl.setDisable(true);
		}
		else {
			focusControl.setDisable(false);
		}
	}

	/**
	 * Mark the GUI object of the given body to show the body is a part of the current route
	 * @param body Name of body
     */
	private void markForRoute(String body) {
		Circle planetObj = SpaceObjects.getBody(body).getGUIObject();

		// Add a white stroke around the body
		planetObj.setStrokeWidth(2);
		planetObj.setStroke(Paint.valueOf("white"));
	}

	/**
	 * Remove the marked style around the GUI object of the given body
	 * @param body Name of body
     */
	private void unmarkForRoute(String body) {
		Circle planetObj = SpaceObjects.getBody(body).getGUIObject();
		planetObj.setStrokeWidth(0);
	}

	/**
	 * Method to recenter the planet system if it has been moved around the screen
	 */
	@FXML protected void centerPlanets(){
		systemPane.setTranslateX(0);
		systemPane.setTranslateY(0);
	}

	/**
	 * Method to display option to enter orbit information for the route
	 */
	@FXML protected void displayOrbit() {

		/* If the text entry fields to create an orbit to not already exist in the dialog, then add them in */
		if (orbit.getChildren().isEmpty()) {

			/* Add text fields for entering the apoapsis and periapsis of the orbit - set hint text to
			 * show user which is which */
			TextField apoapsisEntry = new TextField();
			apoapsisEntry.setPromptText("Apoapsis (km)");
			TextField periapsisEntry = new TextField();
			periapsisEntry.setPromptText("Periapsis (km)");

			// Add a button to allow the user to submit an orbit to the route
			Button submit = new Button();
			submit.setId("confirmButton");

			// Once an orbit is entered, add this to the route
			submit.setOnAction(new EventHandler<ActionEvent>() {
			    @Override public void handle(ActionEvent e) {
					// Retrieve the planet name on the dialog to work out the destination
					String planet = planetName.getText();
			        try {
						// Parse the entered numbers to double values
						double apoapsis = Double.parseDouble(apoapsisEntry.getText());
						double periapsis = Double.parseDouble(periapsisEntry.getText());

						// If the orbit distance is not greater than 0, then throw an exception
						if (apoapsis <= 0 || periapsis <= 0) {
							throw new IllegalArgumentException();
						}

						// Set up an array and reorder the entered text so that the larger number always comes first
						double orbit[] = new double[2];
						if (apoapsis > periapsis) {
							orbit[0] = apoapsis;
							orbit[1] = periapsis;
						} else {
							orbit[0] = periapsis;
							orbit[1] = apoapsis;
						}

						// Add the orbit to the route orbit list
						routeOrbit.add(orbit);

						// Add the destination planet to the planet route list
						routePlanets.add(planet);

						// Add information about this orbit to the route list so it is displayed to the user
						routeList.setText(routeList.getText() + planet + " Orbit\r\n\t" + orbit[0] + "km\r\n\t" +
								orbit[1] + "km\r\n");

						// Mark the planet as being on the route
						markForRoute(planet);

						// Disable the buttons as appropriate for this new stage of the journey
						disableButtons(planet);

						// Clear the text entry fields from the dialog
						PathSelectionController.this.orbit.getChildren().clear();

					} catch (NumberFormatException exception) {
						// Display an error if non numeric values added
						LOGGER.warning("Non number value passed to orbit");
						apoapsisEntry.setText("Invalid");
						periapsisEntry.setText("orbit.");
					} catch (IllegalArgumentException exception) {
						// Display an error if the enter values are not over 0
						LOGGER.warning("Orbit value must be greater than 0");
						apoapsisEntry.setText("Invalid");
						periapsisEntry.setText("orbit.");
					}
			    }
			});

			// Add the text entry and button to the dialog display
			orbit.getChildren().add(apoapsisEntry);
			orbit.getChildren().add(periapsisEntry);
			orbit.getChildren().add(submit);
		}
	}

	/**
	 * Adds a land on the selected planet to the route list
	 */
	@FXML protected void landOnPlanet() {

		// If the orbit entry fields are displayed, remove these from display
		if (!orbit.getChildren().isEmpty()) {
			orbit.getChildren().clear();
		}

		// Get the planet to land on from the planet name on the dialog
		String planet = planetName.getText();

		// As we are landing on the planet, set the orbit numbers to 0,0 and add the information the route lists
		double[] landed = {0, 0};
		routePlanets.add(planet);
		routeOrbit.add(landed);

		// Add the route stage to the display list for the user
		routeList.setText(routeList.getText() + " " + planet + " Surface\r\n");

		// Mark the GUI object for the body to show it is on the route
		markForRoute(planet);

		// Disable the selection buttons as appropriate for this new stage
		disableButtons(planet);
	}

	/**
	 * Action for the undo button to remove the last item on the current route
	 */
	@FXML protected void removeLast() {

		int lastItem = routePlanets.size() - 1;

		// As long as the route list has content, remove the item
		if (!(lastItem < 0))  {

			LOGGER.info("Removing " + routePlanets.get(lastItem) + " " + routeOrbit.get(lastItem)[0] + " "
					+ routeOrbit.get(lastItem)[1] + " from the route list...");

			// Unmark the item on the GUI and remove the item from the lists
			unmarkForRoute(routePlanets.get(lastItem));
			routePlanets.remove(lastItem);
			routeOrbit.remove(lastItem);

			/* Construct a new string to display the route to the user by iterating over all
			 * the planets and adding the appropriate information to the string. Also mark the GUI objects,
			 * in case the removed planet occurs earlier in the route, thus still needs to be marked. */
			StringBuilder newRouteList = new StringBuilder();
			for (int i = 0; i < routePlanets.size(); i++) {
				newRouteList.append(routePlanets.get(i)).append(" ");
				
				if (routeOrbit.get(i)[0] == 0 && routeOrbit.get(i)[1] == 0) {
					newRouteList.append("Surface\r\n");
				}
				else {
					newRouteList.append("Orbit\r\n\t");
					newRouteList.append(routeOrbit.get(i)[0]).append("km\r\n\t");
					newRouteList.append(routeOrbit.get(i)[1]).append("km\r\n");
				}
				
				markForRoute(routePlanets.get(i));
			}

			// Display the route list on screen for the user
			routeList.setText(newRouteList.toString());

			// Disable buttons for the new context - if the list is now empty, pass value to the method to indicate this
            if (lastItem != 0) {
                disableButtons(routePlanets.get(lastItem - 1));
            } else {
                disableButtons("Start");
            }
		}
	}

	/**
	 * Changes the frame of the reference to display with a planet at the centre, and
	 * its satellites as the children around it
	 */
	@FXML protected void focusOnPlanet() {
		// Work out which planet to focus on from the text field in the dialog
		String planet = planetName.getText();

		// Hide the dialog used for data entry
		inputPane.toBack();

		// Set the system parent to the selected planet and make the child bodies the currently displayed selection
		currentParent = SpaceObjects.getPlanets().get(planet);
		childBodies = SpaceObjects.getChildren(planet);

		// Clear all bodies on the screen so the new ones can be added
		systemPane.getChildren().clear();

		// Save the current scale on the main solar system view, and set the scale for the new frame
		scaleSave = SCREEN_SCALE;
		SCREEN_SCALE = SpaceObjects.getScale(planet).get(0);

		/* If the minimum and maximum scale for the planet are the same, then deactive the slider since we don't
		 * need it. Otherwise, set up the slider with the scale information for this frame. */
		if (SpaceObjects.getScale(planet).get(0).equals(SpaceObjects.getScale(planet).get(1))) {
			zoomSlide.setVisible(false);
		}
		else {
			zoomSlide.setVisible(true);
			zoomSlide.setMin(SpaceObjects.getScale(planet).get(0));
			zoomSlide.setMax(SpaceObjects.getScale(planet).get(1));
			zoomSlide.setValue(SCREEN_SCALE);
		}

		// Since we are now focused on a body, activate the button to return to the main view of the solar system
		unfocusFrame.setDisable(false);

		// Render the new system on the screen
		displaySystem();
	}

	/**
	 * Changes the frame of reference back to the main solar system view so the Sun is at the centre of the screen
	 */
	@FXML protected void focusOnSun() {
		// Hide the data entry dialog and reset the display to the sun and the planets
		inputPane.toBack();
		currentParent = SpaceObjects.getSun();
		childBodies = SpaceObjects.getPlanets();

		// Empty the display of the current frame
		systemPane.getChildren().clear();

		// Reactivate the zoom slider and set the scale to that of the sun
		zoomSlide.setVisible(true);
		zoomSlide.setMin(SpaceObjects.getScale("Sun").get(0));
		zoomSlide.setMax(SpaceObjects.getScale("Sun").get(1));

		// Retrieve the previously saved scale and set this as the current scale
		SCREEN_SCALE = scaleSave;
		zoomSlide.setValue(scaleSave);

		// At the top level, so deactivate the button for returning to the main solar system view
		unfocusFrame.setDisable(true);

		// Render the system on the screen
		displaySystem();
	}

	/**
	 * Method used to display the animation of the inputted route
	 * @throws IOException
     */
	@FXML protected void startJourney() throws IOException {

		// If the current route does not have enough stages, reject the move since there is nothing to animate!
		if (routePlanets.size() < 2) {
			LOGGER.info("Your journey does not contain 2 stages - please add more and try again.");
		} else {
			// Use the button clicked on to get a reference to the window
			Stage stage;
			Parent root;
			stage=(Stage) startButton.getScene().getWindow();

			// Load the XML file for the Journey Animation view into a scene
			root = FXMLLoader.load(getClass().getResource("/solarsystem/resources/xml/journeyanimation.fxml"));
			Scene scene = new Scene(root);

			// Replace the current window contents with the Journey Animator
			stage.setScene(scene);
			stage.show();
		}
	}
	
}
