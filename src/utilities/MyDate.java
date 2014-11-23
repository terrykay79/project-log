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
package utilities;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author tezk
 */
public class MyDate
{

    public static String longToTime(long time)
    {
        /** Returns time in the format HH:mm from a given time in long */
        Date date = new Date(time);

        SimpleDateFormat ft = new SimpleDateFormat("HH:mm");
        return (ft.format(date));
    }

    public static long stringToLongTime(String time)
    {
        /** Given time in format HH:mm or HH.mm, converts to long value for that time today */
        SimpleDateFormat ft = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date t = null;
        // Cheating way to get text value of today... format yyyy-mm-dd
        java.sql.Date sqlDate=new java.sql.Date(System.currentTimeMillis());
        
        time = sqlDate+" "+time;
        System.out.println(time);
        try {
            t = ft.parse(time);
            System.out.println(t);
        }
        catch (ParseException e) {
            try {
                ft = new SimpleDateFormat("yyyy-MM-dd HH.mm");
                t = ft.parse(time);
                System.out.println(t);
            }
            catch (ParseException ed) {
                System.out.println("Unparseable using " + ft);
                return (long)-1;
            }
        }
        return t.getTime();

    }
}
