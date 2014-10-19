/*
 * Freely distributable

 */
package model;

import java.sql.Connection;
import java.sql.SQLException;

/**
 *
 * @author tezk
 */
public interface PUInterface
{
    public boolean persist(Connection c) throws SQLException; // writes an inbstance of object to table
    
}
