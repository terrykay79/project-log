
package projectlog;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TextArea;

/**
 * FXML Controller class
 *
 * @author tezk
 */
public class MainController implements Initializable
{

    /**
     * Initializes the controller class.
     */
    @FXML
    private TextArea textArea;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // TODO
        PersistanceUnit pu = PersistanceUnit.getPersistanceUnit();
        textArea.setText(pu.getName());
    }    
    
}
