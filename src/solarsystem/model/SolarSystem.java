package solarsystem.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

public class SolarSystem extends Application {
    
    public static void main(String[] args) {
        Application.launch(SolarSystem.class, args); 
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("../resources/xml/system.fxml"));
        
        stage.setTitle("FXML Space");
        stage.setScene(new Scene(root, 750, 600));
        
        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        stage.setMaxHeight(primaryScreenBounds.getHeight());
        
        stage.show();
    }
}
