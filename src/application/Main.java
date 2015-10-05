package application;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
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
 
        // Orbits of the balls (Radius)
        final double[] ballOrbits = {0.387,0.723,1.0,1.524,5.203, 9.54,19.18,30.06};
        // Movement
        final double[] ballMovement = { 0.241, 0.615, 1.0, 1.881, 11.86, 29.46, 84.01, 164.8 };

        final double[] planetAngle = { 5.2, 1.8, 1.4, 3.6, 1.6, 4.5, 1.6, 2.4 };
 
        final List<Circle> balls = new ArrayList<>();
        for (int i = 0; i < ballOrbits.length; i++) {
        	Circle planet = new Circle(0, 0, 3);
        	planet.getStyleClass().add("ball-style-" + i);
            balls.add(planet);
        }
 
        EventHandler<ActionEvent> planetMovement = new EventHandler<ActionEvent>() { 
            @Override
            public void handle(ActionEvent event) {
 
                for (int i = 0; i < balls.size(); i++) {
                	
                	planetAngle[i] += Math.toRadians( (2 * Math.PI) / ballMovement[i]);
                    // p(x) = x(0) + r * sin(a)
                    // p(y) = y(y) - r * cos(a)
                    moveBall(balls.get(i),
                    		midPoint + (ballOrbits[i] * SCREEN_SCALE) * Math.sin(planetAngle[i]),
                    		midPoint - (ballOrbits[i] * SCREEN_SCALE) * Math.cos(planetAngle[i]));
                
                    // Reset after one orbit.
                    if (planetAngle[i] >= (2 * Math.PI)) {
                    	planetAngle[i] -= 2 * Math.PI;
                    }
                
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
        
        for (int i = 0; i < balls.size(); i++) {
        	Circle x = new Circle(midPoint, midPoint, ballOrbits[i] * SCREEN_SCALE);
        	x.setStroke(Color.GREY);
        	x.setFill(Color.TRANSPARENT);
        	root.getChildren().add(x);
        }
 
        root.getChildren().addAll(balls);
        
        Scene scene = new Scene(root, SCENE_SIZE, SCENE_SIZE);
 
        primaryStage.setTitle("Solar System");
        primaryStage.setScene(scene);
        primaryStage.getScene().getStylesheets().add(getClass().
        		getResource("application.css").toExternalForm());
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

