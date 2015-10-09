package application;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
 
	// Planet scale factor
	private static final double SCREEN_SCALE = 0.2;
    // Rotation speed
    private static final double STEP_DURATION = 3; //milliseconds
    // Scene Size
    private static final int SCENE_SIZE = 600;
 
    
	@Override
    public void start(Stage primaryStage) {
        final int midPoint = SCENE_SIZE / 2;
        
        final List<Planet> planets = new ArrayList<>();
        
        planets.add(new Planet("mercury", 57.92, 58.65, 5.2));
        planets.add(new Planet("venus", 108.2, 224.7, 1.8));
        planets.add(new Planet("earth", 149.6, 365.2, 1.4));
        planets.add(new Planet("mars", 228.0, 687.0, 3.6));
        planets.add(new Planet("jupiter", 779.1, 4333.0, 1.6));
        planets.add(new Planet("saturn", 1426.0, 10759.0, 4.5));
        planets.add(new Planet("uranus", 2870.0, 30685.0, 1.6));
        planets.add(new Planet("neptune", 4493.0, 60200.0, 2.4));

        for (Planet current: planets) {
        	Circle planet = new Circle(0, 0, 3);
        	current.setGUIPlanet(planet);
        	
        	Circle orbit = new Circle(midPoint, midPoint, current.getOrbit() * SCREEN_SCALE);
        	current.setGUIOrbit(orbit);
        }
 
        EventHandler<ActionEvent> planetMovement = new EventHandler<ActionEvent>() { 
            @Override
            public void handle(ActionEvent event) {
 
                for (Planet current: planets) {
                	
                	current.incrementAngle();
                	
                    // p(x) = x(0) + r * sin(a)
                    // p(y) = y(y) - r * cos(a)
                    moveBall(current.getGUIPlanet(),
                    		
                    		midPoint + (current.getOrbit() * SCREEN_SCALE) * 
                    		Math.sin(current.getAngle()),
                    		
                    		midPoint - (current.getOrbit() * SCREEN_SCALE) * 
                    		Math.cos(current.getAngle()));
                               
                }
                 
            }
        };
        
        
        final Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, planetMovement), 
        		new KeyFrame(Duration.millis(STEP_DURATION)));
 
        timeline.setCycleCount(Timeline.INDEFINITE);
 
        Pane root = new Pane();
        
        Circle sun = new Circle(midPoint, midPoint, 3);
        sun.getStyleClass().add("sun");
        root.getChildren().add(sun);
        
        for (Planet current: planets) {        	
        	root.getChildren().add(current.getGUIOrbit());
        	root.getChildren().add(current.getGUIPlanet());
        }
        
        Scene scene = new Scene(root, SCENE_SIZE, SCENE_SIZE);
 
        primaryStage.setTitle("Solar System");
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add(getClass().
        		getResource("application.css").toExternalForm());
        
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();
        
        primaryStage.setHeight(bounds.getHeight());
        primaryStage.setWidth(bounds.getHeight());
        
        primaryStage.show();
 
        timeline.play();
    }
 
    private void moveBall(Circle ball, double x, double y) {
        TranslateTransition move = new TranslateTransition(
        		Duration.millis(STEP_DURATION), ball);
        move.setToX(x);
        move.setToY(y);
        move.playFromStart();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}

