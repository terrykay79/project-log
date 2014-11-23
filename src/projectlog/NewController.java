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
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.PersistanceUnit;
import model.ProjectDescriptor;
import view.FXDialog;

/**
 * FXML Controller class
 *
 * @author tezk
 */
public class NewController implements Initializable
{

    boolean okPressed = false;
    ProjectDescriptor newProject;

    @FXML
    private TextField newProjectTitle;
    @FXML
    private TextArea newProjectDescription;
    @FXML
    private Button newCreateButton;
    @FXML
    private Button newCancelButton;

    public boolean isOkPressed()
    {
        return okPressed;
    }

    public ProjectDescriptor getNewProject()
    {
        return newProject;
    }

    public void clearFields()
    {
        newProjectTitle.setText(null);
        newProjectDescription.setText(null);
    }
    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb)
    {

    }

    @FXML
    private void saveNewProject(ActionEvent event)
    {
        String title = newProjectTitle.getText();
        String description = newProjectDescription.getText();
        // Tried using class variable set in "ShowStage" but didn't work! Need to get stage from button...
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();

        if ("".equals(title)) {
            // No blank title! Silly!
            new FXDialog("Please set a project title!");
        }
        else {
            newProject = new ProjectDescriptor(title, description);

           // try {
                PersistanceUnit.getPersistanceUnit().create(newProject);
            //}
            //catch (SQLException e) {
            //    System.err.println("Error saving : " + e.getMessage());
           // }

            stage.hide();
        }
        okPressed = true;

    }

    @FXML
    private void cancelNewProject(ActionEvent event)
    {
        // Tried using class variable set in "ShowStage" but didn't work! Need to get stage from button...
        Node source = (Node) event.getSource();
        Stage stage = (Stage) source.getScene().getWindow();
        stage.hide();
        okPressed = false;
    }

}
