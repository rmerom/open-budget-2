package com.yossale.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.RecordList;
import com.smartgwt.client.types.DisplayNodeType;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.GroupStartOpen;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HStack;
import com.smartgwt.client.widgets.layout.VStack;
import com.smartgwt.client.widgets.tree.DataChangedEvent;
import com.smartgwt.client.widgets.tree.DataChangedHandler;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.yossale.client.datastore.LocalDataStore;
import com.yossale.client.datastore.OneYearBudgetDataSource;
import com.yossale.client.graph.GraphCanvas;
import com.yossale.shared.RpcExpenseObject;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OBudget2 implements EntryPoint {

  protected String returnedJson;
  private final GraphCanvas graph = new GraphCanvas();

  /***/

  public TreeGrid generateGridList() {

    TreeGridField codeField = new TreeGridField("code", "Code");
    codeField.setIncludeInRecordSummary(false);

    TreeGridField itemDescriptionField = new TreeGridField("title", "Title");

    TreeGridField grossAllocated = new TreeGridField("gross_allocated",
        "הקצאה ברוטו");

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

  private TreeGrid generateOneYearBudgetTree() {

    TreeGrid budgetTree = new TreeGrid();
    budgetTree.setHeight(300);
    budgetTree.setWidth(500);
    budgetTree.setDisplayNodeType(DisplayNodeType.NULL);
    budgetTree.setLoadDataOnDemand(false);
    OneYearBudgetDataSource instance = OneYearBudgetDataSource.getInstance();
    TreeGridField titleField = new TreeGridField("title", "שם סעיף");
    titleField.setFrozen(true);

    TreeGridField codeField = new TreeGridField("code", "מספר");

    /**
     * We're basically working on a tableTree (TreeGrid = TableTree) , so we can
     * add more fields to it , that may contain the year/value and so on I'm
     * trying to add these fields
     */
    budgetTree.setFields(titleField, codeField);

    List<Record> list = new ArrayList<Record>();
    Record rec1 = new Record( new RpcExpenseObject("0001", 1992, "name1", 100, 110, 120, 130, 140, 150).convertToPropertiesMap());
    Record rec2 = new Record( new RpcExpenseObject("0002", 1992, "name2", 100, 110, 120, 130, 140, 150).convertToPropertiesMap());
    
    ListGridRecord[] recsArr = new ListGridRecord[]{new ListGridRecord(rec1.getJsObj()), new ListGridRecord(rec2.getJsObj())};
    budgetTree.setData(recsArr);
    budgetTree.setAutoFetchData(false);
    budgetTree.setCanDragRecordsOut(true);
    budgetTree.setDragDataAction(DragDataAction.COPY);

    return budgetTree;

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

  }

  private void loadOBudget(LoginInfo loginInfo) {

    TreeGrid budgetTree = generateOneYearBudgetTree();
    final TreeGrid topicsList = generateGridList();

    HStack stack = new HStack();
    stack.addMember(budgetTree);
    stack.addMember(topicsList);

    topicsList.getData().addDataChangedHandler(new DataChangedHandler() {

      @Override
      public void onDataChanged(DataChangedEvent event) {
        RecordList fields = topicsList.getDataAsRecordList();
        graph.updateGraph(fields);
      }
    });

    final VStack vStack = new VStack();
    String currentUser = (loginInfo.isLoggedIn() ? loginInfo.getEmailAddress()
        : "<a href='" + loginInfo.getLoginUrl() + "'>log in</a>");
    Label userLabel = new Label(currentUser);
    vStack.addMember(userLabel);
    vStack.addMember(new Label("V0.4"));
    vStack.addMember(stack);
    vStack.addMember(graph);

    vStack.draw();
  }

}
