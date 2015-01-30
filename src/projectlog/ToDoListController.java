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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import model.PersistanceUnit;
import model.ToDoItem;

/**
 * FXML Controller class
 *
 * @author tezk
 */
public class ToDoListController implements Initializable
{

    static final String[] choiceBoxChoices = {"Open only", "All", "Closed only"};
    private Scene mainScene;
    private Stage mainStage;
    private Scene locScene;

    private String projectName;
    private String currentSelection;

    private PersistanceUnit pu;

    private List<ToDoItem> toDoItems;
    @FXML
    private TableView<ToDoItem> toDoTable;
    @FXML
    private TableColumn<ToDoItem, String> toDoListColumn;
    @FXML
    private ChoiceBox<ArrayList> logTypeList;
    @FXML
    private TableColumn<ToDoItem, String> completedColumn;

    public String getProjectName()
    {
        return projectName;
    }

    public void setStages(Scene mainScene, Stage mainStage, Scene locScene)
    {
        this.mainScene = mainScene;
        this.mainStage = mainStage;
        this.locScene = locScene;
    }

    public Scene getLocScene()
    {
        return locScene;
    }

    public void setLocScene(Scene locScene)
    {
        this.locScene = locScene;
    }

    public void setProjectName(String projectName)
    {
        this.projectName = projectName;

        // List open only ToDo items
        try {
            toDoItems = ToDoItem.getToDoItems(pu.getConn(), projectName);
        }
        catch (SQLException e) {
            System.err.println("Error reading to do items : " + e.getMessage());
            return;
        }
        setItemList("Open only");
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

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        pu = PersistanceUnit.getPersistanceUnit();
        ObservableList myList = FXCollections.observableArrayList(choiceBoxChoices);
        logTypeList.setItems(myList);
        logTypeList.getSelectionModel().selectFirst();

        logTypeList.getSelectionModel().selectedIndexProperty().addListener(new ChoiceHandler());

        toDoListColumn.setCellValueFactory(cellData -> cellData.getValue().getDetailsForDisplay());
        completedColumn.setCellValueFactory(cellData -> cellData.getValue().getCompletedDetailsForDisplay());
    }

    private class ChoiceHandler implements ChangeListener
    {

        @Override
        public void changed(ObservableValue ov, Object valueP, Object newValueP)
        {
            String selection = choiceBoxChoices[((Number) newValueP).intValue()];
            setItemList(selection);
        }
    }

    protected void setItemList(String what)
    {
        currentSelection = what;
        //toDoTable, toDoListColumn
        if (toDoItems == null) {
            return;
        }
        ObservableList toDoList = FXCollections.observableArrayList();

        Iterator<ToDoItem> i = toDoItems.iterator();

        switch (what) {
            case "Open only":
                while (i.hasNext()) {
                    ToDoItem t = i.next();
                    System.out.println(t);
                    if (t.getCompleted() == 0) {
                        toDoList.add(t);
                    }
                }
                break;
            case "Closed only":
                while (i.hasNext()) {
                    ToDoItem t = i.next();
                    if (t.getCompleted() != 0) {
                        toDoList.add(t);
                    }
                }
                break;
            default:
                while (i.hasNext()) {
                    toDoList.add(i.next());
                }
                break;
        }

        toDoTable.setItems(toDoList);
    }

    @FXML
    private void markAsComplete(ActionEvent event)
    {
        ToDoItem anItem = toDoTable.getSelectionModel().getSelectedItem();

        anItem.setCompleted(System.currentTimeMillis());
        try {
            anItem.persist(pu.getConn());
        }
        catch (SQLException e) {
            System.err.println("Error updating todoitem : " + e.getMessage());
        }
        
        setItemList(currentSelection);
    }

    @FXML
    private void editLog(ActionEvent event)
    {
        ToDoItem selected = toDoTable.getSelectionModel().getSelectedItem();
        String logName = selected.getLogName();
        int logId = selected.getLogId();
        
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
        //controller.setLogController(this);
        controller.setLog(logName, logId);
        mainStage.setScene(scene);

        mainStage.setTitle(projectName + " logs");
    }

    @FXML
    private void openLogOverview(ActionEvent event)
    {
        mainStage.setScene(locScene);
        mainStage.setTitle(projectName + " logs");
    }

}
