package com.yossale.client.data;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class BucketRecord implements Serializable {

	private String name;
	private long id;
	private boolean isPublic;
	private List<ExpenseRecord> expenses;
	
	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public BucketRecord() {		
		super();
	}
	
	public BucketRecord(long id, String name, List<ExpenseRecord> expenses) {
	  super();
	  this.name = name;
	  this.id = id;
	  this.isPublic = false;
	  this.expenses = expenses;
  }

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getId() {
		return id;
	}
	
	public List<ExpenseRecord> getExpenses() {
		return expenses;
	}
	
	public void setId(long id) {
		this.id = id;
	}
}
