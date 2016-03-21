package solarsystem.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import solarsystem.objects.BodyInSpace;
import solarsystem.objects.RouteStage;
import solarsystem.objects.SpaceObjects;
import solarsystem.objects.Spaceship;
import solarsystem.math.Calculator;
import solarsystem.math.MathEllipse;

/**
 * Controller class for showing an animation of a journey through space
 * @author Laura McGhie
 */
public class JourneyController extends SuperController implements Initializable {

    @FXML private Label routeStage;
    @FXML private Pane systemPane;
    @FXML private Pane sourcePane;
    @FXML private TextArea routeData;
    @FXML private Pane completionPane;
    @FXML private TextArea journeyInfo;
    @FXML private Button speedButton;
    @FXML private Button slowButton;

    private Timeline timeline;

    // Track the current step in the animation and whether or not this has been set up on the display
    private int steps = 0;
    private boolean newStepMain = true, newStepFocus = true;

    private boolean transferWindow = false;
    private double startAngle, endAngle, transAngle, drawAngle;

    // Variables for managing the focus view
    private double orbitsSearch;
    private double focusScale;
    private double yPositionOut = 100;
    private double yPositionIn = 200;
    private static double DEFAULT_FALCON_PERIOD = 30;
    private double transOrb1, transOrb2;
    private boolean transferringOrbits, newMove, transferComplete;

    // Store data about the journey taken as a whole
    private final StringBuilder completeJourneyData = new StringBuilder();
    private double totalJourneyTime;

    // Store the displayed parent and its children
    private BodyInSpace currentParent = SpaceObjects.getSun();
    private Map<String, BodyInSpace> childBodies = SpaceObjects.getPlanets();

    private final static Logger LOGGER = Logger.getLogger(JourneyController.class.getName());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUp();
    }

    private void setUp() {

        // Redraw the planet set at 0,0 to counteract object displacement
        for (BodyInSpace current : childBodies.values()) {
            current.resetPlanet();
        }

        // Set the parent position to the middle of the available window space
        currentParent.moveGUIObject(systemPane.getPrefWidth() / 2, systemPane.getPrefHeight() / 2);

        // Set up a planet to show at the centre of the focus pane
        final Circle planetFocus = new Circle(sourcePane.getPrefWidth() / 2, sourcePane.getPrefHeight() / 2, 5);

        /* Event handler to move the planets around their orbits. For each planet shown on the screen, we
		 * increment their current angle by the speed factor, work out the x and y co-ordinates on their orbit circle
		 * that correspond to this angle, and then move the planet to this location. */
        EventHandler<ActionEvent> planetMovement = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (BodyInSpace current : childBodies.values()) {

                    // Increment the angle of the planet by the speed factor
                    current.incrementAngle(SPEED_FACTOR);

                    /* Work out the x & y co-ordinates that correspond to the new angle. Multiply by the screen scale
					 * to ensure x & y apply to the current display ratio. */
                    double moveX = current.getParent().getX() + (current.getOrbit() * SCREEN_SCALE) *
                            Math.sin(current.getAngle());

                    double moveY = current.getParent().getY() - (current.getOrbit() * SCREEN_SCALE) *
                            Math.cos(current.getAngle());

                    // Update the planet position and move the planet
                    current.setPosition(moveX, moveY);
                    moveBall(current.getGUIObject(), moveX, moveY);
                }
            }
        };

        // Set up a calculator object for working out the transfer data
        final Calculator calc = new Calculator(SpaceObjects.getBody(planetsOnPath.get(0).getBody()));

        // Set up two spaceship instances - one for the main planet view and one for the focus view
        final Spaceship enterprise = new Spaceship();
        final Spaceship falcon = new Spaceship();

        /* Using the Falcon for the focus view, so set the centre point of this spaceship to
         * the centre of the focus pane and the radius to 0, as we always start from a land. */
        falcon.setCenterPoint(planetFocus.getCenterX(), planetFocus.getCenterY());
        falcon.setRadius(0, 0);

        // Find the largest orbit distance in the route list
        double maxOrbit = Double.MIN_VALUE;
        for (RouteStage stage : planetsOnPath) {
            if (stage.getPeriapsis() > maxOrbit) {
                maxOrbit = stage.getPeriapsis();
            }
            if (stage.getApoapsis() > maxOrbit) {
                maxOrbit = stage.getApoapsis();
            }
        }

        // Use the largest orbit to calculate the scale to be used in the focus panel
        focusScale = ((sourcePane.getPrefWidth() / 2) - 4) / maxOrbit;

        // Add a line to the focus pane to be used for showing transition onto and off of orbits around planets
        Line entryLine = new Line(0, 0, 0, sourcePane.getPrefHeight());
        entryLine.setStroke(Color.TRANSPARENT);
        entryLine.setStrokeWidth(1);

        // Event Handler to control the movement of the spaceship in the main planet view pane.
        EventHandler<ActionEvent> spaceshipMove = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                // Intermediate step for every item in the list, so divide by two to get the planet references
                int planetIndex = steps / 2;

                // Get information about the starting planet in the current journey stage
                BodyInSpace startPlanet, endPlanet;

                String phaseStart = planetsOnPath.get(planetIndex).getBody();
                RouteStage startStage = planetsOnPath.get(planetIndex);
                startPlanet = SpaceObjects.getBody(phaseStart);

                /* Attempt to get information about the destination planet in the journey stage, as long as
                 * we are not at the end of the journey.  */
                boolean outwardMovement = true;
                String phaseEnd;
                RouteStage endStage;
                if (planetsOnPath.size() > planetIndex + 1) {
                    phaseEnd = planetsOnPath.get(planetIndex + 1).getBody();
                    endPlanet = SpaceObjects.getBody(phaseEnd);
                    endStage = planetsOnPath.get(planetIndex + 1);

                    // Work out the direction of movement and set the scale of the view based on the most distant planet
                    if ((startPlanet.isSibling(endPlanet) && startPlanet.getOrbit() > endPlanet.getOrbit())
                            || (startPlanet.isChild(endPlanet))) {
                        outwardMovement = false;
                        setScale(285 / startPlanet.getOrbit());
                    } else {
                        setScale(285 / endPlanet.getOrbit());
                    }
                } else {
                    phaseEnd = "";
                    endPlanet = startPlanet;
                    endStage = new RouteStage("null", 0, 0);
                }

                // Reset the currently displayed frame based on the next transition in the journey
                if (newStepMain && !phaseEnd.isEmpty()) {
                    if (startPlanet.isSibling(endPlanet)) {
                        changeFrame(startPlanet.getParent().getName(), enterprise);
                    } else if (startPlanet.isParent(endPlanet)) {
                        changeFrame(startPlanet.getName(), enterprise);
                    } else if (startPlanet.isChild(endPlanet)) {
                        changeFrame(endPlanet.getName(), enterprise);
                    }
                }

                /* Odd steps are for transferring between two bodies/orbits */
                if (steps % 2 == 1) {

                    // If we are on a new step, then we need to set up the spacecraft for the new path
                    if (newStepMain) {
                        // For a sibling transfer to a new body
                        if (!phaseStart.equals(phaseEnd) && startPlanet.isSibling(endPlanet)) {

                            /* Set the parent of the spacecraft to be the parent of the siblings, and get references
                             * dependent on which point of the journey is closer to the parent body. */
                            enterprise.setParent(startPlanet.getParent());
                            BodyInSpace nearestPlanet, furthestPlanet;
                            if (outwardMovement) {
                                nearestPlanet = startPlanet;
                                furthestPlanet = endPlanet;

                            } else {
                                nearestPlanet = endPlanet;
                                furthestPlanet = startPlanet;
                            }

                            /* Use the MathEllipse object to create an ellipse between the start and end planets, and
                             * then use the calculated axises to draw the spacecraft's path. */
                            MathEllipse transferPath = new MathEllipse(currentParent.getMass(),
                                    nearestPlanet.getOrbit(), furthestPlanet.getOrbit());
                            enterprise.setRadius(transferPath.semiMinor() * SCREEN_SCALE,
                                    transferPath.semiMajor() * SCREEN_SCALE);

                            /* Calculate the centre point of the path between the two bodies, and then use the start
                             * angle of the journey to work out the required centre point of the spacecraft's path. */
                            double radiusJourney = (transferPath.semiMajor() - endPlanet.getOrbit()) * SCREEN_SCALE;

                            double journeyMoveX = enterprise.getParent().getX() + ((radiusJourney) *
                                    Math.sin(startPlanet.getAngle()));
                            double journeyMoveY = enterprise.getParent().getY() - ((radiusJourney) *
                                    Math.cos(startPlanet.getAngle()));

                            enterprise.setCenterPoint(journeyMoveX, journeyMoveY);

                            // Rotate the path according the angle at which the path is triggered to be displayed
                            enterprise.setPathRotation(Math.toDegrees(startPlanet.getAngle()));

                            /* Get the angular velocity of the destination body and use this to calculate the
                             * period required so that the spacecraft will meet the destination body at the end of
                             * its orbit. */
                            double period = 360 / ((endPlanet.getAngularV() * Math.PI) / (transAngle));
                            enterprise.setPeriod(period);
                        }
                        // For a parent/child transfer
                        else if (!phaseStart.equals(phaseEnd)) {

                            /* Work out which of the bodies is the child body, and then establish the
                             * journey distance as the orbit of the child body. */
                            double distance;
                            if (startPlanet.isChild(endPlanet)) {
                                enterprise.setParent(startPlanet.getParent());
                                distance = startPlanet.getOrbit();
                            } else {
                                enterprise.setParent(endPlanet.getParent());
                                distance = endPlanet.getOrbit();
                            }

                            // Create a new Ellipse to get the data for the path between the bodies
                            MathEllipse transferPath = new MathEllipse(currentParent.getMass(), 0, distance);

                            /* If the width of the path is 0, then set it to the width of the displayed body on screen
                             * for reasonable accuracy with a real journey */
                            double pathWidth = transferPath.semiMinor();
                            if (transferPath.semiMinor() == 0) {
                                pathWidth = currentParent.getGUIObject().getRadius() * 2 / SCREEN_SCALE;
                            }

                            // Set up the spacecraft's path to match the distance between bodies
                            enterprise.setRadius(pathWidth * SCREEN_SCALE, transferPath.semiMajor() * SCREEN_SCALE);

                            /* Work out the mid point between the parent and the child body at the starting
                             * angle of the journey, and set the journey path to centre at this point */
                            double journeyMoveX = enterprise.getParent().getX() + (enterprise.getRadiusY() *
                                    Math.sin(drawAngle));
                            double journeyMoveY = enterprise.getParent().getY() - (enterprise.getRadiusY() *
                                    Math.cos(drawAngle));

                            enterprise.setCenterPoint(journeyMoveX, journeyMoveY);

                            // Rotate the path according the angle at which the path is triggered to be displayed
                            enterprise.setPathRotation(Math.toDegrees(drawAngle));

                            /* Use the calculated transfer time to set the period that the spacecraft should take
                             * to make one full orbit of it's path. Ensures the spacecraft meets it's destination at
                             * the end of the path.  */
                            enterprise.setPeriod(2 * (calc.getTime() / 86400));

                        }

                        // Set the angle of the spacecraft to match the starting angle of the journey
                        enterprise.setAngle(startAngle);

                        // Set the title of the route display to match the current transition
                        routeStage.setText(startPlanet.getName() + " > " + endPlanet.getName());

                        // Get the transfer data string from the Calculator
                        String data = calc.getTransferData();

                        // Format the data string for display in the side panel and then add it to this panel
                        String newData = data.replace("\t", "\n");
                        routeData.setText(newData.replace("days ", "days\n"));

                        // Add information about the start point of this transition to the journey data string
                        completeJourneyData.append(startPlanet.getName()).append(" ");
                        if (startStage.isLanded()) {
                            completeJourneyData.append("Surface");
                        } else {
                            completeJourneyData.append(String.format("%.0fkm/%.0fkm", startStage.getApoapsis(),
                                    startStage.getPeriapsis()));
                        }

                        // Add information about the end point of this transition to the journey data string
                        completeJourneyData.append(" > ").append(endPlanet.getName()).append(" ");
                        if (endStage.isLanded()) {
                            completeJourneyData.append("Surface");
                        } else {
                            completeJourneyData.append(String.format("%.0fkm/%.0fkm", endStage.getApoapsis(),
                                    endStage.getPeriapsis()));
                        }

                        // Add the transition data to the journey data string
                        completeJourneyData.append("\n\t").append(calc.getTransferData());

                        // Add the transition time to the total journey time
                        totalJourneyTime += calc.getTime();

                        // Finished processing a new step, so indicate this in the boolean
                        newStepMain = false;

                    } else {
                        /* Currently making a transition, so simply increment the angle of the spacecraft
                         * along it's path by the current speed factor. */
                        enterprise.incrementAngle(SPEED_FACTOR);
                    }

                    /* If we are moving to a new body, check if we have reached the end point of the transition. If
                     * not, then move the spacecraft so it mirrors the current body's position. */
                    if (!phaseStart.equals(phaseEnd)) {
                        if (Math.abs(Math.toDegrees(enterprise.getAngle() - endAngle)) < 1) {
                            nextPhase();
                        }
                    } else {
                        enterprise.setCenterPoint(startPlanet.getX(), startPlanet.getY());
                    }
                }
                // Even step thus we are currently orbiting a single body
                else {

                    // If we are on a new step, then set up the display with data about the new orbit
                    if (newStepMain) {

                        /* Set the text on the sidebar to display the name of the current planet, as well as
                         * data regarding the current orbit. */
                        routeStage.setText(startPlanet.getName() + " Orbit");
                        routeData.setText("Apoapsis = " + startStage.getApoapsis() + "km\nPeriapsis = "
                                + startStage.getPeriapsis() + "km");

                        /* As long as there is another planet on the journey to move to, then set up the calculator
                         * with information about the transfer, and set a variable to indicate we are waiting for a
                         * window to make the transfer. */
                        if (!(phaseEnd.isEmpty())) {

                            MathEllipse transfer = null;
                            if (!(endStage.isLanded())) {
                                /* As long as we are not landing on the planet, convert the inputted distances
                                 * into metres and create an ellipse */
                                double r1 = endPlanet.getRadius() + 1.0e3 * endStage.getApoapsis();
                                double r2 = endPlanet.getRadius() + 1.0e3 * endStage.getPeriapsis();

                                transfer = new MathEllipse(endPlanet.getMass(), r2, r1);
                            }

                            /* If we are moving to a new planet then we need to search for the correct phase
                             * angle so that the spacecraft will meet the planet. */
                            if (!startPlanet.getName().equals(endPlanet.getName())) {
                                transferWindow = true;
                                orbitsSearch = 0;
                            } else {
                                transferWindow = false;
                            }

                            // Get the calculator to make the transfer to the new orbit
                            calc.transfer_slow(endPlanet, transfer);
                        }

                        // Orbiting a planet, so in this view just display the spacecraft at the same point.
                        enterprise.setRadius(0, 0);

                        // Finished processing information about the stage, so set to false
                        newStepMain = false;
                    }

                    if (transferWindow && !(phaseEnd.isEmpty())) {

                        if (startPlanet.isSibling(endPlanet)) {

                            /* For a sibling transfer, increment the counter of how long we have been searching for a
                             * transfer window by the speed factor. This is so we can track the effective distance
                             * travelled in this time. */
                            orbitsSearch += SPEED_FACTOR;

                            /* Work out the phase angle between the start and end planet. This should always be positive,
                             * so add a full circle if it is negative.*/
                            double phase = Math.toDegrees(endPlanet.getAngle() - startPlanet.getAngle());
                            if (phase < 0) {
                                phase += 360.0;
                            }

                            /* If the current phase angle matches the desired phase worked out by the calculator,
                             * then we can begin the transfer. */
                            if (Math.abs(phase - calc.getStartPhaseAngle()) < 1) {

                                /* Use the orbit search counter to work out approximately how long the spacecraft
                                 * waited to make a transfer, and then add this to the final journey data string. */
                                double angleTravelled = orbitsSearch * Math.toRadians(startPlanet.getAngularV());
                                double timeTaken = startPlanet.getPeriodAsSeconds() * (angleTravelled / 360);

                                completeJourneyData.append(startPlanet.getName()).append("\n\t");
                                completeJourneyData.append("Time In Orbit = ").append(timeToString(timeTaken));

                                totalJourneyTime += timeTaken;

                                // Journey is always made from 0 to 180 degrees around the transfer orbit
                                startAngle = 0;
                                endAngle = Math.PI;

                                /* Get the angle at which the orbit needs to be drawn on the view. Centre point
                                 * should be directly opposite the start planet, so subtract 180 degrees and increment
                                 * to ensure the angle is positive. */
                                drawAngle = startPlanet.getAngle() - Math.PI;
                                if (drawAngle < 0) {
                                    drawAngle += 2 * Math.PI;
                                }

                                /* Get the angle that the destination body will move through whilst waiting for the
                                 * spacecraft to arrive. Get the difference between the current position and the end
                                 * point of the spacecraft's path, and add a number of full orbits based on the
                                 * calculated journey time. */
                                transAngle = drawAngle - endPlanet.getAngle();
                                if (transAngle < 0) {
                                    transAngle += 2 * Math.PI;
                                }
                                double completedOrbits = Math.floor(calc.getTime() / endPlanet.getPeriodAsSeconds());
                                transAngle += completedOrbits * 2 * Math.PI;

                                /* Have all the necessary information to make the transfer, so move to the next
                                 * phase of the journey */
                                nextPhase();
                            }
                        } else {
                            /* For parent/child transfers, wait until the focus view spacecraft has reached an angle
                             * of 90 degrees. This ensures the animation for the focus view runs smoothly, as all
                             * transitions from previous stages must be completed. */
                            if (Math.abs(Math.toDegrees(falcon.getAngle()) - 90) < 1) {

                                /* Work out if we are going to or from a child body. If we are going to the
                                 * child body, then work out how far the body will have moved in the time it takes for
                                 * the spacecraft to make the transfer. */
                                BodyInSpace childPlanet;
                                double angleCovered = 0;
                                if (startPlanet.isChild(endPlanet)) {
                                    childPlanet = startPlanet;
                                } else {
                                    childPlanet = endPlanet;

                                    // Get the transfer time in days and work out the angle covered in this time
                                    double transferTime = (calc.getTime() / 86400);
                                    angleCovered = Math.toRadians(childPlanet.getAngularV() * transferTime);
                                }

                                /* The angle at which the transfer orbit should be drawn. Accounts for the movement
                                 * of the body and ensures the spacecraft will meet it if appropriate. */
                                drawAngle = childPlanet.getAngle() + angleCovered;
                                while (drawAngle < 0) {
                                    drawAngle += 2 * Math.PI;
                                }

                                /* Spacecraft always moves around half of the orbit circle. Work out which half based
                                 * on the direction of the transfer. */
                                if (!outwardMovement || childPlanet.equals(startPlanet)) {
                                    startAngle = 0;
                                    endAngle = Math.PI;
                                } else {
                                    startAngle = Math.PI;
                                    endAngle = 0;
                                }

                                // We have all the information to make the transfer, so move to next stage of the journey
                                nextPhase();
                            }
                        }
                    }

                    /* Need to move the spacecraft so that it matches the body it is orbiting, so relocate
                     * its centre point so that it mirrors the body */
                    enterprise.setCenterPoint(startPlanet.getX(), startPlanet.getY());

                }

                // Work out the x & y co-ordinates that correspond to the angle of the spacecraft around it's orbit path.
                double moveX = enterprise.getCenterX() + (enterprise.getRadiusX()) *
                        Math.sin(enterprise.getAngle());
                double moveY = enterprise.getCenterY() - (enterprise.getRadiusY()) *
                        Math.cos(enterprise.getAngle());

                // Get the center point of the spacecraft's orbit
                double centreX = enterprise.getCenterX();
                double centreY = enterprise.getCenterY();

                /* Rotate the x & y co-ordinates axis by the same number of degrees as the orbital path is rotated.
                 * Thus the spacecraft will lie on the tilted orbit */
                double rotatedX = (moveX - centreX) * Math.cos(enterprise.getPathRotation())
                        - (moveY - centreY) * Math.sin(enterprise.getPathRotation()) + centreX;

                double rotatedY = (moveX - centreX) * Math.sin(enterprise.getPathRotation())
                        + (moveY - centreY) * Math.cos(enterprise.getPathRotation()) + centreY;

                // Move the spacecraft to the new position
                moveBall(enterprise.getGUIShip(), rotatedX, rotatedY);
            }
        };


        EventHandler<ActionEvent> focusMovement = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                // Intermediate step for every item in the list, so divide by two to get the planet references
                int planetIndex = steps / 2;

                // Retrieve the starting planet of this stage and style the body in the focus pane for the start point
                String phaseStart = planetsOnPath.get(planetIndex).getBody();
                RouteStage startStage = planetsOnPath.get(planetIndex);
                BodyInSpace startPlanet = SpaceObjects.getBody(phaseStart);
                planetFocus.getStyleClass().add("body-" + phaseStart);

                // If we are not at the end of the journey, get the destination planet of this journey stage
                String phaseEnd;
                RouteStage endStage;
                if (planetsOnPath.size() > planetIndex + 1) {
                    phaseEnd = planetsOnPath.get(planetIndex + 1).getBody();
                    endStage = planetsOnPath.get(planetIndex + 1);
                } else {
                    phaseEnd = "";
                    endStage = new RouteStage("null", 0, 0);
                }

                // Reference to the width of the focus panel
                double focusWidth = sourcePane.getPrefWidth() / 2;
                double focusHeight = sourcePane.getPrefHeight() / 2;

                // If we are transferring between two different orbits of the same body
                if (phaseStart.equals(phaseEnd) && steps % 2 == 1) {
                    // For a new step which has not been processed
                    if (newStepFocus) {
                        // Set the parent of the falcon to the new body
                        falcon.setParent(startPlanet);

                        // Work out the ellipse for the periapsis of the end orbit to the apoapsis of the start orbit
                        MathEllipse transferPath = new MathEllipse(currentParent.getMass(), endStage.getPeriapsis(),
                                startStage.getApoapsis());

                        /* If the width of the path is 0, then draw the ellipse with a fixed width corresponding to
                         * the size of the GUI object representing the body. */
                        double pathWidth = transferPath.semiMinor();
                        if (transferPath.semiMinor() == 0) {
                            pathWidth = planetFocus.getRadius() * 2;
                        }
                        falcon.setRadius(transferPath.semiMajor() * focusScale, pathWidth * focusScale);

                        /* Set the centre point of the ellipse so that the body is at the focus of
                         * the ellipse, not the centre. */
                        double transferRadius = (endStage.getPeriapsis() + startStage.getApoapsis()) / 2;
                        falcon.setCenterPoint(focusWidth - ((transferRadius - startStage.getApoapsis()) * focusScale),
                                focusHeight);

                        // Ensure period is set to the default
                        falcon.setPeriod(DEFAULT_FALCON_PERIOD);

                        // Finished processing the new step, so set to false
                        newStepFocus = false;
                    }

                    /* Increment the angle of the spacecraft around its orbit by a constant - does not react to speed
                     * factor changes. */
                    falcon.incrementAngle(1);

                    // Work out the new position of the spacecraft based on its new angle
                    double moveX = falcon.getCenterX() + falcon.getRadiusX() * Math.sin(falcon.getAngle());
                    double moveY = falcon.getCenterY() - falcon.getRadiusY() * Math.cos(falcon.getAngle());

                    // Move the spacecraft to its new position
                    moveBall(falcon.getGUIShip(), moveX, moveY);

                    // When the spacecraft has reached 270 degrees, the transfer is complete, so move to the next phase
                    if (Math.abs(Math.toDegrees(falcon.getAngle()) - 270) < 1) {
                        nextPhase();
                    }
                }
                // If we are making a transfer between two different bodies, or currently transitioning between orbits
                else if (steps % 2 == 1 || transferringOrbits) {

                    /* If we have an unhandled new step, the journey is not complete, and the last transfer is
                     * finished then update the end orbit and mark the route for transfer. */
                    if (newStepFocus && !phaseEnd.isEmpty()) {
                        transferComplete = false;
                        transOrb1 = endStage.getApoapsis();
                        transOrb2 = endStage.getPeriapsis();

                        /* As long as the new step has been processed in the main view, use the transfer period to
                         * change the speed of the focus spacecraft. This ensures the departure and arrival animations
                         * have time to play if the transfer is fast. */
                        if (!newStepMain) {
                            double angleToTravel = Math.toDegrees(falcon.getAngle()) - 90;
                            if (angleToTravel < 360) { angleToTravel += 360; }

                            double period = ( angleToTravel / 360) * (enterprise.getPeriod() / 8 / SPEED_FACTOR);
                            if (period > DEFAULT_FALCON_PERIOD) { period = DEFAULT_FALCON_PERIOD; }
                            falcon.setPeriod(period);

                            //LOGGER.info("FALCON PERIOD: " + period);
                            newStepFocus = false;
                        }
                    }

                    /* If the counter for the inward motion is below the midpoint, then the transfer is complete, so
                     * update the variables to show this, hide the transfer line and reset the counters. */
                    if (yPositionIn < 100) {
                        transferringOrbits = false;
                        transferComplete = true;
                        entryLine.setStroke(Color.TRANSPARENT);
                        yPositionIn = 200;
                        yPositionOut = 100;
                        falcon.setPeriod(DEFAULT_FALCON_PERIOD);
                    }

                    /* When the spacecraft reaches the 90 degrees position and there is a transfer ongoing, move the
                     * spacecraft along the line to make it leave its current orbit. */
                    if (Math.abs(Math.toDegrees(falcon.getAngle()) - 90) < 1 && yPositionOut < 205 && !transferComplete) {
                        // Make the transfer line visible
                        entryLine.setStroke(Color.ORANGE);

                        // Indicate we need to handle the move on re-entry and that we are currently making a transfer
                        newMove = true;
                        transferringOrbits = true;

                        // Move the line to the right edge of the starting orbit
                        entryLine.setStartX(startStage.getApoapsis() * focusScale + focusWidth);
                        entryLine.setEndX(startStage.getApoapsis() * focusScale + focusWidth);

                        // Increment the spacecraft along the exit line, and move to its new position
                        yPositionOut += 0.5;
                        moveBall(falcon.getGUIShip(), entryLine.getStartX(), yPositionOut);

                    } else if (((Math.abs(Math.toDegrees(enterprise.getAngle() - endAngle)) < 10 && yPositionIn > 100 && !transferComplete)
                            || yPositionIn != 200) && transferringOrbits) {
                        if (newMove) {
                            // Create an ellipse object to calculate the radii of the new orbit
                            MathEllipse orbit = new MathEllipse(startPlanet.getMass(), transOrb1, transOrb2);
                            falcon.setRadius(orbit.semiMajor() * focusScale, orbit.semiMinor() * focusScale);

                            /* Calculate an offset in order to place the planet at the focus of the ellipse
                             * instead of the centre */
                            double offset = (transOrb1 - transOrb2) / 2;
                            falcon.setCenterPoint(focusWidth + (offset * focusScale), focusHeight);

                            /* Set the angle of the spacecraft to 270 as this is the point at
                             * which it will rejoin the orbit path */
                            falcon.setAngle(Math.toRadians(270));

                            // Restyle the planet in the focus pane to match the destination
                            planetFocus.getStyleClass().add("body-" + phaseEnd);

                            // Move the line entering the new orbit to the left edge of the orbit
                            entryLine.setStartX(transOrb2 * -1 * focusScale + focusWidth);
                            entryLine.setEndX(transOrb2 * -1 * focusScale + focusWidth);

                            // Finished processing the new orbit data, so mark this in the boolean
                            newMove = false;
                        }

                        /* Decrement the counter to move the spacecraft up the entry line, and move the
                         * spacecraft to the new position */
                        yPositionIn -= 0.5;
                        moveBall(falcon.getGUIShip(), entryLine.getStartX(), yPositionIn);
                    }
                    /* If we are not currently transferring between orbits, then we need to move the spacecraft
                     * around the orbit as previously, so that it will eventually reach a transfer point. */
                    else if (!transferringOrbits) {
                        /* Increment the angle of the spacecraft around its orbit by a constant - does not react to speed
                         * factor changes. */
                        falcon.incrementAngle(1);

                        // Work out the new position of the spacecraft based on its new angle
                        double moveX = falcon.getCenterX() + falcon.getRadiusX() * Math.sin(falcon.getAngle());
                        double moveY = falcon.getCenterY() - falcon.getRadiusY() * Math.cos(falcon.getAngle());

                        // Move the spacecraft to its new position
                        moveBall(falcon.getGUIShip(), moveX, moveY);
                    }
                }
                // We therefore must be in an orbit stage of the journey
                else {
                    /* As long as the spacecraft is not landed on the planet, then work out the elliptical path for
                     * the orbit using a MathEllipse object, and center it so that the planet object is the focus of
                     * the ellipse, not the centre. */
                    if (!startStage.isLanded()) {
                        MathEllipse orbit = new MathEllipse(startPlanet.getMass(), startStage.getApoapsis(),
                                startStage.getPeriapsis());

                        falcon.setRadius(orbit.semiMajor() * focusScale, orbit.semiMinor() * focusScale);

                        double offset = ((startStage.getApoapsis() - startStage.getPeriapsis()) / 2) * focusScale;
                        falcon.setCenterPoint((sourcePane.getPrefWidth() / 2) + offset, sourcePane.getPrefHeight() / 2);

                    } else {
                        /* If we are landed on the planet, then set the radius of the spacecraft's path to 0 and
                         * center it on the planet. */
                        falcon.setRadius(0, 0);
                        falcon.setCenterPoint(planetFocus.getCenterX(), planetFocus.getCenterY());
                    }

                    /* If the next transition moves to a new orbit around the same body, then trigger this when
                     * the spacecraft reaches an angle of 90 degrees around the body in the focus panel. Also use this
                     * position to finish the journey if we are at the end of the journey. */
                    if (Math.abs(Math.toDegrees(falcon.getAngle()) - 90) < 1 &&
                            (phaseStart.equals(phaseEnd) || phaseEnd.isEmpty())) {
                        nextPhase();
                    }

                    // Ensure period is set to the default
                    falcon.setPeriod(DEFAULT_FALCON_PERIOD);

                    /* Increment the angle of the spacecraft around its orbit by a constant - does not react to speed
                     * factor changes. */
                    falcon.incrementAngle(1);

                    // Work out the new position of the spacecraft around its orbit from this new angle.
                    double moveX = falcon.getCenterX() + falcon.getRadiusX() * Math.sin(falcon.getAngle());
                    double moveY = falcon.getCenterY() - falcon.getRadiusY() * Math.cos(falcon.getAngle());

                    // Move the spacecraft to this new location
                    moveBall(falcon.getGUIShip(), moveX, moveY);
                }
            }
        };


        // Set up a timeline with the various event handles to display the animation
        timeline = new Timeline(new KeyFrame(Duration.ZERO, planetMovement),
                new KeyFrame(Duration.ZERO, spaceshipMove),
                new KeyFrame(Duration.ZERO, focusMovement),
                new KeyFrame(Duration.millis(STEP_DURATION)));
        timeline.setCycleCount(Timeline.INDEFINITE);

        // Add the parent object to the display
        systemPane.getChildren().add(currentParent.getGUIObject());

        // Add all the bodies and their orbits to the display
        for (BodyInSpace current : childBodies.values()) {
            current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
            systemPane.getChildren().add(current.getGUIOrbit());
            systemPane.getChildren().add(current.getGUIObject());
        }

        // Add the spaceship for the main view to the display
        systemPane.getChildren().add(enterprise.getGUITrail());
        systemPane.getChildren().add(enterprise.getGUIShip());

        // Add the spaceship, transition line and central planet to the focus window
        sourcePane.getChildren().add(falcon.getGUITrail());
        sourcePane.getChildren().add(falcon.getGUIShip());
        sourcePane.getChildren().add(entryLine);
        sourcePane.getChildren().add(planetFocus);

        // Run the animation
        timeline.play();
    }

    /**
     * Internal method to animate the movement of a body from one location to the next.
     * @param ball The GUI object to be moved
     * @param x The x position to move this object to
     * @param y The y position to move this object to
     */
    private void moveBall(Shape ball, double x, double y) {
        TranslateTransition move = new TranslateTransition(
                Duration.millis(STEP_DURATION), ball);
        move.setToX(x);
        move.setToY(y);
        move.playFromStart();
    }

    /**
     * Set the current display scale to the given value. Checks the
     * scale is within the bounds for the current planet scale.
     * @param new_val scale value
     */
    private void setScale(double new_val) {
        // Get the minimum and maximum values
        double min_value = SpaceObjects.getScale(currentParent.getName()).get(0);
        double max_value = SpaceObjects.getScale(currentParent.getName()).get(1);

        // Set the scale to its minimum or maximum if it is outwith the bounds
        double val;
        if (new_val > max_value) {
            val = max_value;
        } else if (new_val < min_value) {
            val = min_value;
        } else {
            val = new_val;
        }

        // Set the scale to the value, and reset all the displayed orbits to match this new scale
        SCREEN_SCALE = val;
        for (BodyInSpace current : childBodies.values()) {
            current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
        }
    }

    /**
     * Method to move the animation on to the next stage of the journey - also handles displaying the completion dialog
     * when the journey is finished
     */
    private void nextPhase() {
        /* If there is a new stage to move on to, then increment the
         * counter and indicate that we are on a new stage */
        if (steps / 2 < planetsOnPath.size() - 1) {
            steps++;
            newStepMain = true;
            newStepFocus = true;
        } else {
            /* When there are no stages left, pause the animation, show the completion
             * dialog and show the journey data in the dialog. */
            timeline.pause();
            completionPane.toFront();

            // Add the total journey time to the data string
            completeJourneyData.append("\r\nTotal Journey Time\n\t");
            completeJourneyData.append(timeToString(totalJourneyTime));

            journeyInfo.setText(completeJourneyData.toString());
            LOGGER.info("Journey complete");
        }
    }

    /**
     * Exit the program on button press
     * @throws IOException
     */
    @FXML protected void quitProgram() throws IOException {
        Platform.exit();
    }

    /**
     * Save the data about the journey made into a file. Uses a file picker to create a
     * text file in the specified location.
     * @throws IOException
     */
    @FXML protected void saveJourney() throws IOException {

        //Establish a file chooser
        FileChooser fileChooser = new FileChooser();

        // Set file extension filter - output saved as text file
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        // Show save file dialog for the user
        Stage stage = (Stage) routeStage.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        // As long as the file specified can be opened, write the journey data string to the file
        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(completeJourneyData.toString());
                fileWriter.close();
            } catch (IOException ex) {
                System.err.println("Could not save journey information");
            }
        }
    }

    /**
     * Decreases the current movement speed of the system, caps speed at x1/64 and disables button if at cap
     */
    @FXML protected void slowMovement() {
        if (SPEED_FACTOR > 0.015625) {
            SPEED_FACTOR = SPEED_FACTOR / 2;
        }

        if (SPEED_FACTOR == 0.015625) {
            slowButton.setDisable(true);
            speedButton.setDisable(false);
        } else if (SPEED_FACTOR == 512) {
            slowButton.setDisable(false);
            speedButton.setDisable(true);
        } else {
            slowButton.setDisable(false);
            speedButton.setDisable(false);
        }
    }

    /**
     * Increases the current movement speed of the system, caps speed at x512 and disables button if at cap
     */
    @FXML protected void speedUpMovement() {
        if (SPEED_FACTOR < 512) {
            SPEED_FACTOR = SPEED_FACTOR * 2;
        }

        if (SPEED_FACTOR == 0.015625) {
            slowButton.setDisable(true);
            speedButton.setDisable(false);
        } else if (SPEED_FACTOR == 512) {
            slowButton.setDisable(false);
            speedButton.setDisable(true);
        } else {
            slowButton.setDisable(false);
            speedButton.setDisable(false);
        }
    }

    /**
     * Reset the speed factor to its original value, enable both buttons
     */
    @FXML protected void resetMovement() {
        // Adjust the speed back to its initial for the given frame
        if (currentParent.getName().equals("Sun")) {
            SPEED_FACTOR = 1;
        } else {
            SPEED_FACTOR = 0.25;
        }

        speedButton.setDisable(false);
        slowButton.setDisable(false);
    }

    /**
     * Converts a time in seconds into a string which represents the time in days, hours, minutes and seconds
     * @param t time in seconds
     * @return time string
     */
    private String timeToString(double t) {
        double days = Math.floor(t / 86400);
        double hours = Math.floor((t % 86400) / 3600);
        double minutes = Math.floor(((t % 86400) % 3600) / 60);
        double seconds = Math.floor(((t % 86400) % 3600) % 60);

        return String.format("%6.0f days %6.0f hours %6.0f mins %6.0f s\n", days, hours, minutes, seconds);
    }

    /**
     * Method to update the current display frame to the one specified.
     * @param parent Name of the body at the centre of the frame
     * @param spaceship Spaceship to readd to the main display
     */
    private void changeFrame(String parent, Spaceship spaceship) {

        // If the current parent is the one specified, return as no changes needed
        if (parent.equals(currentParent.getName())) {
            return;
        }

        /* If the parent is the sun, the retrieve the sun and planet objects, and set the speed to the default.
         * Otherwise retrieve the children of the planet and give it a lower speed factor to account for the speed
         * of the satellites. */
        if (parent.equals("Sun")) {
            currentParent = SpaceObjects.getSun();
            childBodies = SpaceObjects.getPlanets();
            SPEED_FACTOR = 1;
        } else {
            currentParent = SpaceObjects.getPlanets().get(parent);
            childBodies = SpaceObjects.getChildren(parent);
            SPEED_FACTOR = 0.25;
        }


        // Clear all objects from the display
        systemPane.getChildren().clear();

        // Reset the current parent body and then redraw it at the centre of the available space
        currentParent.resetPlanet();
        systemPane.getChildren().add(currentParent.getGUIObject());
        currentParent.moveGUIObject(systemPane.getPrefWidth() / 2, systemPane.getPrefHeight() / 2);

        /* For the child bodies, adjust their orbits to the screen scale, add the orbit to the
         * display, and then reset the planets and draw them. Two seperate loops used to ensure the planets
         * always display on top of the orbit circles. */
        if (!childBodies.isEmpty()) {
            for (BodyInSpace current : childBodies.values()) {
                current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
                systemPane.getChildren().add(current.getGUIOrbit());
            }

            for (BodyInSpace current : childBodies.values()) {
                current.resetPlanet();
                systemPane.getChildren().add(current.getGUIObject());
            }

        }

        /* Readd the spaceship and the spaceship path to the display. */
        systemPane.getChildren().add(spaceship.getGUITrail());
        systemPane.getChildren().add(spaceship.getGUIShip());
    }

    /**
     * Method to switch back to the Path Selection view
     * @throws IOException
     */
    @FXML protected void mapJourney() throws IOException {
        // Use an element on screen on to get a reference to the window
        Stage stage;
        Parent root;
        stage = (Stage) routeStage.getScene().getWindow();

        // Empty the route list and reset the screen scale for displaying the path
        planetsOnPath.clear();
        SCREEN_SCALE = SpaceObjects.getScale("Sun").get(0);

        // Load the XML file for the Path Selector into a scene
        root = FXMLLoader.load(getClass().getResource("/solarsystem/resources/xml/pathselect.fxml"));
        Scene scene = new Scene(root);

        // Replace the current window contents with the Path Selector
        stage.setScene(scene);
        stage.show();
    }

    /**
     * Method to switch to the Solar System animation view
     * @throws IOException
     */
    @FXML protected void viewAnimation() throws IOException {
        // Use an element on screen on to get a reference to the window
        Stage stage;
        Parent root;
        stage = (Stage) routeStage.getScene().getWindow();

        // Empty the route list as the journey is now complete
        planetsOnPath.clear();

        // Load the XML file for the System Animation into a scene
        root = FXMLLoader.load(getClass().getResource("/solarsystem/resources/xml/system.fxml"));
        Scene scene = new Scene(root);

        // Replace the current window contents with the System Animation
        stage.setScene(scene);
        stage.show();
    }
}
