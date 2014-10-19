package model;

import model.ProjectDescriptor;
import model.PUInterface;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

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
        // Set up our connection
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
            executeQuery = s.executeQuery("SELECT * FROM log_descriptions");
        }
        catch (SQLException e) {
            if (e.getErrorCode() == 1) {
                // Table not there, create!
                try {
                    s.executeUpdate(ProjectDescriptor.getSQLCreationString());
                    s.executeUpdate("insert into log_descriptions values('ProjectLog','Log for this project',date('2014-10-18'))");

                }
                catch (SQLException ne) {
                    System.err.println("Cannot create log descriptor table!" + e.getMessage());
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

    }

    public ArrayList<ProjectDescriptor> getProjects()
    {
        return projects;
    }

    public void setProjects(ArrayList<ProjectDescriptor> projects)
    {
        this.projects = projects;
    }

    public void create(PUInterface what) throws SQLException
    {
        what.persist(conn);
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
}
