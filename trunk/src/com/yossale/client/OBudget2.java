package com.yossale.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.calendar.Calendar;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
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
import com.yossale.client.gui.dataobj.SectionRecordTreeNode;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OBudget2 implements EntryPoint {

  public static final String VERSION_ID = "1.1 - added mail support";

  private TreeGrid budgetTree;
  private final GraphCanvas graph = new GraphCanvas();
  private final SectionServiceAsync sectionsService = GWT
      .create(SectionService.class);
  private final BucketServiceAsync bucketService = GWT
      .create(BucketService.class);
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
      System.out.println("Cache hit on " + year);
      BudgetTreeGrid budgetTree = budgetTreesCache.get(year);
      budgetPane.setLeftPane(budgetTree);
      budgetPane.redraw();

    } else {

      System.out.println("Generating new tree");
      BudgetTreeGrid budgetTree = new BudgetTreeGrid(year);
      budgetTreesCache.put(year, budgetTree);
      budgetPane.setLeftPane(budgetTree);
    }
  }


  private TreeGrid generateBucket() {

    TreeGrid tree = new TreeGrid();
    tree.setFields(new TreeGridField("#"), new TreeGridField("Name"),
        new TreeGridField("Year"));
    // employeeTreeGrid.setData(generateSimpleTreeGrid(2002));
    tree.setSize("400", "400");
    tree.setShowOpenIcons(true);
    tree.setShowEdges(true);
    tree.setBorder("0px");
    tree.setBodyStyleName("normal");
    tree.setLeaveScrollbarGap(false);
    tree.setEmptyMessage("<br>Drag & drop sections here");
    tree.setCanReorderRecords(true);
    tree.setCanAcceptDrop(true);
    tree.setCanDragRecordsOut(true);
    tree.setCanAcceptDroppedRecords(true);
    tree.setDragDataAction(DragDataAction.MOVE);
    tree.setCanRemoveRecords(true);
    tree.setShowFilterEditor(true);
    tree.setFilterOnKeypress(true);

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
          list.add(((SectionRecordTreeNode) nodes[i]).getRecord());
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

        final SectionRecord e = new SectionRecord("001122", "0011", 9999,
            "SomeName", 101, 102, 103, 104, 105, 106);

        sectionsService.addSectionRecord(e, new AsyncCallback<Void>() {

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

        String content = jsonText.getValueAsString();
        if (null == content) {
          return;
        }

        sectionsService.getSectionsByYear(Integer.parseInt(content),
            new AsyncCallback<SectionRecord[]>() {

              @Override
              public void onFailure(Throwable caught) {
                retrieveText.setValue("Failure :(");
              }

              @Override
              public void onSuccess(SectionRecord[] result) {
                retrieveText.setValue("Success!");

                textCanvas.setContents(textCanvas.getPrefix() + " Retrieved "
                    + result.length + " records");
              }

            });

      }
    });

    IButton deleteAll = new IButton("Delete All");
    deleteAll.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {

        sectionsService.removeAll(new AsyncCallback<Void>() {

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
        if (content == null) {
          return;
        }
        System.out.println("Updating data for year " + content);

        sectionsService.loadYearData(content, new AsyncCallback<Void>() {

          @Override
          public void onSuccess(Void result) {
            commitText.setValue("Success!");
            textCanvas.setContents(textCanvas.getPrefix());
          }

          @Override
          public void onFailure(Throwable caught) {
            commitText.setValue("Failure :(");
          }

        });

      }
    });

    final IButton updateBudget = new IButton("Update budget tree");
    updateBudget.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {
        String content = jsonText.getValueAsString();
        if (content == null) {
          return;
        }
        updateTree(Integer.parseInt(content));
      }
    });

    IButton addBucket = new IButton("AddBucket");
    addBucket.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {

        String content = jsonText.getValueAsString();
        if (content == null) {
          return;
        }
        System.out.println("adding bucket " + content);

        bucketService.addBucket(content, new AsyncCallback<BucketRecord>() {

          @Override
          public void onSuccess(BucketRecord bucket) {
            commitText.setValue("Success!");
            textCanvas.setContents(textCanvas.getPrefix());
          }

          @Override
          public void onFailure(Throwable caught) {
            commitText.setValue("Failure :(");
          }
        });
      }
    });

    buttonLayout.addMember(commitButton);
    buttonLayout.addMember(retrieveButton);
    buttonLayout.addMember(deleteAll);
    buttonLayout.addMember(commitJson);
    buttonLayout.addMember(updateBudget);
    buttonLayout.addMember(addBucket);

    HLayout layout = new HLayout(15);
    layout.setAutoHeight();
    layout.addMember(messageLayout);
    layout.addMember(buttonLayout);

    return layout;

  }

  private DynamicForm generateDynamicForm() {

    DynamicForm form = new DynamicForm();
    form.setWidth(250);

    final SelectItem selectOtherItem = new SelectItem();

    sectionsService.getAvailableBudgetYears(new AsyncCallback<String[]>() {

      @Override
      public void onFailure(Throwable caught) {
        System.out.println("Failed to retrieve years!");
      }

      @Override
      public void onSuccess(String[] result) {
        selectOtherItem.setValueMap(result);

      }
    });

    selectOtherItem.setTitle("Select year");
    selectOtherItem.addChangedHandler(new ChangedHandler() {

      @Override
      public void onChanged(ChangedEvent event) {
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
    
    budgetTree = generateBudgetTree();
    bucketTree = generateBucket();
    budgetPane = new BudgetPane(budgetTree, bucketTree);   

    final DynamicForm form = generateDynamicForm();

    HLayout h = new HLayout();
    h.addMember(budgetPane);  
//    h.addMember(new BudgetTreeGrid(2010));
    
    h.addMember(graph);
    h.addMember(form);

    VLayout v = new VLayout();

    String currentUser = (loginInfo.isLoggedIn() ? "<a href='"
        + loginInfo.getLogoutUrl() + "'>" + loginInfo.getEmailAddress()
        + "</a>" : "<a href='" + loginInfo.getLoginUrl() + "'>log in</a>");
    Label userLabel = new Label(currentUser);
    v.setAutoHeight();
    v.setMembersMargin(30);

    v.addMember(new Label("Version :" + VERSION_ID));
    v.addMember(userLabel);
    v.addMember(createTitle());
    v.addMember(form);
    v.addMember(h);

    v.addMember(generateDBZone());

    v.draw();

  }

  private TreeGrid generateBudgetTree() {
    int curYear = 2010;
    System.out.println("Retrieving year: " + curYear);
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
