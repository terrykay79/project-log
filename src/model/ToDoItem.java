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
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import utilities.MyDate;

/**
 *
 * @author tezk
 */
public class ToDoItem implements PUInterface
{

    // todo(_log_name, todo_id,_ created, details, completed) //
    private static final String ROOT_TABLE = "log_descriptions";
    private static final String TABLE_NAME = "todo";
    private static final String LOG_NAME_COLUMN = "log_name";
    private static final String LOG_ID_COLUMN = "log_id";
    private static final String TODO_ID_COLUMN = "todo_id";
    private static final String CREATED_COLUMN = "created";
    private static final String DETAILS_COLUMN = "details";
    private static final String COMPLETED_COLUMN = "completed";

    private static final String sqlCreationString
            = "create table " + TABLE_NAME + " ("
            + LOG_NAME_COLUMN + " varchar(20) not null, "
            + LOG_ID_COLUMN + " integer, "
            + TODO_ID_COLUMN + " integer primary key autoincrement, " // Should be auto increment.. Will need to change code
            + CREATED_COLUMN + " bigint not null, "
            + DETAILS_COLUMN + " varchar(1000) not null, "
            + COMPLETED_COLUMN + " bigint ,"
            + "foreign key (" + LOG_NAME_COLUMN + ") references " + ROOT_TABLE + "(" + LOG_NAME_COLUMN + ")"
            + ")";

    public static String getSQLCreationString()
    {
        return sqlCreationString;
    }

    private StringProperty logName;
    private int logId;
    private StringProperty details;
    private long created;
    private long completed = 0;
    private int toDoId;

    public ToDoItem()
    {
        logName = new SimpleStringProperty();
        details = new SimpleStringProperty();
        created = System.currentTimeMillis();
    }

    public ToDoItem(String aLogName)
    {
        this();
        this.setLogName(aLogName);
    }

    public ToDoItem(String aLogName, String someDetails)
    {
        this(aLogName);
        this.setDetails(someDetails);
    }

    public int getToDoId()
    {
        return toDoId;
    }

    public void setToDoId(int toDoId)
    {
        this.toDoId = toDoId;
    }

    public StringProperty getLogNameProperty()
    {
        return logName;
    }

    public String getLogName()
    {
        return logName.get();
    }

    public void setLogNameProperty(StringProperty logName)
    {
        this.logName = logName;
    }

    public void setLogName(String logName)
    {
        this.logName.set(logName);
    }

    public int getLogId()
    {
        return logId;
    }

    public void setLogId(int logId)
    {
        this.logId = logId;
    }

    public StringProperty getDetailsProperty()
    {
        return details;
    }

    public String getDetails()
    {
        return details.get();
    }

    public void setDetailsProperty(StringProperty details)
    {
        this.details = details;
    }

    public void setDetails(String details)
    {
        this.details.set(details);
    }

    public long getCreated()
    {
        return created;
    }

    public StringProperty getCreatedProperty()
    {
        return new SimpleStringProperty((new Date(created)).toString());
    }

    public void setCreated(long created)
    {
        this.created = created;
    }

    public void setCreated(Date created)
    {
        this.created = created.getTime();
    }

    public long getCompleted()
    {
        return completed;
    }

    public StringProperty getCompletedProperty()
    {
        return new SimpleStringProperty(completed == 0 ? "" : (new Date(completed)).toString());
    }

    public void setCompleted(long completed)
    {
        this.completed = completed;
    }

    public void setCompleted(Date completed)
    {
        this.completed = completed.getTime();
    }

    //from PUInterface
    @Override
    public boolean persist(Connection c) throws SQLException
    {
        // todo(_log_name, todo_id,_ created, details, completed) //
        // Only store log_name, created, details - todo-id is auto inc and completed updated later
        Statement s = c.createStatement();

        String sqlString = "select * from " + TABLE_NAME + " where " + LOG_NAME_COLUMN + " = \""
                + getLogName() + "\" and "
                + TODO_ID_COLUMN + " = \"" + getToDoId() + "\"";

        System.out.println(sqlString);
        
        ResultSet results = s.executeQuery(sqlString);

        if (results.next() == false) {
            // Use insert
            sqlString = "insert into " + TABLE_NAME
                    + " (" + LOG_NAME_COLUMN + ", " + LOG_ID_COLUMN + ", " + CREATED_COLUMN + ", " + DETAILS_COLUMN + ")values ('"
                    + getLogName() + "', "
                    + getLogId() + ", '"
                    + getCreated() + "', '"
                     
                    + getDetails() + "')";

        }
        else { // Use update
            sqlString = "update " + TABLE_NAME
                    + " set " + DETAILS_COLUMN + " = \"" + getDetails() + "\", "
                    + COMPLETED_COLUMN + "= \"" + getCompleted() + "\""
                    + " where " + LOG_NAME_COLUMN + " = \"" + getLogName() + "\""
                    + " and " + TODO_ID_COLUMN + " = \"" + getToDoId() + "\"";
        }
        System.out.println("Prepared string = " + sqlString);
        if (s.executeUpdate(sqlString) > 0) {
            return true;
        }
        else {
            return false;
        }
    }

    public StringProperty getDetailsForDisplay()
    {
        return details;
    }

    public StringProperty getCompletedDetailsForDisplay()
    {
        StringProperty c = new SimpleStringProperty();
        if (getCompleted()!=0)
            c.setValue(MyDate.longToDate(getCompleted()));
        
        return c;
    }

    
    public static List<ToDoItem> getToDoItems(Connection c, String project) throws SQLException
    {
        ArrayList<ToDoItem> myList = new ArrayList();

        Statement s = c.createStatement();

        String sqlString = "select * from " + TABLE_NAME;
        if (project != null && project.length() > 0) {
            sqlString += " where " + LOG_NAME_COLUMN + " = \"" + project + "\"";
        }
        System.out.println(sqlString);
        System.out.println(project);
        ResultSet results = s.executeQuery(sqlString);

        while (results.next()) {
            ToDoItem anItem = new ToDoItem();
            anItem.setLogName(results.getString(LOG_NAME_COLUMN));
            anItem.setLogId(results.getInt(LOG_ID_COLUMN));
            anItem.setToDoId(results.getInt(TODO_ID_COLUMN));
            anItem.setCreated(results.getLong(CREATED_COLUMN));
            anItem.setDetails(results.getString(DETAILS_COLUMN));
            anItem.setCompleted(results.getLong(COMPLETED_COLUMN));

            myList.add(anItem);
        }

        return myList;
    }
}
