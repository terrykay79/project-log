/*
 * Copyright (C) 2014 tezk
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package projectlog;

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
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.LogEntry;
import model.PersistanceUnit;
import model.ProjectDescriptor;

/**
 * FXML Controller class
 *
 * @author tezk
 */
public class LogOverviewController implements Initializable
{

    private Scene mainScene;
    private Stage mainStage;
    private Scene locScene;
    private String projectName;

    private PersistanceUnit pu;

    @FXML
    private TableView<LogEntry> projectTable;
    @FXML
    private TableColumn<LogEntry, String> projectColumn;

    private ObservableList <LogEntry> logList;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        pu = PersistanceUnit.getPersistanceUnit();
        //setLogList();
    }

    public void setLogList()
    {
        List<LogEntry> list = pu.getLogs(projectName);

        logList = FXCollections.observableArrayList();
        projectColumn.setCellValueFactory(cellData -> cellData.getValue().getDetailsForDisplay());
        
        if (list==null)
        {
            System.out.println("Cry!");
            return;
        }
        Iterator<LogEntry> i = list.iterator();
        while (i.hasNext()) {
            LogEntry eachLog = i.next();
            logList.add(eachLog);
        }
        
        projectTable.setItems(logList);
    }
    
    public String getProjectName()
    {
        return projectName;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public void setLocScene(Scene locScene)
    {
        this.locScene = locScene;
    }
    
    public Scene getMainScene()
    {
        return mainScene;
    }

    public void setMainScene(Scene mainScene)
    {
        this.mainScene = mainScene;
    }

    @FXML
    private void loadMain(ActionEvent event)
    {
        mainStage.setScene(mainScene);
        mainStage.setTitle("Project log");
    }

    public Stage getMainStage()
    {
        return mainStage;
    }

    public void setMainStage(Stage mainStage)
    {
        this.mainStage = mainStage;
    }

    private void doEditLog(String logName, int logId)
    {
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ProjectLog.class.getResource("/view/newLog.fxml"));
        try {
            root = (Parent) fxmlLoader.load();
        }
        catch (IOException e) {
            System.err.println("Failed to load 'newLog' fxml");
        }
        Scene scene = new Scene(root);
        NewLogController controller = fxmlLoader.getController();

        controller.setStages(mainScene, mainStage, locScene);
        controller.setProjectName(projectName);

        mainStage.setScene(scene);

        mainStage.setTitle(projectName + " logs");
    }

    @FXML
    private void addNewLog(ActionEvent event)
    {
        doEditLog(null, 0);
    }

    @FXML
    private void editLog(ActionEvent event)
    {

    }

    @FXML
    private void deleteLog(ActionEvent event)
    {
    }

    @FXML
    private void todoList(ActionEvent event)
    {
        Parent root = null;
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(ProjectLog.class.getResource("/view/toDoList.fxml"));
        try {
            root = (Parent) fxmlLoader.load();
        }
        catch (IOException e) {
            System.err.println("Failed to load 'toDoList' fxml");
            return;
        }
        Scene scene = new Scene(root);
        ToDoListController controller = fxmlLoader.getController();

        controller.setStages(mainScene, mainStage, locScene);
        controller.setProjectName(projectName);
        System.out.println(projectName);
        controller.setLocScene(locScene);
        
        mainStage.setScene(scene);

        mainStage.setTitle("To do items");
    }

    @FXML
    private void statistics(ActionEvent event)
    {
    }

}
