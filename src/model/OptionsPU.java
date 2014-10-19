/*
 * Freely distributable

 */
package model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 *
 * @author tezk
 */
public class OptionsPU implements PUInterface
{
    
    @Override
    public boolean persist(Connection c) throws SQLException
            
    {
        String myStatement="string";
        PreparedStatement s = c.prepareStatement(myStatement);
        
                
        
        return true;
    }
}
