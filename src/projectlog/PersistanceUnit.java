/*
 * Freely distributable

 */
package projectlog;

import java.sql.Connection;
import java.sql.DriverManager;

/**
 *
 * @author tezk
 */
public class PersistanceUnit
{
    static Connection c = null;
    static PersistanceUnit pu = null;
    
    private PersistanceUnit()
    {
        try {
            Class.forName("org.sqlite.JDBC");
            c = DriverManager.getConnection("jdbc:sqlite:test.db");
        }
        catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }

    public String getName()
    {
        try {
            return "Contents: "+c.getCatalog();
        } catch (Exception e) {System.err.println(e.getMessage());};
        return("fail");
    }
    
    public static PersistanceUnit getPersistanceUnit()
    {
        if (pu==null)
            pu = new PersistanceUnit();
        else 
            System.out.println("Behave yuor self!");

        return pu;
    }
}
