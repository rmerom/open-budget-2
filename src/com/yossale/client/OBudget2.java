package com.yossale.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.Alignment;
import com.smartgwt.client.types.DragDataAction;
import com.smartgwt.client.types.TreeModelType;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.Label;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
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
import com.yossale.client.actions.SectionService;
import com.yossale.client.actions.SectionServiceAsync;
import com.yossale.client.data.SectionRecord;
import com.yossale.client.graph.GraphCanvas;
import com.yossale.client.gui.BudgetPane;
import com.yossale.client.gui.BudgetTreeGrid;
import com.yossale.client.gui.DBPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class OBudget2 implements EntryPoint {

  public static final String VERSION_ID = "0.2 - search by ID";
  private static final Logger logger = Logger.getLogger(OBudget2.class.getName());

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
      logger.info("Cache hit on " + year);
      BudgetTreeGrid budgetTree = budgetTreesCache.get(year);
      budgetPane.updateBudgetTree(budgetTree);

    } else {

      logger.info("Generating new tree");
      BudgetTreeGrid budgetTree = new BudgetTreeGrid(year);
      budgetTreesCache.put(year, budgetTree);
      budgetPane.updateBudgetTree(budgetTree);
    }
  }

  private TreeGrid generateBucket() {

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
  public void loadOBudget() {

    logger.info("OBudget loading started");
    budgetTree = generateBudgetTree();
    bucketTree = generateBucket();
    budgetPane = new BudgetPane(budgetTree, bucketTree);

    final DynamicForm form = generateDynamicForm();

    HLayout h = new HLayout();
    h.addMember(budgetPane);
    h.addMember(graph);
    h.addMember(form);

    VLayout v = new VLayout();

//    String currentUser = (loginInfo.isLoggedIn() ? "<a href='"
//        + loginInfo.getLogoutUrl() + "'>" + loginInfo.getEmailAddress()
//        + "</a>" : "<a href='" + loginInfo.getLoginUrl() + "'>log in</a>");
//    Label userLabel = new Label(currentUser);
    v.setAutoHeight();
    v.setMembersMargin(30);

    v.addMember(new Label("Version :" + VERSION_ID));
//    v.addMember(userLabel);
    v.addMember(createTitle());
    v.addMember(form);
    v.addMember(h);

    v.addMember(new DBPanel());
    v.draw();

  }

  private BudgetTreeGrid generateBudgetTree() {
    logger.info("Generating budget tree");
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
    loadOBudget();
  }

}
