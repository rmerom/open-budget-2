package com.yossale.client.gui;

import java.util.logging.Logger;

import com.smartgwt.client.widgets.tree.TreeGrid;
import com.smartgwt.client.widgets.tree.TreeGridField;
import com.yossale.client.datastore.BudgetDataSource;

public class BudgetTreeGrid extends TreeGrid {

  private static final Logger logger = Logger.getLogger(BudgetTreeGrid.class.getName());
  
  public BudgetTreeGrid(int year) {
    
    logger.info("Creating new budgetTree for year:" + year);
    BudgetDataSource ds = new BudgetDataSource(year);
    setDataSource(ds);
    setAutoFetchData(true);
    setShowOpenIcons(true);
    setFields(new TreeGridField("sectionCode"), new TreeGridField("name"),
        new TreeGridField("year"));
    
    setSize("400", "400");
    
    setShowEdges(true);
    setBorder("1px solid black");
    setBodyStyleName("normal");
    setLeaveScrollbarGap(false);    

    setCanReorderRecords(true);
    setCanAcceptDroppedRecords(true);
    setCanDragRecordsOut(true);
  }

}
