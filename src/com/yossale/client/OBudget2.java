package com.yossale.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.PopupPanel;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.DisplayNodeType;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectOtherItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.tree.DataChangedEvent;
import com.smartgwt.client.widgets.tree.DataChangedHandler;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.yossale.client.actions.ExpenseService;
import com.yossale.client.actions.ExpenseServiceAsync;
import com.yossale.client.actions.LoginService;
import com.yossale.client.actions.LoginServiceAsync;
import com.yossale.client.data.ExpenseRecord;
import com.yossale.client.data.LoginInfo;
import com.yossale.client.datastore.OneYearBudgetDataSource;
import com.yossale.client.datastore.TestDS;
import com.yossale.client.graph.GraphCanvas;
import com.yossale.client.gui.dataobj.ExpenseRecordTreeNode;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OBudget2 implements EntryPoint {

  private static final String versionId = "0.51";

  private TreeGrid budgetTree;
  private TreeGrid topicsList;
  private final GraphCanvas graph = new GraphCanvas();
  private final ExpenseServiceAsync expensesService = GWT
      .create(ExpenseService.class);

  /***/

  private TreeGrid generateBudgetExpensesTreeGrid() {

    TreeGrid budgetTree = new TreeGrid();
    budgetTree.setWidth(500);
    budgetTree.setHeight(400);
    budgetTree.setShowOpenIcons(false);
    budgetTree.setShowDropIcons(false);
    budgetTree.setClosedIconSuffix("");
    budgetTree.setFields(new TreeGridField("Name"), new TreeGridField("Year"));
    // budgetTree.setData(generateSimpleTreeGrid("2002"));
    budgetTree.setSize("400", "400");

    return budgetTree;
  }

  public TreeGrid generateGridList() {

    TreeGridField codeField = new TreeGridField("code", "Code");
    codeField.setIncludeInRecordSummary(false);

    TreeGridField itemDescriptionField = new TreeGridField("title", "Title");

    TreeGridField grossAllocated = new TreeGridField("gross_allocated",
        "gross_allocated");

    final TreeGrid listGrid = new TreeGrid();

    listGrid.setWidth(600);
    listGrid.setHeight(520);
    listGrid.setAutoFetchData(false);

    listGrid.setShowAllRecords(false);
    listGrid.setCanEdit(false);
    listGrid.setGroupStartOpen(GroupStartOpen.NONE);
    listGrid.setShowGridSummary(false);
    listGrid.setShowGroupSummary(false);

    listGrid.setFields(itemDescriptionField, codeField, grossAllocated);
    listGrid.setCanAcceptDrop(true);
    listGrid.setCanDragRecordsOut(true);
    listGrid.setCanAcceptDroppedRecords(true);
    listGrid.setDragDataAction(DragDataAction.MOVE);
    listGrid.setCanRemoveRecords(true);

    return listGrid;
  }

  @Override
  public void onModuleLoad() {
    LoginServiceAsync loginService = GWT.create(LoginService.class);
    loginService.login(GWT.getModuleBaseURL(), new AsyncCallback<LoginInfo>() {
      public void onFailure(Throwable error) {
      }

      public void onSuccess(LoginInfo result) {
        loadOBudget(result);
      }
    });

    // getAsyncExpenseForYear(2002);

  }

  private void updateBudgetTreeData(final String year) {

    final List<ExpenseRecordTreeNode> treeRecs = new ArrayList<ExpenseRecordTreeNode>();
    System.out.println("Trying to retrieve information for year: " + year);
     

    expensesService.getExpenses(Integer.parseInt(year),
        new AsyncCallback<ExpenseRecord[]>() {

          @Override
          public void onSuccess(ExpenseRecord[] result) {
            System.out.println("Call to server for year : " + year + " succeded");
            if (result == null || result.length == 0 ) {
              return;
            }
            
            for (ExpenseRecord record : result) {
              treeRecs.add(new ExpenseRecordTreeNode(record));              
            }
          }

          @Override
          public void onFailure(Throwable caught) {
            System.out.println("Call to server for year : " + year + " failed");
          }
        });

    /**
     * In the data model you need to have something which extends "TreeNode",
     * Which is very trivial..
     * 
     * Since we want to give it all the records and let him figure the
     * Hierarchy, we need to tell him 2 thing : id and parentId. This is defined
     * in the object itself (here it's at the ExpenseRecord)
     * 
     * So after you have a list of items, each knows who is father is and what
     * is it's ID, you just provide them to the model as an array, and it'll
     * take care of the rest.
     */

    Tree expensesTreeModel = new Tree();
    expensesTreeModel.setModelType(TreeModelType.PARENT);
    expensesTreeModel.setNameProperty("Name");
    // expensesTreeModel.setChildrenProperty("directReports");

    ExpenseRecordTreeNode[] recArr = new ExpenseRecordTreeNode[] {};
    expensesTreeModel.setData(treeRecs.toArray(recArr));
    budgetTree.setData(recArr);
  }

  private void loadOBudget(LoginInfo loginInfo) {

    budgetTree = generateBudgetExpensesTreeGrid();
    topicsList = generateGridList();

    final DynamicForm form = new DynamicForm();
    form.setWidth(250);

    SelectOtherItem selectOtherItem = new SelectOtherItem();
    selectOtherItem.setOtherTitle("Other..");
    selectOtherItem.setOtherValue("OtherVal");

    selectOtherItem.setTitle("Select year");
    selectOtherItem.setValueMap("2001", "2002", "2003");
    selectOtherItem.addChangedHandler(new ChangedHandler() {

      @Override
      public void onChanged(ChangedEvent event) {
        /**
         * Since we want to update on value change, we just replace the
         * TreeGrid's data with a new data (remember, the model is an Object of
         * type Tree), and it refreshes alone.
         */

        String val = (String) event.getValue();
        updateBudgetTreeData(val);
      }
    });

    form.setFields(selectOtherItem);

    HStack stack = new HStack();
    stack.addMember(generateBudgetExpensesTreeGrid());
    stack.addMember(topicsList);

    topicsList.getData().addDataChangedHandler(new DataChangedHandler() {

      @Override
      public void onDataChanged(DataChangedEvent event) {

        RecordList fields = topicsList.getDataAsRecordList();
        graph.updateGraph(fields);

      }
    });

    final VStack vStack = new VStack();
    String currentUser = (loginInfo.isLoggedIn() ? "<a href='"
        + loginInfo.getLogoutUrl() + "'>" + loginInfo.getEmailAddress()
        + "</a>" : "<a href='" + loginInfo.getLoginUrl() + "'>log in</a>");
    Label userLabel = new Label(currentUser);
    vStack.addMember(userLabel);
    vStack.addMember(new Label(versionId));
    vStack.addMember(form);
    vStack.addMember(stack);
    vStack.addMember(graph);

    vStack.draw();
  }

  private void displayExpenses(ExpenseRecord[] result) {
  }

  /**
   * Call the backend server to get the expenses for the given year. Will call
   * displayExpenses when the data returns.
   * 
   * @param year
   */
  private void getAsyncExpenseForYear(int year) {
    ExpenseServiceAsync expenseService = GWT.create(ExpenseService.class);
    expenseService.getExpenses(year, new AsyncCallback<ExpenseRecord[]>() {
      public void onFailure(Throwable error) {
      }

      public void onSuccess(ExpenseRecord[] result) {
        displayExpenses(result);
      }
    });
  }

}
