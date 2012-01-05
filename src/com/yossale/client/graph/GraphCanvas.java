package com.yossale.client.graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AreaChart;
import com.google.gwt.visualization.client.visualizations.corechart.AxisOptions;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.yossale.client.data.ExpenseRecord;

public class GraphCanvas extends Composite {

  private AreaChart pie;

  /* The pie creation part should be in a different class * */
  
  public GraphCanvas() {
    
    final VerticalPanel widget = new VerticalPanel();
    initWidget(widget);
    widget.addStyleName("demo-Composite");
    
    Runnable onLoadCallback = new Runnable() {
      public void run() {        
        pie = new AreaChart(createTable(null), createOptions());        
        widget.add(pie);
      }
    };

    VisualizationUtils.loadVisualizationApi(onLoadCallback, CoreChart.PACKAGE);

  }
  
  public void updateGraph(List<ExpenseRecord> nodes) {

    
    if (nodes == null || nodes.isEmpty()) {    	
    	return;    	
    }      
    
    pie.draw(createTable(nodes), createOptions());
  }

  private Options createOptions() {
    Options options = Options.create();
    options.setWidth(600);
    options.setHeight(400);
//     options.set3D(true);
    options.setTitle("Government expenses");    
    return options;
  }
  
  private List<ExpenseRecord> summarizeResults(List<ExpenseRecord> topics) {
	  
	  Map<Integer, ExpenseRecord> map = new HashMap<Integer, ExpenseRecord>();
	  
	  for(ExpenseRecord t : topics) {		  
		  ExpenseRecord sum = map.get(t.getYear()) == null ? new ExpenseRecord() : map.get(t.getYear());		  
		  sum.add(t);
		  sum.setYear(t.getYear());
		  map.put(t.getYear(), sum);		  
	  }
	  
	  return new ArrayList<ExpenseRecord>(map.values());
	  
  }

  private DataTable createTable(List<ExpenseRecord> topics) {
	  
	System.out.println("Updating graph");
	
	
    DataTable data = DataTable.create();
    data.addColumn(ColumnType.STRING, "Year");
    data.addColumn(ColumnType.NUMBER, "Net Gross Allocated");
    data.addColumn(ColumnType.NUMBER, "Net Net Allocated");
    data.addColumn(ColumnType.NUMBER, "Net Gross Used");   
    
    if (topics == null || topics.isEmpty()) {
      return data;
    }
    
    List<ExpenseRecord> sums = summarizeResults(topics);

    for (ExpenseRecord t : sums) {
      int rowIndex = data.addRow();
      data.setValue(rowIndex, 0, t.getYear()+"");
      data.setValue(rowIndex, 1, t.getGrosAmountAllocated());
      data.setValue(rowIndex, 2, t.getNetAmountAllocated());
      data.setValue(rowIndex, 3, t.getGrossAmountUsed());
    }
    
    return data;
  }



}
