package projectlog;

import java.io.IOException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author tezk
 */
public class ProjectLog extends Application
{
    @Override
    public void start(Stage stage)
    {
        Parent root = null;
      
        try {
            root = FXMLLoader.load(getClass().getResource("main.fxml"));
        }
        catch (IOException e) {
            System.err.println("Don't even bother!");
        }
        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();

    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        launch(args);
    }

}
