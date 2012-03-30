package com.yossale.client.gui;

import java.util.LinkedList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.layout.HLayout;
import com.yossale.client.actions.ExpenseService;
import com.yossale.client.actions.ExpenseServiceAsync;
import com.yossale.client.data.ExpenseRecord;
import com.yossale.client.graph.GraphCanvas;
import com.allen_sauer.gwt.log.client.Log;

public class BucketPane extends HLayout {

  private final ListGrid grid;
  private final GraphCanvas graphCanvas;
  private final ExpenseServiceAsync expensesService = GWT
      .create(ExpenseService.class);

  public BucketPane(GraphCanvas graph) {
    this(graph, new LinkedList<ExpenseRecord>());
  }

  public BucketPane(GraphCanvas graph, List<ExpenseRecord> expenses) {
    grid = generateList();
    addExpenses(expenses);
    this.graphCanvas = graph;
    addMember(grid, 0);
  }

  public void addExpenses(List<ExpenseRecord> expenses) {
    addExpenses(ExpenseRecord.getExpenseRecordsArray(expenses));
  }

  public void addExpenses(ExpenseRecord[] expenses) {
    for (ExpenseRecord exp : expenses) {
      grid.addData(ExpenseRecord.getRecord(exp));
      graphCanvas.updateGraph(expenses);
    }
  }

  private ListGrid generateList() {

    final ListGrid sectionsList = new ListGrid();
    sectionsList.setWidth(500);
    sectionsList.setHeight(224);
    sectionsList.setShowAllRecords(true);
    sectionsList.setSelectionType(SelectionStyle.SINGLE);

    ListGridField nameField = new ListGridField("name", "Name");
    ListGridField codeField = new ListGridField("expenseCode", "Code");
    sectionsList.setFields(codeField, nameField);

    sectionsList.setData(new Record[] {});
    return sectionsList;
  }

  public void addSection(String expenseId, String[] values) {

    Integer[] years = new Integer[values.length];
    for (Integer i = 0; i < values.length; i++) {
      years[i] = Integer.parseInt(values[i]);
    }

    expensesService.getExpensesByCodeAndYears(expenseId, years,
        new AsyncCallback<ExpenseRecord[]>() {

          @Override
          public void onSuccess(ExpenseRecord[] result) {
            Log.info("Retrieved " + result.length + " ExpenseRecords");
            addExpenses(result);
          }

          @Override
          public void onFailure(Throwable caught) {
            Log.info("Failed to retrieve ExpenseRecords", caught);
          }
        });
  }
}
