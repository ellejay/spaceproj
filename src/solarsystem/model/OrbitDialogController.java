package solarsystem.model;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import javafx.stage.Stage;
 
public class OrbitDialogController {
    @FXML private Text actiontarget;
    @FXML private Button submitButton;
    
    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
        System.out.println("Sign in button pressed");
        Stage stage = (Stage) submitButton.getScene().getWindow();
        stage.close();
    }

}

