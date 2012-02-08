package com.yossale.client.gui;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.FilterBuilder;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;

public class BudgetPane extends HLayout {
  
  VLayout vLayout = new VLayout();
  HLayout treesLayout = new HLayout();
  
  public BudgetPane(final BudgetTreeGrid left, Canvas right) {
    setBorder("5px solid blue");
    treesLayout.addMember(left, 0);
    treesLayout.addMember(right, 1);
    treesLayout.setShowResizeBar(true);
    
    final FilterBuilder filterBuilder = new FilterBuilder();          
    filterBuilder.setDataSource(left.getDataSource());  
    
    vLayout.addMember(filterBuilder, 0);
    
    IButton filterButton = new IButton("Filter");  
    filterButton.addClickHandler(new ClickHandler() {  
        public void onClick(ClickEvent event) {  
          left.filterData(filterBuilder.getCriteria());  
        }  
    });
    
    vLayout.addMember(filterButton, 1);
    vLayout.addMember(treesLayout, 2);
    addMember(vLayout);
  }
  
  public void updateBudgetTree(Canvas left) {
    Canvas member = treesLayout.getMember(0);
    treesLayout.removeMember(member);
    treesLayout.addMember(left,0);    
  }
  
  public void updateBucketTree(Canvas right) {
    Canvas member = treesLayout.getMember(1);
    treesLayout.removeMember(member);
    treesLayout.addMember(right,1);
  }

}
