package solarsystem.model;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
 
public class OrbitDialogController extends SuperController {
    @FXML private Text actiontarget;
    @FXML private Button submitButton;
    @FXML private TextField field_f1;
    @FXML private TextField field_f2;
    
    @FXML protected void handleSubmitButtonAction(ActionEvent event) {
    	
    	double f1, f2;
    	
    	try {
    		f1 = Double.parseDouble(field_f1.getText());
    		f2 = Double.parseDouble(field_f2.getText());
    		
    		orbitParams[0] = f1;
    		orbitParams[1] = f2;
    		
    		Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
    	}
    	catch (NumberFormatException e) {
    		
    	}
    	catch (NullPointerException e) {
    		
    	}
    		
    }

}

