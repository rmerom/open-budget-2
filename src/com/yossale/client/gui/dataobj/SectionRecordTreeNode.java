package com.yossale.client.gui.dataobj;

import com.smartgwt.client.widgets.tree.TreeNode;
import com.yossale.client.data.SectionRecord;

public class SectionRecordTreeNode extends TreeNode {
  
  private final SectionRecord record;
  
  public SectionRecordTreeNode(SectionRecord section) {
    
    this.record = section;
    
    String id = section.getYear() + "_" + section.getSectionCode() ; 
    setID(id);
    setParentID(id.substring(0, id.length() - 2));    
    setAttribute("ID", id);
    setAttribute("#", section.getSectionCode());
    setAttribute("Name", section.getName());    
    setAttribute("Year", section.getYear());    
  }

  public SectionRecord getRecord() {
    return record;
  }
  
  
  

}
