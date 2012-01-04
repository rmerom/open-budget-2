package com.yossale.client.gui.dataobj;

import com.smartgwt.client.widgets.tree.TreeNode;
import com.yossale.client.data.ExpenseRecord;

public class ExpenseRecordTreeNode extends TreeNode {
  
  private final ExpenseRecord record;
  
  public ExpenseRecordTreeNode(ExpenseRecord expense) {
    
    this.record = expense;
    
    String id = expense.getExpenseCode(); 
    setID(id);
    setParentID(id.substring(0, id.length() - 2));    
    setAttribute("Name", id);
    setAttribute("Year", expense.getYear());    
  }

  public ExpenseRecord getRecord() {
    return record;
  }
  
  
  

}
