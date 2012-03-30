package com.yossale.client;

import java.util.ArrayList;
import java.util.List;

import com.allen_sauer.gwt.log.client.Log;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.TreeModelType;
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
import com.yossale.client.data.BucketRecord;
import com.yossale.client.data.ExpenseRecord;
import com.yossale.client.data.LoginInfo;
import com.yossale.client.graph.GraphCanvas;
import com.yossale.client.gui.BucketPane;
import com.yossale.client.gui.DBPanel;
import com.yossale.client.gui.InputPane;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OBudget2 implements EntryPoint {

  public static final String VERSION_ID = "0.4 - initial buckets";

  private final GraphCanvas graph = new GraphCanvas();

  private TreeGrid bucketTree;


  private TreeGrid generateBucket() {

    Log.info("Generating new bucket");
    TreeGrid tree = new TreeGrid();
    tree.setFields(new TreeGridField("expenseCode", "Code"), new TreeGridField(
        "name", "Name"), new TreeGridField("year", "Year"));
    tree.setSize("400", "400");
    tree.setShowOpenIcons(true);
    tree.setShowEdges(true);
    tree.setBorder("1px solid black");
    tree.setBodyStyleName("normal");
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
    tree.setData(bucketModel);

    bucketModel.addDataChangedHandler(new DataChangedHandler() {

      @Override
      public void onDataChanged(DataChangedEvent event) {
        System.out.println("dropped something?");
        TreeNode[] nodes = bucketModel.getAllNodes();

        List<ExpenseRecord> list = new ArrayList<ExpenseRecord>();

        for (int i = 0; i < nodes.length; i++) {
          list.add(ExpenseRecord.getExpenseRecord(nodes[i]));
        }

        graph.updateGraph(list);
      }
    });

    return tree;
  }

  /**
   * This is the entry point method.
   */
  public void loadOBudget(LoginInfo loginInfo) {
  	final BucketServiceAsync bucketService = GWT.create(BucketService.class); 
    Log.info("OBudget loading started");
    bucketTree = generateBucket();
    final TreeGrid bucketTreeFinal = bucketTree;

    String currentUser;
    if (loginInfo != null) {
    	currentUser = (loginInfo.isLoggedIn() ? "<a href='"
    			 + loginInfo.getLogoutUrl() + "'>" + loginInfo.getEmailAddress()
           + "</a>" : "<a href='" + loginInfo.getLoginUrl() + "'>log in</a>");
    } else {
    	currentUser = "login currently not working";
    }
        
    HTML userLabel = new HTML(currentUser);

    HLayout bucketLayout = new HLayout();
    Label bucketLabel = new Label();
    bucketLabel.setText("Load bucket:");
    bucketLayout.addMember(bucketLabel);
    final ListBox bucketListBox = new ListBox();

//    bucketListBox.
    bucketLayout.addMember(bucketListBox);

    bucketService.getBucketsOfLoggedInUser(new AsyncCallback<BucketRecord[]>() {
			@Override
			public void onFailure(Throwable caught) {
				Log.warn("unable to get list of buckets");
			}

			@Override
			public void onSuccess(BucketRecord[] result) {
				Log.warn("Got " + result.length + " buckets");
				for (BucketRecord bucketRecord : result) {
					bucketListBox.addItem(bucketRecord.getName());
				}
			}
    	
    });
    HLayout horizontalSavePanel = new HLayout();
    final TextBox textBox = new TextBox();
    horizontalSavePanel.addMember(textBox);

    horizontalSavePanel.addMember(new Button("save bucket", new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				bucketService.addBucket(textBox.getText(), new AsyncCallback<BucketRecord>() {
					
					@Override
					public void onSuccess(BucketRecord result) {
						ListGridRecord[] records = bucketTreeFinal.getRecords();
						for (ListGridRecord record : records) {
							result.getExpenses().add(ExpenseRecord.getExpenseRecord(record));
						}
						bucketService.updateBucket(result, new AsyncCallback<Void>() {
							@Override
							public void onFailure(Throwable caught) {
								Window.alert("Error saving the bucket.");
								Log.warn("failure to update bucket: " + caught);
							}

							@Override
							public void onSuccess(Void result) {
								Window.alert("Saved successfully");
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
    
    VLayout v = new VLayout();
    v.setAutoHeight();
    v.setMembersMargin(30);
    v.addMember(new Label("Version :" + VERSION_ID));
    v.addMember(userLabel);
    
    BucketPane bucketPane = new BucketPane(graph);    
    
    HLayout h = new HLayout();
    h.setMembersMargin(30);
    h.addMember(new InputPane(bucketPane));
    h.addMember(bucketPane);
    
    v.addMember(h);
    v.addMember(graph);

    v.addMember(new DBPanel());
    v.draw();
  }

  @Override
  public void onModuleLoad() {
    
    GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void onUncaughtException(Throwable e) {
				Log.fatal("there will be a merge issue here", e);
			}
		});
    
    // loadOBudget(null);
    LoginServiceAsync loginService = GWT.create(LoginService.class);
    loginService.login(GWT.getHostPageBaseURL(), new AsyncCallback<LoginInfo>() {
      public void onFailure(Throwable error) {
      }

      public void onSuccess(LoginInfo result) {
        loadOBudget(result);
      }
    });
  }

}
