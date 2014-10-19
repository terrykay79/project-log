package projectlog;

import model.PersistanceUnit;
import model.ProjectDescriptor;
import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import view.FXDialog;

/**
 * FXML Controller class
 *
 * @author tezk
 */
public class MainController implements Initializable
{

    private static Stage mainStage;
    // References to Stage and Controller for "newProject" stage & controller, saves creating them over and over
    private Stage newProjectStage;
    private NewController newController;
    // Reference to the DBAccess methods
    private static PersistanceUnit pu = null;

    //List of projects displayed in the table
    private ObservableList<ProjectDescriptor> projectList;

    public static void setStage(Stage stage)
    {
        mainStage = stage;
    }

    @FXML
    private TableView<ProjectDescriptor> projectTable;
    @FXML
    private Button openButton;
    @FXML
    private Button createButton;
    @FXML
    private TableColumn<ProjectDescriptor, String> projectColumn;
    @FXML
    private TableColumn<ProjectDescriptor, String> lastColumn;
    @FXML
    private Button deleteButton;

    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        // Called on new creation of every window, pu = set up once!
        if (pu == null) {
            pu = PersistanceUnit.getPersistanceUnit();

        //projectTable.setItems(projectList);
            //newProjectStage = null;
        }

        setProjectList();
    }

    private void setProjectList()
    {

        List<ProjectDescriptor> list = pu.getProjects();

        projectList = FXCollections.observableArrayList();
        Iterator<ProjectDescriptor> i = list.iterator();
        while (i.hasNext()) {
            ProjectDescriptor eachProject = i.next();
            projectList.add(eachProject);
        }
        Collections.sort(projectList);
        projectTable.setItems(projectList);
        projectColumn.setCellValueFactory(cellData -> cellData.getValue().logNameProperty());
        lastColumn.setCellValueFactory(cellData -> cellData.getValue().lastDateProperty());
    }

    @FXML
    private void newProject(ActionEvent event)
    {
        // If we've already displayed the stage once, should still be there.. Saves time on loading again
        
        if (newProjectStage == null) {
            Parent root = null;
            newProjectStage = new Stage();
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(ProjectLog.class.getResource("/view/newProject.fxml"));
            try {
                root = (Parent) fxmlLoader.load();
            }
            catch (IOException e) {
                System.err.println("Failed to load 'new project' fxml");
            }
            Scene scene = new Scene(root);

            newProjectStage.setScene(scene);
            newProjectStage.initOwner(mainStage);
            newProjectStage.setTitle("Add new project");
            newController=fxmlLoader.getController();
        }
        else
            newController.clearFields();

        newProjectStage.showAndWait();
        if (newController.isOkPressed()) {
            projectList.add(newController.getNewProject());
            Collections.sort(projectList);
        }
    }

    @FXML
    private void openProject(ActionEvent event)
    {
        System.out.println("Open "+projectTable.getSelectionModel().getSelectedItem().getLogName());
    }

    @FXML
    private void deleteProject(ActionEvent event)
    {
        new FXDialog("Are you sure?");
    }
}
