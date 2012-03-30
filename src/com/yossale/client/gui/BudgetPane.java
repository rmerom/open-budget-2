package com.yossale.client.gui;

import java.util.logging.Logger;

import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.util.SC;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.FormItem;
import com.smartgwt.client.widgets.form.fields.PickerIcon;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.fields.events.FormItemClickHandler;
import com.smartgwt.client.widgets.form.fields.events.FormItemIconClickEvent;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.smartgwt.client.widgets.tree.TreeGrid;

public class BudgetPane extends HLayout {

  private static final Logger logger = Logger.getLogger(BudgetPane.class.getName());
  
  VLayout vLayout = new VLayout();
  DynamicForm filtersPane = new DynamicForm();
  HLayout treesLayout = new HLayout();
  BudgetTreeGrid budgetTree;
  TreeGrid bucketTree;

  public BudgetPane(final BudgetTreeGrid budgetTreeGrid,
      final TreeGrid bucketTreeGrid) {
    logger.info("Loading budget pane");
    setBorder("5px solid blue");
    budgetTree = budgetTreeGrid;
    bucketTree = bucketTreeGrid;

    treesLayout.addMember(budgetTree, 0);
    treesLayout.addMember(bucketTree, 1);
    treesLayout.setShowResizeBar(true);

    buildFilterPane();

    vLayout.addMember(filtersPane, 0);

    // vLayout.addMember(filterButton, 1);
    vLayout.addMember(treesLayout, 2);
    addMember(vLayout);
  }

  private void buildFilterPane() {

    filtersPane.setWidth(300);

    PickerIcon clearPicker = new PickerIcon(PickerIcon.CLEAR,
        new FormItemClickHandler() {
          public void onFormItemClick(FormItemIconClickEvent event) {
            event.getItem().clearValue();
            //Remove previous filters
            budgetTree.filterData();
          }
        });

    PickerIcon searchPicker = new PickerIcon(PickerIcon.SEARCH,
        new FormItemClickHandler() {
          public void onFormItemClick(FormItemIconClickEvent event) {
            String itemName = event.getItem().getName();
            if ("expenseName".equalsIgnoreCase(itemName)) {
              SC.say("Search by String is not supported yet, Sorry :(");
              return;
            }
            String itemValue = (String) event.getItem().getValue();
            Criteria c = new Criteria(itemName, itemValue);
            budgetTree.filterData(c);
          }
        });

    TextItem codeItem = new TextItem("expenseCode", "מספר סעיף");
    codeItem.setRequired(false);
    codeItem.setDefaultValue("00");
    codeItem.setIcons(searchPicker, clearPicker);

    TextItem nameItem = new TextItem("expenseName", "טקסט");
    nameItem.setRequired(false);
    nameItem.setDefaultValue("הכנסות");
    nameItem.setIcons(searchPicker, clearPicker);

    filtersPane.setFields(new FormItem[] { codeItem, nameItem });

  }

  public void updateBudgetTree(BudgetTreeGrid tree) {
    budgetTree = tree;
    Canvas member = treesLayout.getMember(0);
    treesLayout.removeMember(member);
    treesLayout.addMember(tree, 0);
  }

  public void updateBucketTree(TreeGrid bucket) {
    bucketTree = bucket;
    Canvas member = treesLayout.getMember(1);
    treesLayout.removeMember(member);
    treesLayout.addMember(bucket, 1);
  }

}
