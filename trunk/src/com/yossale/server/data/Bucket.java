package com.yossale.server.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Embedded;
import javax.persistence.Id;

import com.googlecode.objectify.Key;

public class Bucket {

	@Id
	private Long key;

	Key<User> owner;
	
	private String name;

	@Embedded private List<Expense> expenses;
	
	private List<Integer> years;

	private Boolean isPublic;

	public static class Expense {
		private String expenseCode;

		// Between 0.0 and 1.0.
		private double weight;

		public Expense() {
		}
		
		public Expense(String expenseCode, double weight) {
			this.expenseCode = expenseCode;
			this.weight = weight;
		}

		public String getExpenseCode() {
			return expenseCode;
		}

		public void setExpense(String expenseCode) {
			this.expenseCode = expenseCode;
		}

		/**
		 * The weight in which this expenseCode appears in the enclosing bucket.
		 * For example, 0.2 means 20% of this expense should be calculated in this.
		 */
		public double getRatio() {
			return weight;
		}

		public void setRatio(double ratio) {
			this.weight = ratio;
		}
		
	}
	public Bucket() {
		expenses = new ArrayList<Expense>();
		years = new ArrayList<Integer>();
	}
	
	public Bucket setOwner(String ownerEmail) {
		this.owner = Key.create(User.class, ownerEmail);
		return this;
	}
	
	public Long getKey() {
		return key;
	}

	public void setKey(Long key) {
		this.key = key;
	}
	
	public String getName() {
		return name;
	}

	public Bucket setName(String name) {
		this.name = name;
		return this;
	}

	public List<Expense> getExpenses() {
		return expenses;
	}

	public Bucket setExpenses(List<Expense> expenses) {
		this.expenses = expenses;
		return this;
	}
	
	public Boolean getIsPublic() {
		return isPublic == null ? false : isPublic;
	}

	public Bucket setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
		return this;
	}

	/**
	 * Years which this bucket spans. All expenseCodes are relative to these years.
	 */
	public List<Integer> getYears() {
		return years;
	}

	public void setYears(List<Integer> years) {
		this.years = years;
	}
}
