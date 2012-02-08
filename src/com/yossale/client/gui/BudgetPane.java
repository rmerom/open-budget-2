package com.yossale.client.gui;

import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.layout.SplitPane;

public class BudgetPane extends Canvas {
  
  private final SplitPane sp = new SplitPane();
  
  public BudgetPane(Canvas left, Canvas right) {
    super();
    setBorder("5px solid blue");
    sp.setBorder("3px solid red");
    
    addChild(sp);
    sp.setSize("750","400");
    setLeftPane(left);
    setRightPane(right);
    setSize("800","400");
  }
  
  public void setLeftPane(Canvas left) {
    sp.setNavigationPane(left);
  }
  
  public void setRightPane(Canvas right) {
    sp.setDetailPane(right);
  }

}
