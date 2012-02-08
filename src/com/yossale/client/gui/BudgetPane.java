package com.yossale.client.gui;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.HLayout;

public class BudgetPane extends HLayout {
  
  public BudgetPane(Canvas left, Canvas right) {
    setBorder("5px solid blue");
    addMember(left, 0);
    addMember(right, 1);
    setShowResizeBar(true);
  }
  
  public void updateBudgetTree(Canvas left) {
    left.animateFade(25);
    Canvas member = getMember(0);
    removeMember(member);
    addMember(left,0);    
  }
  
  public void updateBucketTree(Canvas right) {
    right.animateFade(25);
    Canvas member = getMember(1);
    removeMember(member);
    addMember(right,1);
  }

}
