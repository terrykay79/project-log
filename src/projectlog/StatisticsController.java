/*
 * Copyright (C) 2015 tezk
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

import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.StackedBarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import model.LogEntry;
import utilities.MyDate;

/**
 * FXML Controller class
 *
 * @author tezk
 */
public class StatisticsController implements Initializable {
    @FXML
    private PieChart pieChart;
    @FXML
    private StackedBarChart<String, Integer> barChart;
    @FXML
    private TextArea textArea;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO

    }    
 
    public void setCharts(ObservableList <LogEntry>logs)
    {
        ObservableList <PieChart.Data> dataList = FXCollections.observableArrayList();
        Map <String, Long> areaTotals = new HashMap(); // Area, Time spent
        Map <String, XYChart.Series<String, Integer>> barChartData = new HashMap(); // Area, <Date, Time spent>
      
        // Process all log entries - String is area (DB, GUI) Long is ms spent
       long totalTime = 0;
        Iterator <LogEntry> i = logs.iterator();
        while (i.hasNext()) {
            LogEntry eachLog = i.next();
            String eachArea = eachLog.getArea();
            long timeSpent = (eachLog.getFinished() - eachLog.getStarted() + 59999) / (1000 * 60);
            totalTime+=timeSpent;
            // PieChart
            if  (areaTotals.get(eachArea)==null) areaTotals.put(eachArea, new Long(0));
            areaTotals.put(eachArea, areaTotals.get(eachArea) + timeSpent);
            // BarChart
            if (barChartData.get(eachArea)==null) {
                XYChart.Series <String, Integer> newSeries= new XYChart.Series();
                newSeries.setName(eachArea);
                barChartData.put(eachArea, newSeries);
            }
            barChartData.get(eachArea).getData().add(new XYChart.Data(MyDate.longToDate(eachLog.getStarted()), timeSpent));
        }
        
        StringBuilder textOutput = new StringBuilder("Spent a total of "+totalTime+" minutes working.\n");
        // Build the Pie Chart data list
        Iterator <String> i2 = areaTotals.keySet().iterator();
        while (i2.hasNext()) {
            String eachKey = i2.next();
            dataList.add(new PieChart.Data(eachKey, areaTotals.get(eachKey)));
            barChart.getData().add(barChartData.get(eachKey));
            textOutput.append("Area "+eachKey+" = "+areaTotals.get(eachKey)+" minutes\n");
        }
        pieChart.setStartAngle(90);
        pieChart.setData(dataList);
        
        textArea.setText(textOutput.toString());
    }
    
}
