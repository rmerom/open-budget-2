package com.yossale.client;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectOtherItem;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
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
import com.yossale.client.graph.GraphCanvas;
import com.yossale.client.gui.dataobj.ExpenseRecordTreeNode;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OBudget2 implements EntryPoint {

  private TreeGrid budgetTree;
  private final GraphCanvas graph = new GraphCanvas();
  private final ExpenseServiceAsync expensesService = GWT
      .create(ExpenseService.class);

  private TreeGrid bucketTree;

  private Integer counter = 2012;

  private void updateTree(final int year) {
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
    expensesService.getExpenses(year, new AsyncCallback<ExpenseRecord[]>() {

      @Override
      public void onSuccess(ExpenseRecord[] result) {

        if (result == null || result.length == 0) {
          return;
        }

        TreeNode[] nodes = new TreeNode[result.length];
        for (int i = 0; i < result.length; i++) {
          nodes[i] = new ExpenseRecordTreeNode(result[i]);
        }

        System.out.println("Updating tree for [" + year + "] ");
        Tree expensesTreeModel = new Tree();
        expensesTreeModel.setModelType(TreeModelType.PARENT);
        expensesTreeModel.setNameProperty("ID");
        expensesTreeModel.setChildrenProperty("directReports");
        expensesTreeModel.setData(nodes);
        budgetTree.setData(expensesTreeModel);
      }

      @Override
      public void onFailure(Throwable caught) {
        System.out.println("Call to server for year : " + year + " failed");
      }
    });

  }

  private TreeGrid generateBudgetTree() {
    TreeGrid employeeTreeGrid = new TreeGrid();
    employeeTreeGrid.setShowOpenIcons(true);
    employeeTreeGrid.setClosedIconSuffix("");
    employeeTreeGrid.setFields(new TreeGridField("Name"), new TreeGridField(
        "Year"));
    // employeeTreeGrid.setData(generateSimpleTreeGrid(2002));
    employeeTreeGrid.setSize("400", "400");

    employeeTreeGrid.setShowEdges(true);
    employeeTreeGrid.setBorder("0px");
    employeeTreeGrid.setBodyStyleName("normal");
    employeeTreeGrid.setShowHeader(false);
    employeeTreeGrid.setLeaveScrollbarGap(false);
    employeeTreeGrid.setEmptyMessage("<br>Choose year to see budget expenses");

    employeeTreeGrid.setCanReorderRecords(true);
    employeeTreeGrid.setCanAcceptDroppedRecords(true);
    employeeTreeGrid.setCanDragRecordsOut(true);

    return employeeTreeGrid;
  }

  private TreeGrid generateBucket() {

    TreeGrid tree = new TreeGrid();    
    tree.setFields(new TreeGridField("Name"), new TreeGridField("Year"));
    // employeeTreeGrid.setData(generateSimpleTreeGrid(2002));
    tree.setSize("400", "400");

    tree.setShowOpenIcons(true);
    
    tree.setClosedIconSuffix("");
    tree.setShowEdges(true);
    tree.setBorder("0px");
    tree.setBodyStyleName("normal");
    tree.setShowHeader(false);
    tree.setLeaveScrollbarGap(false);
    tree.setEmptyMessage("<br>Drag & drop expenses here");
    tree.setCanReorderRecords(true); 
    tree.setCanAcceptDrop(true);
    tree.setCanDragRecordsOut(true);
    tree.setCanAcceptDroppedRecords(true);
    tree.setDragDataAction(DragDataAction.MOVE);
    tree.setCanRemoveRecords(true);
    
    
    final Tree bucketModel = new Tree();
    bucketModel.setModelType(TreeModelType.PARENT);
    bucketModel.setNameProperty("ID");
    bucketModel.setChildrenProperty("directReports");
//    expensesTreeModel.setData(nodes);
    tree.setData(bucketModel);
    
    bucketModel.addDataChangedHandler(new DataChangedHandler() {
		
		@Override
		public void onDataChanged(DataChangedEvent event) {
			System.out.println("dropped something?");
			TreeNode[] nodes = bucketModel.getAllNodes();			
			List<ExpenseRecord> list = new ArrayList<ExpenseRecord>();
			
			for (int i=0; i<nodes.length; i++) {
				list.add(((ExpenseRecordTreeNode)nodes[i]).getRecord());
			}			
			
			graph.updateGraph(list);			
		}
	});      
    
    return tree;
  }
  
  private HLayout generateDBZone() {

    VLayout messageLayout = new VLayout();
    messageLayout.setWidth(200);
    messageLayout.setHeight(300);
    messageLayout.setBorder("1px solid #6a6a6a");
    messageLayout.setLayoutMargin(5);

    final Canvas textCanvas = new Canvas();
    textCanvas.setPrefix("<b>Testing the DB:</b><BR>");
    textCanvas.setPadding(5);
    textCanvas.setHeight(1);

    final TextItem commitText = new TextItem();
    commitText.setTitle("Commit");
    commitText.setWidth("*");
    commitText.setDefaultValue("Commit");

    final TextItem retrieveText = new TextItem();
    retrieveText.setTitle("Retrieve");
    retrieveText.setWidth("*");
    retrieveText.setDefaultValue("Retrieve");

    final DynamicForm form = new DynamicForm();
    form.setNumCols(2);
    form.setHeight("*");
    form.setColWidths(60, "*");
    
    final TextAreaItem jsonText = new TextAreaItem();  
    jsonText.setShowTitle(false);  
    jsonText.setLength(5000);  
    jsonText.setColSpan(2);  
    jsonText.setWidth("*");  
    jsonText.setHeight("*");  

    form.setFields(commitText, retrieveText, jsonText);

    messageLayout.addMember(textCanvas);
    messageLayout.addMember(form);

    VLayout buttonLayout = new VLayout(10);

    IButton commitButton = new IButton("Commit");
    commitButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {

        final ExpenseRecord e = new ExpenseRecord("001122", 9999, "SomeName", 101,
            102, 103, 104, 105, 106);

        expensesService.addExpenseRecord(e, new AsyncCallback<Void>() {

          @Override
          public void onSuccess(Void result) {
            commitText.setValue("Success!");
            textCanvas.setContents(textCanvas.getPrefix() + e.toString());
          }

          @Override
          public void onFailure(Throwable caught) {
            commitText.setValue("Failure :(");
          }

        });

        
      }
    });

    IButton retrieveButton = new IButton("Retrieve");
    retrieveButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {        

        expensesService.getExpensesByYear(9999, new AsyncCallback<ExpenseRecord[]>() {          

          @Override
          public void onFailure(Throwable caught) {
            retrieveText.setValue("Failure :(" );
          }

          @Override
          public void onSuccess(ExpenseRecord[] result) {
            retrieveText.setValue("Success!");            
            
            textCanvas.setContents(textCanvas.getPrefix() + " Retrieved " + 
                result.length + " records");
          }

        });
        
      }
    });
    
    IButton deleteAll = new IButton("Delete All");
    deleteAll.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {       

        expensesService.removeAll(new AsyncCallback<Void>() {

          @Override
          public void onSuccess(Void result) {
            commitText.setValue("Deleted everything");            
          }

          @Override
          public void onFailure(Throwable caught) {
            commitText.setValue("Failure to delete");
          }

        });
        
      }
    });
    
    IButton commitJson = new IButton("CommitJson");
    commitJson.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        
        String content = jsonText.getValueAsString();

        JSONValue res = JSONParser.parseStrict(content);        
        JSONArray arr = res.isArray();
        for (int i=0; i<arr.size(); i++) {
          
        }
        
                
      }
    });
    
    

    buttonLayout.addMember(commitButton);
    buttonLayout.addMember(retrieveButton);
    buttonLayout.addMember(deleteAll);
    buttonLayout.addMember(commitJson);
    

    HLayout layout = new HLayout(15);
    layout.setAutoHeight();
    layout.addMember(messageLayout);
    layout.addMember(buttonLayout);
    
    return layout;

  }

  private DynamicForm generateDynamicForm() {

    DynamicForm form = new DynamicForm();
    form.setWidth(250);

    SelectOtherItem selectOtherItem = new SelectOtherItem();
    selectOtherItem.setOtherTitle("Other..");
    selectOtherItem.setOtherValue("OtherVal");

    selectOtherItem.setTitle("Select year");
    selectOtherItem.setValueMap("2001", "2002", "2003","2004","2005", "2006", "2007","2008");
    selectOtherItem.addChangedHandler(new ChangedHandler() {

      @Override
      public void onChanged(ChangedEvent event) {
        /**
         * Since we want to update on value change, we just replace the
         * TreeGrid's data with a new data (remember, the model is an Object of
         * type Tree), and it refreshes alone.
         */
        String val = (String) event.getValue();
        updateTree(Integer.parseInt(val));
      }
    });

    form.setFields(selectOtherItem);
    return form;
  }

  /**
   * This is the entry point method.
   */
  public void loadOBudget(LoginInfo loginInfo) {

    /**
     * TreeGrid is the actual UI widget. It's datamodel is actually something
     * that's called "Tree" - which is what being generated in the
     * generateSimpleTreeGrid function.
     * 
     * You select which fields you want the TreeGrid to have using the
     * "setFields" command, where each field should correspond to some Attribute
     * of the object in the Tree (which is the DataModel).
     * 
     */

    budgetTree = generateBudgetTree();
    bucketTree = generateBucket();

    final DynamicForm form = generateDynamicForm();

    final IButton button = new IButton("Click me to change tree!");
    button.setWidth(120);
    button.setHeight(60);

    button.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        updateTree(counter++);
      }
    });

    HLayout h = new HLayout();
    h.addMember(budgetTree);
    h.addMember(bucketTree);
    h.addMember(graph);

    VLayout v = new VLayout();

    String currentUser = (loginInfo.isLoggedIn() ? "<a href='"
        + loginInfo.getLogoutUrl() + "'>" + loginInfo.getEmailAddress()
        + "</a>" : "<a href='" + loginInfo.getLoginUrl() + "'>log in</a>");
    Label userLabel = new Label(currentUser);
    v.setAutoHeight();
    v.setMembersMargin(30);
    v.addMember(userLabel);
    v.addMember(generateDBZone());
    v.addMember(button);
    v.addMember(form);
    v.addMember(h);    

    v.draw();

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

}
