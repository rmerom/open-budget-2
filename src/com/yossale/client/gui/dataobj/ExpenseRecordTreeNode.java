package com.yossale.client.gui.dataobj;

import com.smartgwt.client.widgets.tree.TreeNode;
import com.yossale.client.data.ExpenseRecord;

public class ExpenseRecordTreeNode extends TreeNode {
  
  private final ExpenseRecord record;
  
  public ExpenseRecordTreeNode(ExpenseRecord expense) {
    
    this.record = expense;
    
    String id = expense.getYear() + "_" + expense.getExpenseCode() ; 
    setID(id);
    setParentID(id.substring(0, id.length() - 2));    
    setAttribute("ID", id);
    setAttribute("Name", expense.getExpenseCode());
    setAttribute("Year", expense.getYear());    
  }

  public ExpenseRecord getRecord() {
    return record;
  }
  
  
  

}
