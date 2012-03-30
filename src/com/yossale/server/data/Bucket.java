package com.yossale.server.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.yossale.client.data.BucketRecord;
import com.yossale.client.data.ExpenseRecord;

public class Bucket {

	@Id
	private Long key;

	Key<User> owner;
	
	private String name;

	private List<String> expenses;

	private Boolean isPublic;

	public Bucket() {
		expenses = new ArrayList<String>();
	}
	
	public Bucket(BucketRecord bucketRecord, User owner) {
		assignBucketRecord(bucketRecord, owner);
	}
	
	public Bucket assignBucketRecord(BucketRecord bucketRecord, User owner) {
		name = bucketRecord.getName();
		isPublic = bucketRecord.isPublic();
		expenses = new ArrayList<String>();
		for (ExpenseRecord expenseRecord : bucketRecord.getExpenses()) {
			expenses.add(new Expense(expenseRecord).getKey());
		}
		this.owner = Key.create(User.class, owner.getEmail()); 
		
		return this;
	}

	public Bucket setOwner(String ownerEmail) {
		this.owner = Key.create(User.class, ownerEmail);
		return this;
	}
	
	public Long getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public Bucket setName(String name) {
		this.name = name;
		return this;
	}

	public List<String> getExpenses() {
		if (expenses == null) {
			return new ArrayList<String>();
		}
		return expenses;
	}

	public Bucket setExpenses(List<String> expenses) {
		this.expenses = expenses;
		return this;
	}
	
	public BucketRecord toBucketRecord() {
		Objectify otfy = new DAO().fact().begin();
		Map<String, Expense> loadedExpenses = otfy.get(Expense.class, expenses);
		List<ExpenseRecord> expenseRecords = new ArrayList<ExpenseRecord>();
		for (Expense loadedExpense : loadedExpenses.values()) {
			expenseRecords.add(loadedExpense.toExpenseRecord());
		}
		return new BucketRecord(getKey(), getName(), expenseRecords);
	}

	public Boolean getIsPublic() {
		return isPublic == null ? false : isPublic;
	}

	public Bucket setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
		return this;
	}

}
