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

import java.net.URL;
import java.util.Iterator;
import java.util.ResourceBundle;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TablePosition;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.stage.Stage;
import model.LogEntry;
import model.PersistanceUnit;
import model.ToDoItem;
import utilities.MyDate;
import view.FXDialog;

/**
 * FXML Controller class Controller to handle "New log entry"
 *
 * @author tezk
 */

// TODO if editing, handle times - either allow no change or keep original date part of the time

public class NewLogController implements Initializable
{

    @FXML
    private TextArea logDetails;
    @FXML
    private TextField startTime;
    @FXML
    private TextField endTime;
    @FXML
    private TextField areaText;
    @FXML
    private TableView<ToDoItem> todoTable;
    @FXML
    private TableColumn<ToDoItem, String> itemColumn;
    @FXML
    private TableColumn<ToDoItem, String> completedColumn;

    private ObservableList<ToDoItem> toDoList;

    private Scene mainScene;
    private Stage mainStage;
    private Scene logScene;
    private LogOverviewController logController;
    private String projectName;
    private int logId;
    // used when editing an old entry, null otherwise
    private LogEntry editLogEntry;
    // TODO: Can't remember why this was created...
    private LogEntry logEntry;
    
    public void setProjectName(String projectName)
    {
        this.projectName = projectName;
    }

    public void setStages(Scene mainScene, Stage mainStage, Scene logScene)
    {
        this.mainScene = mainScene;
        this.mainStage = mainStage;
        this.logScene = logScene;
    }

    public LogOverviewController getLogController()
    {
        return logController;
    }

    public void setLogController(LogOverviewController logController)
    {
        this.logController = logController;
    }

    public void setLog(String logName, int logId)
    {
        // Use to set fields from existing log, to allow editing
        LogEntry aLog = LogEntry.find(logName, logId);
        setLog(aLog);
    }
    
    public void setLog(LogEntry aLogEntry)
    {
        logDetails.setText(aLogEntry.getDetails());
        startTime.setText(MyDate.longToTime(aLogEntry.getStarted()));
        endTime.setText(MyDate.longToTime(aLogEntry.getFinished()));
        areaText.setText(aLogEntry.getArea());
        logId=aLogEntry.getLogId();
        editLogEntry = aLogEntry;
        logEntry = aLogEntry;
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {
        long currentTime = System.currentTimeMillis();
        startTime.setText(MyDate.longToTime(currentTime));
        logEntry = new LogEntry();
        logEntry.setStarted(currentTime);
        logId = 0;
        editLogEntry = null;
        // Cannot setup focus changes from Scenebuilder! Must add from code..
        endTime.focusedProperty().addListener(new ChangeListener<Boolean>()
        {
            @Override
            public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue)
            {
                if (newPropertyValue) {
                    //System.out.println("Textfield on focus");
                }
                else {
                    //System.out.println("Textfield out focus");
                    changeEndTime(null);
                }
            }
        });

        // Add blank to do list item that can be edited to add more, set description to "Enter an item" logName as null
        toDoList = FXCollections.observableArrayList();
        toDoList.add(new ToDoItem(null, "Enter an item"));
        todoTable.setItems(toDoList);
        //itemColumn.setCellValueFactory(cellData -> cellData.getValue().getDetailsProperty());
        itemColumn.setCellValueFactory(new PropertyValueFactory<ToDoItem, String>("details"));
        
        itemColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        itemColumn.setOnEditCommit(new CommitEventHandlerImpl());
        
        completedColumn.setCellValueFactory(cellData -> cellData.getValue().getCompletedProperty());
    }
    
    @FXML
    private void setEndTime(ActionEvent event)
    {
        long currentTime = System.currentTimeMillis();
        endTime.setText(MyDate.longToTime(currentTime));
        logEntry.setFinished(currentTime);
    }

    @FXML
    private void saveLog(ActionEvent event)
    {
        if (logEntry.getStarted() == 0) {
            new FXDialog("You need to set a start time!");
            event.consume();
            return;
        }
        if (logEntry.getFinished() == 0) {
            new FXDialog("You need to set an end time!");
            event.consume();
            return;
        }
        if ("".equals(logDetails.getText()) || logDetails.getText().length() < 3) {
            new FXDialog("You need to enter some details!");
            event.consume();
            return;
        }
        logEntry.setLogName(projectName);

        logEntry.setDetails(logDetails.getText());
        logEntry.setArea(areaText.getText());
        logEntry.setLogId(logId);
        
        //logEntry.persist();        
        PersistanceUnit pu = PersistanceUnit.getPersistanceUnit();
        pu.create(logEntry);
        
        //now save any ToDo items
        Iterator <ToDoItem>i = toDoList.iterator();
        while (i.hasNext()) {
            ToDoItem eachItem = i.next();
            if (eachItem.getLogName() != null) {
                pu.create(eachItem);
            }
        }
        if (logController!=null)
            logController.setLogList();
        // Call cancel() to take us back to log overview
        cancel(null);
    }

    @FXML
    private void cancel(ActionEvent event)
    {
        logEntry = null;
        mainStage.setScene(logScene);
    }

    @FXML
    private void changeEndTime(ActionEvent event)
    {
        String entered_time = endTime.getText();

        long time = MyDate.stringToLongTime(entered_time);

        if (time < 0) {
            time = System.currentTimeMillis();
        }
        logEntry.setFinished(time);
        endTime.setText(MyDate.longToTime(time));
    }
   
    private class CommitEventHandlerImpl implements EventHandler<CellEditEvent<ToDoItem, String>>
    {
       public CommitEventHandlerImpl() { }
        
        @Override
        public void handle(CellEditEvent<ToDoItem, String> t)
        {
            //((ToDoItem) t.getTableView().getItems().get(
            //        t.getTablePosition().getRow())
            //        ).setDetails(t.getNewValue());
            
            TableView <ToDoItem> tableView = t.getTableView();
            ObservableList <ToDoItem>observableList = tableView.getItems();
            TablePosition <ToDoItem, String> tablePosition = t.getTablePosition();
            int row = tablePosition.getRow();
            ToDoItem toDoItem = observableList.get(row);
            String newValue = t.getNewValue();
            String oldValue = t.getOldValue();
            
            // If we're adding a new value
            if ("Enter an item".equals(oldValue))
            {
                // Have they changed and is it something valid?
                if (!("Enter an item".equals(newValue)) && newValue.length()>3)
                {
                    ToDoItem newToDoItem = new ToDoItem(projectName, newValue);
            
                    //Collections.reverse(toDoList);
                    observableList.add(newToDoItem);
                    //Collections.reverse(toDoList);
                }
                toDoItem.setDetails("Enter an item");
            }
            else
                // Else we're updating an old value
            {
                toDoItem.setDetails(newValue);
            }
            System.out.println("New value : "+newValue);
        }
    }

}
