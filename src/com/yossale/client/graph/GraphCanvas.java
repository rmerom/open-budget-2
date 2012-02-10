package com.yossale.client.graph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.visualization.client.AbstractDataTable.ColumnType;
import com.google.gwt.visualization.client.DataTable;
import com.google.gwt.visualization.client.VisualizationUtils;
import com.google.gwt.visualization.client.visualizations.corechart.AreaChart;
import com.google.gwt.visualization.client.visualizations.corechart.CoreChart;
import com.google.gwt.visualization.client.visualizations.corechart.Options;
import com.smartgwt.client.data.Record;
import com.yossale.client.data.SectionRecord;

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

  public void updateGraph(List<SectionRecord> nodes) {

    if (nodes == null || nodes.isEmpty()) {
      return;
    }

    pie.draw(createTable(nodes), createOptions());
  }

  private Options createOptions() {
    Options options = Options.create();
    options.setWidth(600);
    options.setHeight(400);
    // options.set3D(true);
    options.setTitle("Government sections");
    return options;
  }

  private List<SectionRecord> summarizeResults(List<SectionRecord> topics) {

    Map<Integer, SectionRecord> map = new HashMap<Integer, SectionRecord>();

    for (SectionRecord t : topics) {
      SectionRecord sum = map.get(t.getYear()) == null ? new SectionRecord()
          : map.get(t.getYear());
      sum.add(t);
      sum.setYear(t.getYear());
      map.put(t.getYear(), sum);
    }

    return new ArrayList<SectionRecord>(map.values());

  }

  private DataTable createTable(List<SectionRecord> topics) {

    System.out.println("Updating graph");

    DataTable data = DataTable.create();
    data.addColumn(ColumnType.STRING, "Year");
    data.addColumn(ColumnType.NUMBER, "Net Allocated");
    data.addColumn(ColumnType.NUMBER, "Net Revised");
    data.addColumn(ColumnType.NUMBER, "Net Used");

    if (topics == null || topics.isEmpty()) {
      return data;
    }

    List<SectionRecord> sums = summarizeResults(topics);
    Collections.sort(sums, new Comparator<SectionRecord>() {

      @Override
      public int compare(SectionRecord o1, SectionRecord o2) {
        return (new Integer(o1.getYear()).compareTo(new Integer(o2.getYear())));
      }
    });

    for (SectionRecord t : sums) {
      int rowIndex = data.addRow();
      data.setValue(rowIndex, 0, t.getYear() + "");
      data.setValue(rowIndex, 1, t.getNetAmountAllocated());
      data.setValue(rowIndex, 2, t.getNetAmountRevised());
      data.setValue(rowIndex, 3, t.getNetAmountUsed());
    }

    return data;
  }

}
