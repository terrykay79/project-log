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
        FXMLLoader fxmlLoader=new FXMLLoader();
        fxmlLoader.setLocation(ProjectLog.class.getResource("/view/main.fxml"));
        System.out.println(ProjectLog.class.getResource("/view/main.fxml"));
        try {
            root = (Parent)fxmlLoader.load();
        }
        catch (Exception e) {
            System.err.println("Don't even bother!");
            System.exit(0);
        }
        Scene scene = new Scene(root);

        stage.setScene(scene);
       
        MainController.setStage(stage);
        MainController.setScene(scene);
        stage.setTitle("Project log");
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
