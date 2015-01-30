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
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import utilities.MyDate;

/**
 *
 * @author tezk
 */
public class LogEntry implements PUInterface
{

    // log_entries(_log_name_, created, details) //
    private static final String ROOT_TABLE = "project_descriptions";

    private static final String TABLE_NAME = "log_entries";

    private static final String LOG_NAME_COLUMN = "log_name";
    private static final String LOG_ID_COLUMN = "log_id";
    private static final String STARTED_COLUMN = "created";
    private static final String FINISHED_COLUMN = "finished";
    private static final String AREA_COLUMN = "area";
    private static final String DETAILS_COLUMN = "log_details";

    private static final String sqlCreationString
            = "create table " + TABLE_NAME + " ("
            + LOG_NAME_COLUMN + " varchar(20) not null,"
            + LOG_ID_COLUMN + " integer primary key autoincrement,"
            + STARTED_COLUMN + " bigint not null,"
            + FINISHED_COLUMN + " bigint not null,"
            + AREA_COLUMN + " varchar(5) not null,"
            + DETAILS_COLUMN + " varchar(1000),"
            + "constraint fk_log_name foreign key (" + LOG_NAME_COLUMN + ") references " + ROOT_TABLE + "(" + LOG_NAME_COLUMN + ")"
            + ")";

    private StringProperty logName;
    private int logId;
    private StringProperty details;
    private long started;
    private long finished;
    private String area;

    public LogEntry()
    {
        started = 0;
        finished = 0;
        logId = 0;
        logName = new SimpleStringProperty();
        details = new SimpleStringProperty();
    }

    public static String getSQLCreationString()
    {
        return sqlCreationString;
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

    public StringProperty getDetailsForDisplay()
    {
        StringBuilder aString=new StringBuilder();
        aString.append(new Date(this.getStarted()));
        aString.append(":\n");
        aString.append(this.getDetails());
        
        return new SimpleStringProperty(aString.toString());
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

    public long getStarted()
    {
        return started;
    }

    public StringProperty getStartedProperty()
    {
        return new SimpleStringProperty(MyDate.longToTime(started));
    }

    public void setStarted(long started)
    {
        this.started = started;
    }

    public void setStarted(Date started)
    {
        this.started = started.getTime();
    }

    public long getFinished()
    {
        return finished;
    }

    public void setFinished(long finished)
    {
        this.finished = finished;
    }

    public String getArea()
    {
        return area;
    }

    public void setArea(String area)
    {
        this.area = area;
    }

    public static ArrayList<LogEntry> parseQuery(ResultSet r)
    {
        ArrayList<LogEntry> myList = new ArrayList();

        if (r != null) {
            try {
                while (r.next()) {
                    LogEntry aLog = new LogEntry();

                    aLog.setLogName(r.getString(LOG_NAME_COLUMN));
                    aLog.setLogId((int)r.getLong(LOG_ID_COLUMN));
                    aLog.setStarted(r.getLong(STARTED_COLUMN));
                    aLog.setFinished(r.getLong(FINISHED_COLUMN));
                    aLog.setArea(r.getString(AREA_COLUMN));
                    aLog.setDetails(r.getString(DETAILS_COLUMN));
                    myList.add(aLog);
                }
            }
            catch (SQLException e) {
                System.err.println("Cannot process logs : " + e.getMessage());
                return null;
            }
        }
        return myList;
    }

    //from PUInterface

    @Override
    public boolean persist(Connection c) throws SQLException
    {
        // log_entries(_log_name_, created, details) //
        Statement s = c.createStatement();

        // if log_id == 0, new log otherwise update
        
        String sqlString;
        if (getLogId() == 0) sqlString = "insert into " + TABLE_NAME
                + "(" + LOG_NAME_COLUMN + ", " + STARTED_COLUMN + ", " + FINISHED_COLUMN + ", " + AREA_COLUMN + ", " + DETAILS_COLUMN + ") "
                + " values ('"
                + getLogName() + "', "
                + getStarted() + ", "
                + getFinished() + ", '"
                + getArea() + "', '"
                + getDetails() + "')";
        else sqlString = "update " + TABLE_NAME + " set "   // TODO: No changes saved to completed time
                 + AREA_COLUMN + " = '" + getArea() + "', "
                + DETAILS_COLUMN + " = '" + getDetails() + "' "
                + " where " + LOG_ID_COLUMN + " = '" + getLogId() +"' ";
        if (s.executeUpdate(sqlString) > 0) {
            return true;
        }
        else {
            return false;
        }

    }
    
    public static LogEntry find(String logName, int logId)
    {
        Connection c = PersistanceUnit.getConn();
        try {
            Statement s = c.createStatement();
            String sqlString = "select * from '"+TABLE_NAME+"' where "+LOG_ID_COLUMN+" = '"+logId+"' and "+LOG_NAME_COLUMN+" = '"+logName+"'";
            LogEntry aLog = parseQuery(s.executeQuery(sqlString)).get(0);
            return aLog;
        } catch (SQLException e) {
            System.err.println("Error finding log : " + e.getMessage());
            return null;
        }
        
    }
}
