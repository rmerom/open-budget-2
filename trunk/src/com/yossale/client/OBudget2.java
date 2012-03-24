package com.yossale.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.events.ChangedEvent;
import com.smartgwt.client.widgets.form.fields.events.ChangedHandler;
import com.smartgwt.client.widgets.grid.ListGridRecord;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.DataChangedEvent;
import com.smartgwt.client.widgets.tree.DataChangedHandler;
import com.smartgwt.client.widgets.tree.Tree;
import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.smartgwt.client.widgets.tree.TreeNode;
import com.yossale.client.actions.BucketService;
import com.yossale.client.actions.BucketServiceAsync;
import com.yossale.client.actions.LoginService;
import com.yossale.client.actions.LoginServiceAsync;
import com.yossale.client.actions.SectionService;
import com.yossale.client.actions.SectionServiceAsync;
import com.yossale.client.data.BucketRecord;
import com.yossale.client.data.LoginInfo;
import com.yossale.client.data.SectionRecord;
import com.yossale.client.graph.GraphCanvas;
import com.yossale.client.gui.BudgetPane;
import com.yossale.client.gui.BudgetTreeGrid;
import com.yossale.client.gui.DBPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OBudget2 implements EntryPoint {

  public static final String VERSION_ID = "0.4 - initial buckets";

  private BudgetTreeGrid budgetTree;
  private final GraphCanvas graph = new GraphCanvas();
  private final SectionServiceAsync sectionsService = GWT
      .create(SectionService.class);
  private final Map<Integer, BudgetTreeGrid> budgetTreesCache = new HashMap<Integer, BudgetTreeGrid>();

  private BudgetPane budgetPane;
  private TreeGrid bucketTree;

  private void updateTree(final int year) {
    /**
     * In the data model you need to have something which extends "TreeNode",
     * Which is very trivial..
     * 
     * Since we want to give it all the records and let him figure the
     * Hierarchy, we need to tell him 2 thing : id and parentId. This is defined
     * in the object itself (here it's at the SectionRecord)
     * 
     * So after you have a list of items, each knows who is father is and what
     * is it's ID, you just provide them to the model as an array, and it'll
     * take care of the rest.
     */

    if (budgetTreesCache.containsKey(year)) {
      Log.info("Cache hit on " + year);
      BudgetTreeGrid budgetTree = budgetTreesCache.get(year);
      budgetPane.updateBudgetTree(budgetTree);

    } else {

      Log.info("Generating new tree for " + year);
      BudgetTreeGrid budgetTree = new BudgetTreeGrid(year);
      budgetTreesCache.put(year, budgetTree);
      budgetPane.updateBudgetTree(budgetTree);
    }
  }

  private TreeGrid generateBucket() {

    Log.info("Generating new bucket");
    TreeGrid tree = new TreeGrid();
    tree.setFields(new TreeGridField("sectionCode", "Code"), new TreeGridField(
        "name", "Name"), new TreeGridField("year", "Year"));
    tree.setSize("400", "400");
    tree.setShowOpenIcons(true);
    tree.setShowEdges(true);
    tree.setBorder("1px solid black");
    tree.setBodyStyleName("normal");
    tree.setLeaveScrollbarGap(false);
    tree.setEmptyMessage("<br>Drag & drop sections here");
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
    // sectionsTreeModel.setData(nodes);
    tree.setData(bucketModel);

    bucketModel.addDataChangedHandler(new DataChangedHandler() {

      @Override
      public void onDataChanged(DataChangedEvent event) {
        System.out.println("dropped something?");
        TreeNode[] nodes = bucketModel.getAllNodes();

        List<SectionRecord> list = new ArrayList<SectionRecord>();

        for (int i = 0; i < nodes.length; i++) {
          list.add(SectionRecord.getSectionRecord(nodes[i]));
        }

        graph.updateGraph(list);
      }
    });

    return tree;
  }

  private DynamicForm generateDynamicForm() {

    DynamicForm form = new DynamicForm();
    form.setWidth(250);

    final SelectItem yearSelector = new SelectItem();

    sectionsService.getAvailableBudgetYears(new AsyncCallback<String[]>() {

      @Override
      public void onFailure(Throwable caught) {
        System.out.println("Failed to retrieve years!");
      }

      @Override
      public void onSuccess(String[] result) {
        yearSelector.setValueMap(result);
        // yearSelector.setValue(result[result.length - 1]);
      }
    });

    yearSelector.setTitle("Select year");
    yearSelector.addChangedHandler(new ChangedHandler() {

      @Override
      public void onChanged(ChangedEvent event) {
        String val = (String) event.getValue();
        updateTree(Integer.parseInt(val));
      }
    });

    form.setFields(yearSelector);
    return form;
  }

  /**
   * This is the entry point method.
   */
  public void loadOBudget(LoginInfo loginInfo) {
  	final BucketServiceAsync bucketService = GWT.create(BucketService.class); 
    Log.debug("Tommy can you see me?");
    Log.info("OBudget loading started");
    budgetTree = generateBudgetTree();
    bucketTree = generateBucket();
    final TreeGrid bucketTreeFinal = bucketTree;
    budgetPane = new BudgetPane(budgetTree, bucketTree);

    final DynamicForm form = generateDynamicForm();

    HLayout h = new HLayout();
    h.addMember(budgetPane);
    h.addMember(graph);
    h.addMember(form);

    VLayout v = new VLayout();

    String currentUser;
    if (loginInfo != null) {
    	currentUser = (loginInfo.isLoggedIn() ? "<a href='"
    			 + loginInfo.getLogoutUrl() + "'>" + loginInfo.getEmailAddress()
           + "</a>" : "<a href='" + loginInfo.getLoginUrl() + "'>log in</a>");
    } else {
    	currentUser = "login currently not working";
    }
        
    Label userLabel = new Label(currentUser);
    v.setAutoHeight();
    v.setMembersMargin(30);

    v.addMember(new Label("Version :" + VERSION_ID));
    v.addMember(userLabel);
    final ListBox listBox = new ListBox();
    
    v.addMember(listBox);

    bucketService.getBucketsOfLoggedInUser(new AsyncCallback<BucketRecord[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.warn("unable to get list of buckets");
			}

			@Override
			public void onSuccess(BucketRecord[] result) {
				Log.warn("Got " + result.length + " buckets");
				for (BucketRecord bucketRecord : result) {
					listBox.addItem(bucketRecord.getName());
				}
			}
    	
    });
    HLayout horizontalSavePanel = new HLayout();
    final TextBox textBox = new TextBox();
    horizontalSavePanel.addMember(textBox);

    horizontalSavePanel.addMember(new Button("save doNotPress", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				bucketService.addBucket(textBox.getText(), new AsyncCallback<BucketRecord>() {
					
					@Override
					public void onSuccess(BucketRecord result) {
						ListGridRecord[] records = bucketTreeFinal.getRecords();
						for (ListGridRecord record : records) {
							result.getSections().add(SectionRecord.getSectionRecord(record));
						}
						bucketService.updateBucket(result, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Log.warn("failure to update bucket: " + caught);
							}

							@Override
							public void onSuccess(Void result) {
								// TODO Auto-generated method stub
							}
						});
						
					}
					
					@Override
					public void onFailure(Throwable caught) {
						Log.warn("failure to add bucket: " + caught);
					}
				});
			}
    	
    }));
    
    v.addMember(horizontalSavePanel);
    v.addMember(createTitle());
    v.addMember(form);
    v.addMember(h);

    v.addMember(new DBPanel());
    v.draw();

  }

  private BudgetTreeGrid generateBudgetTree() {
    int curYear = 2010;
    Log.info("Generating budget tree - retrieving year: " + curYear);
    return new BudgetTreeGrid(curYear);
  }

  private Canvas createTitle() {
    Label l = new Label();
    l.setAlign(Alignment.CENTER);
    l.setTitle("חוקר התקציב");
    return l;
  }

  @Override
  public void onModuleLoad() {
//    Log.setUncaughtExceptionHandler();
    
    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				Log.fatal("there will be a merge issue here", e);
			}
		});
    
    // loadOBudget(null);
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
