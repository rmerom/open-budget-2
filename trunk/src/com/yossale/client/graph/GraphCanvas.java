package com.yossale.client.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AreaChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.yossale.client.data.ExpenseRecord;

public class GraphCanvas extends Composite {

  private AreaChart pie;
  private static final Logger logger = Logger.getLogger(GraphCanvas.class.getName());

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
    updateGraph(ExpenseRecord.getExpenseRecordsArray(nodes));
  }
  
  public void updateGraph(ExpenseRecord[] nodes) {
    
    if (nodes == null || nodes.length == 0) {
      return;
    }

    pie.draw(createTable(nodes), createOptions());
    
  }

  private Options createOptions() {
    Options options = Options.create();
    options.setWidth(600);
    options.setHeight(400);
    // options.set3D(true);
    options.setTitle("Government expenses");
    return options;
  }

  private List<ExpenseRecord> summarizeResults(ExpenseRecord[] topics) {

    logger.info("Summarizing results");
    Map<Integer, ExpenseRecord> map = new HashMap<Integer, ExpenseRecord>();

    for (ExpenseRecord t : topics) {
      ExpenseRecord sum = map.get(t.getYear()) == null ? new ExpenseRecord()
          : map.get(t.getYear());
      sum.add(t);
      sum.setYear(t.getYear());
      map.put(t.getYear(), sum);
    }

    return new ArrayList<ExpenseRecord>(map.values());

  }

  private DataTable createTable(ExpenseRecord[] topics) {   

    logger.info("Updating graph with " + (null == topics ? 0 : topics.length) + " topics");
    
    DataTable data = DataTable.create();
    data.addColumn(ColumnType.STRING, "Year");
    data.addColumn(ColumnType.NUMBER, "Net Allocated");
    data.addColumn(ColumnType.NUMBER, "Net Revised");
    data.addColumn(ColumnType.NUMBER, "Net Used");

    if (topics == null || topics.length == 0) {
      return data;
    }

    List<ExpenseRecord> sums = summarizeResults(topics);
    Collections.sort(sums, new Comparator<ExpenseRecord>() {

      @Override
      public int compare(ExpenseRecord o1, ExpenseRecord o2) {
        return (new Integer(o1.getYear()).compareTo(new Integer(o2.getYear())));
      }
    });

    for (ExpenseRecord t : sums) {
      int rowIndex = data.addRow();
      data.setValue(rowIndex, 0, t.getYear() + "");
      data.setValue(rowIndex, 1, t.getNetAmountAllocated());
      data.setValue(rowIndex, 2, t.getNetAmountRevised());
      data.setValue(rowIndex, 3, t.getNetAmountUsed());
    }

    return data;
  }

}
