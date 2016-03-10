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
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import solarsystem.objects.BodyInSpace;
import solarsystem.objects.SpaceObjects;
import solarsystem.objects.Spaceship;
import solarsystem.math.Calculator;
import solarsystem.math.MathEllipse;

public class JourneyController extends SuperController implements Initializable {

    @FXML private Label routeStage;
    @FXML private Pane systemPane;
    @FXML private Pane sourcePane;
    @FXML private TextArea routeData;
    @FXML private Pane completionPane;
    @FXML private TextArea journeyInfo;
    private Timeline timeline;
    private int steps;
    private boolean newStep = true;
    private boolean transferWindow = false;
    private double journeyMoveX = 0, journeyMoveY = 0;
    private double startAngle, endAngle, transAngle, drawAngle;
    private final StringBuilder finalJourney = new StringBuilder();
    private double journeyTime;
    private double orbitsSearch;
    private double focusScale;
    private double incOut = 100;
    private double incIn = 200;
    private double transOrb1, transOrb2;
    private boolean orbitTrans, newMove, transferComplete;
    private double lineIn;
    private BodyInSpace currentParent = SpaceObjects.getSun();
    private Map<String, BodyInSpace> selection = SpaceObjects.getPlanets();

    private final static Logger LOGGER = Logger.getLogger(JourneyController.class.getName());

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setUp();
    }

    private void setUp() {

        for (BodyInSpace current : selection.values()) {
            current.resetPlanet();
        }

        EventHandler<ActionEvent> planetMovement = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                for (BodyInSpace current : selection.values()) {

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

        currentParent.moveGUIObject(midPoint, midPoint);

        final Spaceship enterprise = new Spaceship();

        steps = 0;

        final Ellipse route = new Ellipse();
        route.getStyleClass().add("planet-orbit-path");
        final Calculator calc = new Calculator(SpaceObjects.getBody(routePlanets.get(0)));

        final double focusWidth = sourcePane.getPrefWidth() / 2;
        final Circle planetFocus = new Circle(focusWidth, focusWidth, 6);

        final Spaceship falcon = new Spaceship();
        falcon.setCenterPoint(planetFocus.getCenterX(), planetFocus.getCenterY());
        falcon.setRadius(0, 0);

        double maxOrbit = Double.MIN_VALUE;
        for (double[] orbit : routeOrbit) {
            for (double val : orbit) {
                if (val > maxOrbit) {
                    maxOrbit = val;
                }
            }
        }

        focusScale = (focusWidth - 4) / maxOrbit;

        Line entryLine = new Line(195, 0, 195, 200);
        entryLine.setStroke(Color.TRANSPARENT);
        entryLine.setStrokeWidth(1);

        EventHandler<ActionEvent> spaceshipMove = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {

                int planetIndex = steps / 2;
                int movement = 1;

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
                        movement = -1;
                        resetScale(285 / startPlanet.getOrbit());
                    } else {
                        movement = 1;
                        resetScale(285 / endPlanet.getOrbit());
                    }
                } catch (IndexOutOfBoundsException e) {
                    phaseEnd = "";
                }

                if (newStep && !phaseEnd.isEmpty()) {
                    if (startPlanet.isSibling(endPlanet)) {
                        LOGGER.info("CURRENT FRAME: " + startPlanet.getParent().getName());
                        changeFrame(startPlanet.getParent().getName(), enterprise);
                    }
                    else if (startPlanet.isParent(endPlanet)) {
                        LOGGER.info("CURRENT FRAME: " + startPlanet.getName());
                        changeFrame(startPlanet.getName(), enterprise);
                    }
                    else if (startPlanet.isChild(endPlanet)) {
                        LOGGER.info("CURRENT FRAME: " + endPlanet.getName());
                        changeFrame(endPlanet.getName(), enterprise);
                    }
                }

                double moveX, moveY;
                BodyInSpace nearestPlanet, furthestPlanet;
                double radiusJourney = 0;

                if (steps % 2 == 1) {

                    if (!phaseStart.equals(phaseEnd) && startPlanet.isSibling(endPlanet)) {
                        enterprise.setParent(startPlanet.getParent());

                        double distance = Math.abs(endPlanet.getOrbit() + startPlanet.getOrbit());

                        if (movement == 1) {
                            nearestPlanet = startPlanet;
                            furthestPlanet = endPlanet;

                        } else {
                            nearestPlanet = endPlanet;
                            furthestPlanet = startPlanet;
                        }

                        MathEllipse transferPath = new MathEllipse(currentParent.getMass(),
                                nearestPlanet.getOrbit(), furthestPlanet.getOrbit());

                        route.setRadiusX(transferPath.semi_minor() * SCREEN_SCALE);
                        route.setRadiusY(transferPath.semi_major() * SCREEN_SCALE);


                        radiusJourney = route.getRadiusY() - (endPlanet.getOrbit()) * SCREEN_SCALE;

                        route.setCenterX(enterprise.getParent().getX());
                        route.setCenterY(enterprise.getParent().getY() - radiusJourney);

                        if (newStep) {
                            journeyMoveX = enterprise.getParent().getX() + ((radiusJourney) *
                                    Math.sin(startPlanet.getAngle()));

                            journeyMoveY = enterprise.getParent().getY() - ((radiusJourney) *
                                    Math.cos(startPlanet.getAngle()));

                            enterprise.setPathRotation(Math.toDegrees(startPlanet.getAngle()));
                        }

                        route.setCenterX(journeyMoveX);
                        route.setCenterY(journeyMoveY);


                        enterprise.setRadius(route.getRadiusX() / SCREEN_SCALE, route.getRadiusY() / SCREEN_SCALE);
                    } else if (!phaseStart.equals(phaseEnd)) {

                        double distance;
                        if (startPlanet.isChild(endPlanet)) {
                            enterprise.setParent(startPlanet.getParent());
                            distance = startPlanet.getOrbit();
                        } else {
                            enterprise.setParent(endPlanet.getParent());
                            distance = endPlanet.getOrbit();
                        }

                        MathEllipse transferPath = new MathEllipse(currentParent.getMass(),
                                0, distance);

                        double pathWidth = transferPath.semi_minor();
                        if (transferPath.semi_minor() == 0) {
                            pathWidth = currentParent.getGUIObject().getRadius() * 2 / SCREEN_SCALE;
                        }

                        route.setRadiusX(pathWidth * SCREEN_SCALE);
                        route.setRadiusY(transferPath.semi_major() * SCREEN_SCALE);

                        //+ (nearestPlanet.getOrbit()) * SCREEN_SCALE
                        radiusJourney = route.getRadiusY();

                        route.setCenterX(enterprise.getParent().getX());
                        route.setCenterY(enterprise.getParent().getY() - radiusJourney);

                        if (newStep) {
                            journeyMoveX = enterprise.getParent().getX() + ((radiusJourney) *
                                    Math.sin(drawAngle));

                            journeyMoveY = enterprise.getParent().getY() - ((radiusJourney) *
                                    Math.cos(drawAngle));

                            enterprise.setPathRotation(Math.toDegrees(drawAngle));

                        }

                        route.setCenterX(journeyMoveX);
                        route.setCenterY(journeyMoveY);

                        enterprise.setRadius(route.getRadiusX() / SCREEN_SCALE, route.getRadiusY() / SCREEN_SCALE);
                    }
                    else {
                        enterprise.setCenterPoint(startPlanet.getX(), startPlanet.getY());
                        enterprise.setRadius(0, 0);
                    }


                    if (newStep) {

                        routeStage.setText(startPlanet.getName() + " > " + endPlanet.getName());

                        enterprise.setAngle(startAngle);

                        newStep = false;
                        String data = calc.getTransferData();
                        String newData = data.replace("\t", "\n");

                        routeData.setText(newData.replace("days ", "days\n"));

                        finalJourney.append(startPlanet.getName() + " ");
                        if (startOrbit[0] == 0 && startOrbit[1] == 0) {
                            finalJourney.append("Surface");
                        } else {
                            finalJourney.append(String.format("%.0fm/%.0fm", startOrbit[0], startOrbit[1]));
                        }

                        finalJourney.append(" > " + endPlanet.getName() + " ");

                        if (endOrbit[0] == 0 && endOrbit[1] == 0) {
                            finalJourney.append("Surface");
                        } else {
                            finalJourney.append(String.format("%.0fm/%.0fm", endOrbit[0], endOrbit[1]));
                        }

                        finalJourney.append("\n\t" + calc.getTransferData());

                        journeyTime += calc.getTime();

                        double speed;
                        if (startPlanet.equals(endPlanet)) {
                            enterprise.setPeriod(startPlanet.getPeriod());
                        }
                        else if (startPlanet.isSibling(endPlanet)) {
                            if (endPlanet.getAngularV() < startPlanet.getAngularV()) {
                                speed = endPlanet.getAngularV();
                            } else {
                                speed = startPlanet.getAngularV();
                            }

                            double transitionSpeed = 360 / ((speed * Math.PI) / (transAngle));

                            enterprise.setPeriod(transitionSpeed);
                        }
                        else if (startPlanet.isChild(endPlanet) || endPlanet.isChild(startPlanet)) {
                            speed = (2 * (calc.getTime() / 86400));

                            enterprise.setPeriod(speed);
                        }

                    } else {
                        enterprise.incrementAngle(SPEED_FACTOR);
                    }

                    if (!phaseStart.equals(phaseEnd)) {
                        enterprise.setCenterPoint(journeyMoveX, journeyMoveY);

                        if (Math.abs(Math.toDegrees(enterprise.getAngle() - endAngle)) < 1) {
                            nextPhase();
                        }

                    }


                } else {

                    if (newStep) {
                        newStep = false;

                        routeStage.setText(startPlanet.getName() + " Orbit");
                        routeData.setText("Apoapsis = " + startOrbit[0] + "km\nPeriapsis = " + startOrbit[1] + "km");

                        if (movement == -1) {
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

                            double phase = Math.toDegrees(endPlanet.getAngle() - startPlanet.getAngle() );
                            if (phase < 0) {
                                phase += 360.0;
                            }

                            if (Math.abs(phase - calc.getStartPhaseAngle()) < 1) {

                                double angleTravelled = orbitsSearch * Math.toRadians(startPlanet.getAngularV());

                                double timeTaken = startPlanet.getPeriodAsSeconds() *
                                        (angleTravelled / 360);

                                finalJourney.append(startPlanet.getName() + "\n\t");
                                finalJourney.append("Time In Orbit = " + timeToString(timeTaken));

                                journeyTime += timeTaken;

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

                                if (movement == -1 || childPlanet.equals(startPlanet)) {
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

                moveX = enterprise.getCenterX() + (enterprise.getRadiusX() * SCREEN_SCALE) *
                        Math.sin(enterprise.getAngle());

                moveY = enterprise.getCenterY() - (enterprise.getRadiusY() * SCREEN_SCALE) *
                        Math.cos(enterprise.getAngle());

                enterprise.setRadius(enterprise.getRadiusX() * SCREEN_SCALE, enterprise.getRadiusY() * SCREEN_SCALE);

                double cx = enterprise.getCenterX();
                double cy = enterprise.getCenterY();

                double nmoveX = (moveX - cx) * Math.cos(enterprise.getPathRotation())
                        - (moveY - cy) * Math.sin(enterprise.getPathRotation()) + cx;

                double nmoveY = (moveX-cx)*Math.sin(enterprise.getPathRotation())
                        + (moveY - cy) * Math.cos(enterprise.getPathRotation()) + cy;

                moveBall(enterprise.getGUIShip(), nmoveX, nmoveY);
            }
        };


        EventHandler<ActionEvent> focusMovement = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int planetIndex = steps / 2;
                int movement = 1;

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

                    double pathWidth = transferPath.semi_minor();
                    if (transferPath.semi_minor() == 0) {
                        pathWidth = planetFocus.getRadius() * 2;
                    }

                    falcon.setRadius(transferPath.semi_major() * focusScale, pathWidth * focusScale);


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

                            falcon.setRadius(orbit.semi_major() * focusScale, orbit.semi_minor() * focusScale);

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

                        falcon.setRadius(orbit.semi_major() * focusScale, orbit.semi_minor() * focusScale);

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


        timeline = new Timeline(new KeyFrame(Duration.ZERO, planetMovement),
                new KeyFrame(Duration.ZERO, spaceshipMove),
                new KeyFrame(Duration.ZERO, focusMovement),
                new KeyFrame(Duration.millis(STEP_DURATION)));

        timeline.setCycleCount(Timeline.INDEFINITE);

        //timeline.setRate();

        systemPane.getChildren().add(currentParent.getGUIObject());

        for (BodyInSpace current : selection.values()) {
            current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
            systemPane.getChildren().add(current.getGUIOrbit());
            systemPane.getChildren().add(current.getGUIObject());
        }

        systemPane.getChildren().add(enterprise.getGUIShip());
        systemPane.getChildren().add(enterprise.getGUITrail());

        sourcePane.getChildren().add(falcon.getGUITrail());
        sourcePane.getChildren().add(falcon.getGUIShip());

        sourcePane.getChildren().add((entryLine));

        sourcePane.getChildren().add(planetFocus);

        timeline.play();
    }


    private void moveBall(Shape ball, double x, double y) {
        TranslateTransition move = new TranslateTransition(
                Duration.millis(STEP_DURATION), ball);
        move.setToX(x);
        move.setToY(y);
        move.playFromStart();
    }

    private void resetScale(double new_val) {

        double min = SpaceObjects.getScale(currentParent.getName()).get(1);

        double val;
        if (new_val > min) {
            val = min;
        } else {
            val = new_val;
        }

        SCREEN_SCALE = val;
        for (BodyInSpace current : selection.values()) {
            current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
        }
    }

    @FXML
    private void nextPhase() {
        if (steps / 2 < routePlanets.size() - 1) {
            steps++;
            newStep = true;
        } else {
            timeline.pause();
            completionPane.toFront();
            finalJourney.append("\r\nTotal Journey Time\n\t");
            finalJourney.append(timeToString(journeyTime));
            journeyInfo.setText(finalJourney.toString());
            LOGGER.info("journey complete");
        }
    }


    @FXML
    protected void stopTimeline() throws IOException {
        timeline.pause();

        Stage stage;
        Parent root;
        stage = (Stage) routeStage.getScene().getWindow();

        routePlanets.clear();
        routeOrbit.clear();
        SCREEN_SCALE = 0.045;

        root = FXMLLoader.load(getClass().getResource("/solarsystem/resources/xml/pathselect.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }

    @FXML
    protected void viewAnimation() throws IOException {

        timeline.pause();

        Stage stage;
        Parent root;
        stage = (Stage) routeStage.getScene().getWindow();

        routePlanets.clear();
        routeOrbit.clear();

        root = FXMLLoader.load(getClass().getResource("/solarsystem/resources/xml/system.fxml"));
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();

    }

    @FXML
    protected void quitProgram() throws IOException {
        Platform.exit();
    }

    @FXML
    protected void saveJourney() throws IOException {
        FileChooser fileChooser = new FileChooser();

        //Set extension filter
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);

        //Show save file dialog
        Stage stage = (Stage) routeStage.getScene().getWindow();
        File file = fileChooser.showSaveDialog(stage);

        if (file != null) {
            try {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(finalJourney.toString());
                fileWriter.close();
            } catch (IOException ex) {
                System.err.println("Could not save journey information");
            }
        }
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

    private String timeToString(double t) {
        double days = Math.floor(t / 86400);
        double hours = Math.floor((t % 86400) / 3600);
        double minutes = Math.floor(((t % 86400) % 3600) / 60);
        double seconds = Math.floor(((t % 86400) % 3600) % 60);

        return String.format("%6.0f days %6.0f hours %6.0f mins %6.0f s\n", days, hours, minutes, seconds);
    }

    private void changeFrame(String parent, Spaceship spaceship) {
        if (parent.equals(currentParent.getName())) {
            return;
        }

        if (parent.equals("Sun")) {
            currentParent = SpaceObjects.getSun();
            selection = SpaceObjects.getPlanets();
            SPEED_FACTOR = 1;
        } else {
            currentParent = SpaceObjects.getPlanets().get(parent);
            selection = SpaceObjects.getChildren(parent);
            SPEED_FACTOR = 0.25;
        }

        systemPane.getChildren().clear();

        currentParent.resetPlanet();
        systemPane.getChildren().add(currentParent.getGUIObject());
        currentParent.moveGUIObject(midPoint, midPoint);

        if (!selection.isEmpty()) {
            for (BodyInSpace current : selection.values()) {
                current.adjustGUIOrbit(current.getOrbit() * SCREEN_SCALE);
                systemPane.getChildren().add(current.getGUIOrbit());
            }

            for (BodyInSpace current : selection.values()) {
                current.resetPlanet();
                systemPane.getChildren().add(current.getGUIObject());
            }

        }

        systemPane.getChildren().add(spaceship.getGUIShip());
        systemPane.getChildren().add(spaceship.getGUITrail());
    }

}
