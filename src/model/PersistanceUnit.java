package model;

import model.ProjectDescriptor;
import model.PUInterface;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Deals with underlying SQLite database for storage and retrieval
 *
 * @author tel@tezk.co.uk
 */
public class PersistanceUnit
{

    private static Connection conn = null;
    private static PersistanceUnit pu = null;

    private ArrayList<ProjectDescriptor> projects = null;

    private PersistanceUnit()
    {
        // Set up our connection, load initial log descriptions
        try {
            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection("jdbc:sqlite:projectlog.db");
        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            //TODO System.exit is a bit harsh, show error dialog before quitting
            System.exit(0);
        }

        // database name is projectlog.db, contains table log_descriptions that describes the projects the log contains
        // check for existance and create table if not there
        Statement s = null;
        ResultSet executeQuery = null;
        try {
            s = conn.createStatement();
            // raises SQLException if table not found - e.getErrorCode() == 1
            executeQuery = s.executeQuery("SELECT * FROM project_descriptions");
        }
        catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                // Table not there, create!
                try {
                    s.executeUpdate(ProjectDescriptor.getSQLCreationString());
                }
                catch (SQLException ne) {
                    System.err.println("Cannot create log descriptor table!" + e.getMessage());
                    System.exit(0);
                }
            }
        }
        if (s != null) {
            try {
                // Table is now there, if executeQuery == null, we have no projects set up, otherwise read and store!
                if (executeQuery != null) {
                    projects = ProjectDescriptor.parseQuery(executeQuery);
                }
                else {
                    projects = new ArrayList();
                }
                s.close();
            }
            catch (SQLException e) {
                System.err.println("Error reading log descriptions");
            }
        }
        else {
            System.err.println("Error preparing Statement");
        }
        // Is LogEntry database there?
        try {
            s = conn.createStatement();
            // raises SQLException if table not found - e.getErrorCode() == 1
            executeQuery = s.executeQuery("SELECT * FROM log_entries");
        }
        catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                // Table not there, create!
                try {
                    System.out.println("Trying to create log_entries : "+LogEntry.getSQLCreationString());
                    s.executeUpdate(LogEntry.getSQLCreationString());
                    
                }
                catch (SQLException ne) {
                    System.err.println("Cannot create log entry table!" + e.getMessage());
                    System.exit(0);
                }
            }
        }
        
        // Is ToDo database there?
        try {
            s = conn.createStatement();
            // raises SQLException if table not found - e.getErrorCode() == 1
            executeQuery = s.executeQuery("SELECT * FROM todo");
        }
        catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                // Table not there, create!
                try {
                    System.out.println("Trying to create todo : "+ToDoItem.getSQLCreationString());
                    s.executeUpdate(ToDoItem.getSQLCreationString());
                    
                }
                catch (SQLException ne) {
                    System.err.println("Cannot create to do item table!" + e.getMessage());
                    System.exit(0);
                }
            }
        }
    }

    public ArrayList<LogEntry> getLogs(String projectName)
    {
        // find all logs for given project
        ArrayList<LogEntry> logs = null;

        Statement s = null;
        ResultSet executeQuery = null;
        try {
            s = conn.createStatement();
            // raises SQLException if table not found - e.getErrorCode() == 1
            executeQuery = s.executeQuery("SELECT * FROM log_entries where log_name='"+projectName+"'");
        }
        catch (SQLException e)
        {
            System.err.println("Error reading logs from DB : "+e.getMessage());
        }
        if (executeQuery!=null)
        {
            ArrayList aList = LogEntry.parseQuery(executeQuery);
            Collections.reverse(aList);
            return aList;
        }
        else
           return new ArrayList();
    }

    public ArrayList<ProjectDescriptor> getProjects()
    {
        return projects;
    }

    public void setProjects(ArrayList<ProjectDescriptor> projects)
    {
        this.projects = projects;
    }

    public void create(PUInterface what) 
    {
        try {
            what.persist(conn);
        }
        catch (SQLException e) {
            System.err.println("Error saving log : " + e.getMessage());
        
        }
    }

    public String getName()
    {
        try {
            return "Contents: " + conn.getCatalog();
        }
        catch (Exception e) {
            System.err.println(e.getMessage());
        }
        return ("fail");
    }

    public static PersistanceUnit getPersistanceUnit()
    {
        if (pu == null) {
            pu = new PersistanceUnit();
        }
        return pu;
    }

    public static Connection getConn()
    {
        return conn;
    }

    public static void setConn(Connection conn)
    {
        PersistanceUnit.conn = conn;
    }
    
    
}

