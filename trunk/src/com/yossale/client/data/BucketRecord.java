package com.yossale.client.data;

import java.io.Serializable;
import java.util.List;

@SuppressWarnings("serial")
public class BucketRecord implements Serializable {

	private String name;
	private long id;
	private boolean isPublic;
	private List<String> expenseCodes;
	private List<Integer> years;
	
	
	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public BucketRecord() {		
		super();
	}
	
	public BucketRecord(long id, String name, List<String> expenseCodes, List<Integer> years) {
	  super();
	  this.name = name;
	  this.id = id;
	  this.isPublic = false;
	  this.expenseCodes = expenseCodes;
	  this.years = years;
  }

	public List<Integer> getYears() {
		return years;
	}

	public void setYears(List<Integer> years) {
		this.years = years;
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
	
	/**
	 * Returns a mutable list of the expense codes in this bucket.
	 */
	public List<String> getExpenseCodes() {
		return expenseCodes;
	}
	
	public void setId(long id) {
		this.id = id;
	}
	
}
