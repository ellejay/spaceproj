package solarsystem.controller;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

/**
 * Main class used to launch the JavaFX application.
 * @author Laura McGhie
 */
public class SolarSystem extends Application {

    public static void main(String[] args) {
        Application.launch(SolarSystem.class, args); 
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        // Load the XML file for the starting screen as a layout
        Parent root = FXMLLoader.load(getClass().getResource("/solarsystem/resources/xml/system.fxml"));

        // Set the title and add the XML layout to the screen
        stage.setTitle("Space Explorer");
        stage.setScene(new Scene(root));

        // Restrict height of the window
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setMaxHeight(primaryScreenBounds.getHeight());

        // Disable resizing of the window as the program does not handle scales
        stage.setResizable(false);

        // Display the application
        stage.show();
    }
}
