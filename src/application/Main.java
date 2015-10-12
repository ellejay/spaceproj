package application;

import java.util.ArrayList;
import java.util.List;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Circle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Main extends Application {

	// Planet scale factor
	private static double SCREEN_SCALE = 0.7075;
	// Rotation speed
	private static final double STEP_DURATION = 3; //milliseconds
	// Scene Size
	private static final int SCENE_SIZE = 700;


	@Override
	public void start(Stage primaryStage) {
		final int midPoint = SCENE_SIZE / 2;

		final List<Planet> planets = new ArrayList<>();

		Circle sun_obj = new Circle(midPoint, midPoint, 3);
		sun_obj.getStyleClass().add("sun");
		final Star sun = new Star("sun", 1392530, 1.9891e30, sun_obj);

		planets.add(new Planet("mercury", 57.92, 58.65, 5.2, sun));
		planets.add(new Planet("venus", 108.2, 224.7, 1.8, sun));
		planets.add(new Planet("earth", 149.6, 365.2, 1.4, sun));
		planets.add(new Planet("mars", 228.0, 687.0, 3.6, sun));
		planets.add(new Planet("jupiter", 779.1, 4333.0, 1.6, sun));
		planets.add(new Planet("saturn", 1426.0, 10759.0, 4.5, sun));
		planets.add(new Planet("uranus", 2870.0, 30685.0, 1.6, sun));
		planets.add(new Planet("neptune", 4493.0, 60200.0, 2.4, sun));

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
					moveBall(current.getGUIObject(),

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

		Slider slider = new Slider(0.065, 1.35, 0.7075);
		slider.setOrientation(Orientation.VERTICAL);
		slider.setShowTickMarks(true);
		slider.setShowTickLabels(true);
		slider.setMajorTickUnit(0.257);

		Pane root = new Pane();
		
		slider.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov,
					Number old_val, Number new_val) {
				SCREEN_SCALE = (double) new_val;
				for (Planet current: planets) {        	
					root.getChildren().remove(current.getGUIOrbit());
					Circle orbit = new Circle(midPoint, midPoint, current.getOrbit() * SCREEN_SCALE);
					current.setGUIOrbit(orbit);
					root.getChildren().add(orbit);
				}
			}
		});
		
		
		BorderPane border = new BorderPane();
		
		border.setCenter(root);

		root.getChildren().add(sun_obj);

		for (Planet current: planets) {        	
			root.getChildren().add(current.getGUIOrbit());
			root.getChildren().add(current.getGUIObject());
		}
		
		StackPane slider_pane = new StackPane();
		slider_pane.setAlignment(Pos.CENTER);
		slider_pane.getChildren().add(slider);
		
		border.setRight(slider_pane);

		Scene scene = new Scene(border, SCENE_SIZE, SCENE_SIZE);

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

