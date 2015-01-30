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
package model;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 *
 * @author tel@tezk.co.uk
 */
public class ProjectDescriptor implements PUInterface, Comparable
{
    private static final String TABLE_NAME = "project_descriptions";
    
    private static final String LOG_NAME_COLUMN = "project_name";
    private static final String DESCRIPTION_COLUMN = "description";
    private static final String LAST_VIEWED_COLUMN = "last_viewed";
    
    private static final String sqlCreationString
            = "create table " + TABLE_NAME + " ("
            + LOG_NAME_COLUMN + " varchar(20) not null primary key,"
            + DESCRIPTION_COLUMN + " varchar(100) null,"
            + LAST_VIEWED_COLUMN + " date null"
            + ")";

    private StringProperty logName;
    private StringProperty description;
    private long lastViewed;
    
    public StringProperty logNameProperty() { return logName; }
    public StringProperty lastDateProperty() {return new SimpleStringProperty(new Date(lastViewed).toString()); }

    //from PUInterface
    @Override
    public boolean persist(Connection c) throws SQLException
    {
        Statement s = c.createStatement();

        String sqlString = "insert into " + TABLE_NAME
                + " values ('"
                + getLogName() + "', '"
                + getDescription() + "', '"
                + new Date(lastViewed) + "')";
        System.out.println("Prepared string = "+sqlString);
        if (s.executeUpdate(sqlString) > 0) {
            return true;
        }
        else {
            return false;
        }

    }

    public ProjectDescriptor()
    {
    }

    public ProjectDescriptor(String aLogName, String aDescription)
    {
        // If we're setting up a new ProjectDescriptor with no date parameter, set date as todays date
        this(aLogName, aDescription, System.currentTimeMillis());
    }

    public ProjectDescriptor(String aLogName, String aDescription, long aLastViewed)
    {
        this.logName = new SimpleStringProperty(aLogName);
        this.description = new SimpleStringProperty(aDescription);
        this.lastViewed = aLastViewed;
    }
    
    public ProjectDescriptor(String aLogName, String aDescription, Date aLastViewed)
    {
         this(aLogName, aDescription, aLastViewed.getTime());       
    }

    public String getLogName()
    {
        return logName.get();
    }

    public void setLogName(String logName)
    {
        this.logName.set(logName);
    }

    public String getDescription()
    {
        return description.get();
    }

    public void setDescription(String description)
    {
        this.description.set(description);
    }

    public Date getLastViewed()
    {
        return new Date(lastViewed);
    }

    public Long getLastViewedLong()
    {
        return lastViewed;
    }
    
    public String getLastViewedSQL()
    {
        return new Date(lastViewed).toString();
    }
    
    public void setLastViewed(Date lastViewed)
    {
        this.lastViewed = lastViewed.getTime();
    }

    public static ArrayList<ProjectDescriptor> parseQuery(ResultSet r)
    {
        // creates an array list of ProjectDescriptors, give an SQL result set (r)
        ArrayList<ProjectDescriptor> myList = new ArrayList();

        if (r != null) {
            try {
                while (r.next()) {
                    String rLogName = r.getString(LOG_NAME_COLUMN);
                    String rDescription = r.getString(DESCRIPTION_COLUMN);
                    
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                   
                    long rDate = format.parse(r.getString(LAST_VIEWED_COLUMN)).getTime();
                    myList.add(new ProjectDescriptor(rLogName, rDescription, rDate));
                }
            }
            catch (SQLException|ParseException e) {
                System.err.println("Error creating project description objects:");
                System.err.println(e.getMessage());
            }
        }
        
        return myList;
    }

    public static String getSQLCreationString()
    {
        return sqlCreationString;
    }
    
   @Override
    public int compareTo(Object o)
    {
        // Sort on date (desc) then project title, ignoring case
        if (o!=null && o instanceof ProjectDescriptor)
        {
            ProjectDescriptor pd=(ProjectDescriptor)o;
            if (!(pd.getLastViewed().equals(this.getLastViewed())))
            {
                return (int)(pd.getLastViewedLong() - this.getLastViewedLong());
            }
            else
                return this.getLogName().toLowerCase().compareTo(pd.getLogName().toLowerCase());
        }   
       throw (new UnsupportedOperationException("Can't compare those types"));     
    }
}
