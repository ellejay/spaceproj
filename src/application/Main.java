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
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {
 
	// Planet scale
	private static final int SCREEN_SCALE = 50;
    // Ball speed
    private static final int STEP_DURATION_IN_MILLISECONDS = 100;
    // Scene Size
    private static final int SCENE_SIZE = 700;
 
    
	@Override
    public void start(Stage primaryStage) {
        final int midPoint = SCENE_SIZE / 2;
        
        List<Planet> planets = new ArrayList<>();
 
        planets.add(new Planet("mercury", 0.387, 0.241, 5.2));
        planets.add(new Planet("venus", 0.723, 0.615, 1.8));
        planets.add(new Planet("earth", 1.0, 1.0, 1.4));
        planets.add(new Planet("mars", 1.524, 1.881, 3.6));
        planets.add(new Planet("jupiter", 5.203, 11.86, 1.6));
        planets.add(new Planet("saturn", 9.54, 29.46, 4.5));
        planets.add(new Planet("uranus", 19.18, 84.01, 1.6));
        planets.add(new Planet("neptune", 30.06, 164.8, 2.4));

        for (Planet current: planets) {
        	Circle planet = new Circle(0, 0, 3);
        	current.setGUIObject(planet);
        }
 
        EventHandler<ActionEvent> planetMovement = new EventHandler<ActionEvent>() { 
            @Override
            public void handle(ActionEvent event) {
 
                for (Planet current: planets) {
                	
                	current.incrementAngle();
                	
                    // p(x) = x(0) + r * sin(a)
                    // p(y) = y(y) - r * cos(a)
                    moveBall(current.getGUIObject(),
                    		
                    		midPoint + (current.getOrbit() * SCREEN_SCALE) * 
                    		Math.sin(current.getAngle()),
                    		
                    		midPoint - (current.getOrbit() * SCREEN_SCALE) * 
                    		Math.cos(current.getAngle()));
                               
                }
                 
            }
        };
        
        
        final Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, planetMovement), 
        		new KeyFrame(Duration.millis(STEP_DURATION_IN_MILLISECONDS)));
 
        timeline.setCycleCount(Timeline.INDEFINITE);
 
        Pane root = new Pane();
        
        Circle sun = new Circle(midPoint, midPoint, 3);
        sun.setFill(Color.ORANGE);
        root.getChildren().add(sun);
        
        for (Planet current: planets) {
        	Circle x = new Circle(midPoint, midPoint, current.getOrbit() * SCREEN_SCALE);
        	x.setStroke(Color.GREY);
        	x.setFill(Color.TRANSPARENT);
        	root.getChildren().add(x);
        	
        	root.getChildren().add(current.getGUIObject());
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
        		Duration.millis(STEP_DURATION_IN_MILLISECONDS), ball);
        move.setToX(x);
        move.setToY(y);
        move.playFromStart();
    }
 
    public static void main(String[] args) {
        launch(args);
    }
}

