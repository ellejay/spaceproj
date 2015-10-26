package src.solarsystem.model;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class SolarSystem extends Application {
    
    public static void main(String[] args) {
        Application.launch(SolarSystem.class, args); 
    }
    
    @Override
    public void start(Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("system.fxml"));
        
        stage.setTitle("FXML Space");
        stage.setScene(new Scene(root, 650, 650));
        stage.show();
    }
}
