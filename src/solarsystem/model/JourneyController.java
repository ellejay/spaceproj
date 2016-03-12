package solarsystem.model;

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
    private int steps = 0;
    private boolean newStep = true;
    private boolean transferWindow = false;
    private double startAngle, endAngle, transAngle, drawAngle;
    private final StringBuilder completeJourneyData = new StringBuilder();
    private double totalJourneyTime;
    private double orbitsSearch;
    private double focusScale;
    private double incOut = 100;
    private double incIn = 200;
    private double transOrb1, transOrb2;
    private boolean orbitTrans, newMove, transferComplete;
    private double lineIn;
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
		 * increment their current angle by the speed factor, work out the x and y co-ords on their orbit circle
		 * that correspond to this angle, and then move the planet to this location. */
        EventHandler<ActionEvent> planetMovement = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                for (BodyInSpace current : childBodies.values()) {

                    // Increment the angle of the planet by the speed factor
                    current.incrementAngle(SPEED_FACTOR);

                    /* Work out the x & y co-ords that correspond to the new angle. Multiply by the screen scale
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
        final Calculator calc = new Calculator(SpaceObjects.getBody(routePlanets.get(0)));

        // Set up two spaceship instances - one for the main planet view and one for the focus view
        final Spaceship enterprise = new Spaceship();
        final Spaceship falcon = new Spaceship();

        /* Using the Falcon for the focus view, so set the centre point of this spaceship to
         * the centre of the focus pane and the radius to 0, as we always start from a land. */
        falcon.setCenterPoint(planetFocus.getCenterX(), planetFocus.getCenterY());
        falcon.setRadius(0, 0);

        // Find the largest orbit distance in the route list
        double maxOrbit = Double.MIN_VALUE;
        for (double[] orbit : routeOrbit) {
            for (double val : orbit) {
                if (val > maxOrbit) {
                    maxOrbit = val;
                }
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

                int planetIndex = steps / 2;
                boolean outwardMovement = true;

                BodyInSpace startPlanet, endPlanet = null;
                String phaseStart = routePlanets.get(planetIndex);
                double[] startOrbit = routeOrbit.get(planetIndex);
                String phaseEnd;
                double[] endOrbit = null;

                startPlanet = SpaceObjects.getBody(phaseStart);

                try {
                    phaseEnd = routePlanets.get(planetIndex + 1);
                    endOrbit = routeOrbit.get(planetIndex + 1);

                    endPlanet = SpaceObjects.getBody(phaseEnd);

                    if ((startPlanet.isSibling(endPlanet) && startPlanet.getOrbit() > endPlanet.getOrbit())
                            || (startPlanet.isChild(endPlanet))) {
                        outwardMovement = false;
                        setScale(285 / startPlanet.getOrbit());
                    } else {
                        setScale(285 / endPlanet.getOrbit());
                    }
                } catch (IndexOutOfBoundsException e) {
                    phaseEnd = "";
                }

                // Reset the currently displayed frame based on the next transition in the journey
                if (newStep && !phaseEnd.isEmpty()) {
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
                    if (newStep) {
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

                            /* Get the angular velocity of the most distant planet and use this to calculate the
                             * period required so that the spacecraft will meet the destination body at the end of
                             * its orbit. */
                            enterprise.setPeriod(360 / ((furthestPlanet.getAngularV() * Math.PI) / (transAngle)));

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
                        if (startOrbit[0] == 0 && startOrbit[1] == 0) {
                            completeJourneyData.append("Surface");
                        } else {
                            completeJourneyData.append(String.format("%.0fm/%.0fm", startOrbit[0], startOrbit[1]));
                        }

                        // Add information about the end point of this transition to the journey data string
                        completeJourneyData.append(" > ").append(endPlanet.getName()).append(" ");
                        if (endOrbit[0] == 0 && endOrbit[1] == 0) {
                            completeJourneyData.append("Surface");
                        } else {
                            completeJourneyData.append(String.format("%.0fm/%.0fm", endOrbit[0], endOrbit[1]));
                        }

                        // Add the transition data to the journey data string
                        completeJourneyData.append("\n\t").append(calc.getTransferData());

                        // Add the transition time to the total journey time
                        totalJourneyTime += calc.getTime();

                        // Finished processing a new step, so indicate this in the boolean
                        newStep = false;

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

                    if (newStep) {
                        newStep = false;

                        routeStage.setText(startPlanet.getName() + " Orbit");
                        routeData.setText("Apoapsis = " + startOrbit[0] + "km\nPeriapsis = " + startOrbit[1] + "km");

                        if (!outwardMovement) {
                            enterprise.setAngle(3.14);
                        }

                        if (!(phaseEnd.isEmpty())) {
                            MathEllipse transfer = null;
                            if (!(endOrbit[0] == 0 && endOrbit[1] == 0)) {
                                double r1 = endPlanet.getRadius() + 1.0e3 * endOrbit[0];
                                double r2 = endPlanet.getRadius() + 1.0e3 * endOrbit[1];

                                transfer = new MathEllipse(endPlanet.getMass(), r2, r1);
                            }

                            if (!startPlanet.getName().equals(endPlanet.getName())) {
                                transferWindow = true;
                                orbitsSearch = 0;
                            } else {
                                transferWindow = false;
                            }

                            calc.transfer_slow(endPlanet, transfer);

                        }
                    }

                    if (transferWindow && !(phaseEnd.isEmpty())) {

                        if (startPlanet.isSibling(endPlanet)) {

                            orbitsSearch++;

                            double phase = Math.toDegrees(endPlanet.getAngle() - startPlanet.getAngle());
                            if (phase < 0) {
                                phase += 360.0;
                            }

                            if (Math.abs(phase - calc.getStartPhaseAngle()) < 1) {

                                double angleTravelled = orbitsSearch * Math.toRadians(startPlanet.getAngularV());

                                double timeTaken = startPlanet.getPeriodAsSeconds() *
                                        (angleTravelled / 360);

                                completeJourneyData.append(startPlanet.getName()).append("\n\t");
                                completeJourneyData.append("Time In Orbit = ").append(timeToString(timeTaken));

                                totalJourneyTime += timeTaken;

                                //startAngle = startPlanet.getAngle();
                                startAngle = 0;

                                endAngle = startPlanet.getAngle() - Math.PI;
                                if (endAngle < 0) {
                                    endAngle += 2 * Math.PI;
                                }

                                drawAngle = endAngle;
                                transAngle = endAngle - endPlanet.getAngle();

                                if (transAngle < 0) {
                                    transAngle += 2 * Math.PI;
                                }

                                endAngle = Math.PI;

                                nextPhase();
                            }
                        } else {

                            if (Math.abs(Math.toDegrees(falcon.getAngle()) - 90) < 1) {
                                BodyInSpace childPlanet;
                                double angleCovered = 0;
                                if (startPlanet.isChild(endPlanet)) {
                                    childPlanet = startPlanet;
                                } else {
                                    childPlanet = endPlanet;
                                    double transferTime = (calc.getTime() / 86400);
                                    angleCovered = Math.toRadians(childPlanet.getAngularV() * transferTime);
                                }

                                endAngle = childPlanet.getAngle() + angleCovered;
                                while (endAngle < 0) {
                                    endAngle += 2 * Math.PI;
                                }

                                startAngle = endAngle - Math.PI;
                                if (startAngle < 0) {
                                    startAngle += 2 * Math.PI;
                                }

                                drawAngle = endAngle;
                                startAngle = Math.PI;
                                endAngle = 0;

                                if (!outwardMovement || childPlanet.equals(startPlanet)) {
                                    double temp = startAngle;
                                    startAngle = endAngle;
                                    endAngle = temp;
                                }

                                transAngle = childPlanet.getAngle() + (childPlanet.getAngularV() * (calc.getTime() / 24 / 60 / 60));

                                if (transAngle < 0) {
                                    transAngle += 2 * Math.PI;
                                }

                                nextPhase();

                            }
                        }
                    }

                    enterprise.setRadius(0, 0);

                    enterprise.incrementAngle(SPEED_FACTOR);

                    enterprise.setCenterPoint(startPlanet.getX(), startPlanet.getY());

                }

                // Work out the x & y co-ords that correspond to the angle of the spacecraft around it's orbit path.
                double moveX = enterprise.getCenterX() + (enterprise.getRadiusX()) *
                        Math.sin(enterprise.getAngle());
                double moveY = enterprise.getCenterY() - (enterprise.getRadiusY()) *
                        Math.cos(enterprise.getAngle());

                // Get the center point of the spacecraft's orbit
                double cx = enterprise.getCenterX();
                double cy = enterprise.getCenterY();

                /* Rotate the x & y co-ords axis by the same number of degrees as the orbital path is rotated.
                 * Thus the spacecraft will lie on the tilted orbit */
                double nmoveX = (moveX - cx) * Math.cos(enterprise.getPathRotation())
                        - (moveY - cy) * Math.sin(enterprise.getPathRotation()) + cx;

                double nmoveY = (moveX - cx) * Math.sin(enterprise.getPathRotation())
                        + (moveY - cy) * Math.cos(enterprise.getPathRotation()) + cy;

                // Move the spacecraft to the new position
                moveBall(enterprise.getGUIShip(), nmoveX, nmoveY);
            }
        };


        EventHandler<ActionEvent> focusMovement = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int planetIndex = steps / 2;
                int movement = 1;
                double focusWidth = sourcePane.getPrefWidth() / 2;

                BodyInSpace startPlanet, endPlanet;
                String phaseStart = routePlanets.get(planetIndex);
                double[] startOrbit = routeOrbit.get(planetIndex);
                String phaseEnd;
                double[] endOrbit = null;

                startPlanet = SpaceObjects.getBody(phaseStart);

                planetFocus.getStyleClass().add("body-" + phaseStart);

                try {
                    phaseEnd = routePlanets.get(planetIndex + 1);
                    endOrbit = routeOrbit.get(planetIndex + 1);

                    endPlanet = SpaceObjects.getBody(phaseEnd);

                    if (startPlanet.getOrbit() > endPlanet.getOrbit() || startPlanet.isChild(endPlanet)) {
                        movement = -1;
                    } else {
                        movement = 1;
                    }
                } catch (IndexOutOfBoundsException e) {
                    phaseEnd = "";
                }

                if (phaseStart.equals(phaseEnd) && steps % 2 == 1) {
                    double transferRadius = 0;
                    falcon.setParent(startPlanet);

                    transferRadius = (endOrbit[1] + startOrbit[0]) / 2;

                    MathEllipse transferPath = new MathEllipse(currentParent.getMass(),
                            endOrbit[1], startOrbit[0]);

                    if (newStep) {
                        startAngle = Math.PI / 2;
                        endAngle = Math.toRadians(180);
                    }


                    falcon.setCenterPoint(focusWidth - ((transferRadius - startOrbit[0]) * focusScale),
                            focusWidth);

                    double pathWidth = transferPath.semiMinor();
                    if (transferPath.semiMinor() == 0) {
                        pathWidth = planetFocus.getRadius() * 2;
                    }

                    falcon.setRadius(transferPath.semiMajor() * focusScale, pathWidth * focusScale);


                    falcon.incrementAngle(1);

                    double moveX = falcon.getCenterX() + (falcon.getRadiusX()) *
                            Math.sin(falcon.getAngle());

                    double moveY = falcon.getCenterY() - (falcon.getRadiusY()) *
                            Math.cos(falcon.getAngle());

                    moveBall(falcon.getGUIShip(), moveX, moveY);


                    if (Math.abs(Math.toDegrees(falcon.getAngle()) - 270) < 1) {
                        nextPhase();
                    }


                } else if (steps % 2 == 1 || orbitTrans && !phaseEnd.equals("")) {

                    if (newStep) {
                        transferComplete = false;
                        transOrb1 = endOrbit[0];
                        transOrb2 = endOrbit[1];
                    }


                    if (incIn < 100) {
                        orbitTrans = false;
                        transferComplete = true;
                        entryLine.setStroke(Color.TRANSPARENT);
                        incIn = 200;
                        incOut = 100;
                    }

                    if (Math.abs(Math.toDegrees(falcon.getAngle()) - 90) < 1 && incOut < 205 && !transferComplete) {
                        entryLine.setStroke(Color.ORANGE);
                        newMove = true;
                        orbitTrans = true;
                        lineIn = transOrb2 * -1;
                        entryLine.setStartX(startOrbit[0] * focusScale + focusWidth);
                        entryLine.setEndX(startOrbit[0] * focusScale + focusWidth);
                        incOut += 0.5;
                        moveBall(falcon.getGUIShip(), (startOrbit[0] * focusScale) + focusWidth, incOut);

                    } else if (((Math.abs(Math.toDegrees(enterprise.getAngle() - endAngle)) < 10 && incIn > 100 && !transferComplete)
                            || incIn != 200) && orbitTrans) {
                        if (newMove) {
                            MathEllipse orbit = new MathEllipse(startPlanet.getMass(), transOrb1, transOrb2);

                            double offset = (transOrb1 - transOrb2) / 2;

                            planetFocus.getStyleClass().add("body-" + phaseEnd);

                            falcon.setRadius(orbit.semiMajor() * focusScale, orbit.semiMinor() * focusScale);

                            falcon.setCenterPoint(focusWidth + (offset * focusScale), focusWidth);

                            falcon.setAngle(Math.toRadians(270));

                            newMove = false;
                        }
                        entryLine.setStartX(lineIn * focusScale + focusWidth);
                        entryLine.setEndX(lineIn * focusScale + focusWidth);
                        incIn -= 0.5;
                        moveBall(falcon.getGUIShip(), (lineIn * focusScale) + focusWidth, incIn);
                    } else if (!orbitTrans) {
                        falcon.incrementAngle(1);

                        double moveX = falcon.getCenterX() + (falcon.getRadiusX()) *
                                Math.sin(falcon.getAngle());

                        double moveY = falcon.getCenterY() - (falcon.getRadiusY()) *
                                Math.cos(falcon.getAngle());

                        moveBall(falcon.getGUIShip(), moveX, moveY);
                    }
                } else {
                    if (startOrbit[0] != 0 && startOrbit[1] != 0) {
                        MathEllipse orbit = new MathEllipse(startPlanet.getMass(), startOrbit[0], startOrbit[1]);

                        double offset = (startOrbit[0] - startOrbit[1]) / 2;

                        falcon.setRadius(orbit.semiMajor() * focusScale, orbit.semiMinor() * focusScale);

                        falcon.setCenterPoint(focusWidth + (offset * focusScale), focusWidth);

                    } else {
                        falcon.setRadius(0, 0);
                        falcon.setCenterPoint(planetFocus.getCenterX(), planetFocus.getCenterY());
                    }

                    if (Math.abs(Math.toDegrees(falcon.getAngle()) - 90) < 1 && (phaseStart.equals(phaseEnd)
                            || phaseEnd.isEmpty())) {
                        nextPhase();
                    }

                    falcon.incrementAngle(1);

                    double moveX = falcon.getCenterX() + (falcon.getRadiusX()) *
                            Math.sin(falcon.getAngle());

                    double moveY = falcon.getCenterY() - (falcon.getRadiusY()) *
                            Math.cos(falcon.getAngle());

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
        if (steps / 2 < routePlanets.size() - 1) {
            steps++;
            newStep = true;
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
        } else {
            slowButton.setDisable(false);
        }
    }

    /**
     * Increases the current movement speed of the system, caps speed at x512 and disables button if at cap
     */
    @FXML protected void speedUpMovement() {
        if (SPEED_FACTOR < 512) {
            SPEED_FACTOR = SPEED_FACTOR * 2;
        }

        if (SPEED_FACTOR == 512) {
            speedButton.setDisable(true);
        } else {
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
        routePlanets.clear();
        routeOrbit.clear();
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
        routePlanets.clear();
        routeOrbit.clear();

        // Load the XML file for the System Animation into a scene
        root = FXMLLoader.load(getClass().getResource("/solarsystem/resources/xml/system.fxml"));
        Scene scene = new Scene(root);

        // Replace the current window contents with the System Animation
        stage.setScene(scene);
        stage.show();
    }
}
