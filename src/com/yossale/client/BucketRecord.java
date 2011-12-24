package com.yossale.client;

import java.io.Serializable;

public class BucketRecord implements Serializable {

	private static final long serialVersionUID = -6042291935300121674L;
	
	private String name;
	private ExpenseRecord[] expenseRecords;
	private String id;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public ExpenseRecord[] getExpenseRecords() {
		return expenseRecords;
	}
	public void setExpenseRecords(ExpenseRecord[] expenseRecords) {
		this.expenseRecords = expenseRecords;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
}
