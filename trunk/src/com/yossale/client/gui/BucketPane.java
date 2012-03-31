package com.yossale.client.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.SelectionStyle;
import com.smartgwt.client.widgets.grid.ListGrid;
import com.smartgwt.client.widgets.grid.ListGridField;
import com.smartgwt.client.widgets.grid.ListGridRecord;
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

  public ListGridRecord[] getGridRecords() {
  	return grid.getRecords();
  }
  
  public void addExpenses(List<ExpenseRecord> expenses) {
    addExpenses(ExpenseRecord.getExpenseRecordsArray(expenses));
  }

  public void addExpenses(ExpenseRecord[] expenses) {
  	Set<String> expenseCodes = new HashSet<String>();
    for (ExpenseRecord exp : expenses) {
    	if (!expenseCodes.contains(exp.getExpenseCode())) {
    		expenseCodes.add(exp.getExpenseCode());
    		grid.addData(ExpenseRecord.getRecord(exp));
    		graphCanvas.updateGraph(expenses);
    	}
    }
  }
  
  public void clearExpenses() {
  	grid.setData(new Record[]{});
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

  public void addExpenses(String[] expenseCodes, String[] yearStrings) {

    List<Integer> years = new ArrayList<Integer>();
    for (Integer i = 0; i < yearStrings.length; i++) {
      years.add(Integer.parseInt(yearStrings[i]));
    }
    addExpenses(expenseCodes, years);
  }

  public void addExpenses(String[] expenseCodes, List<Integer> years) {
    expensesService.getExpensesByCodeAndYears(expenseCodes, years.toArray(new Integer[]{}),
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
