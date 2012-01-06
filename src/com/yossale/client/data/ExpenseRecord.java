package com.yossale.client.data;

import java.io.Serializable;

import com.google.gwt.json.client.JSONObject;

public class ExpenseRecord implements Serializable {

	private static final long serialVersionUID = -2358987888595684650L;
	
	// Mispar Se'if Takzivi as string.
	private String expenseCode;
	private int year;
	private String name;
	private int netAmountAllocated;
	private int netAmountRevised;
	private int netAmountUsed;
	private int grosAmountAllocated;
	private int grossAmountRevised;
	private int grossAmountUsed;
	
	public ExpenseRecord() {
	  
	  
	}

	public ExpenseRecord(String expenseCode, int year, String name,
			int netAmountAllocated, int netAmountRevised, int netAmountUsed,
			int grosAmountAllocated, int grossAmountRevised, int grossAmountUsed) {
		super();
		this.expenseCode = expenseCode;
		this.year = year;
		this.name = name;
		this.netAmountAllocated = netAmountAllocated;
		this.netAmountRevised = netAmountRevised;
		this.netAmountUsed = netAmountUsed;
		this.grosAmountAllocated = grosAmountAllocated;
		this.grossAmountRevised = grossAmountRevised;
		this.grossAmountUsed = grossAmountUsed;
	}
	
	public ExpenseRecord(JSONObject j) {
	  
	  this.expenseCode = j.get("code").toString();
    this.year = Integer.parseInt(j.get("year").toString());
    this.name = j.get("title").toString();
    this.netAmountAllocated = Integer.parseInt(j.get("net_allocated").toString());;
    this.netAmountRevised = Integer.parseInt(j.get("net_revised").toString());;
    this.netAmountUsed = Integer.parseInt(j.get("net_used").toString());;
    this.grosAmountAllocated = Integer.parseInt(j.get("gross_allocated").toString());;
    this.grossAmountRevised = Integer.parseInt(j.get("gross_revised").toString());;
    this.grossAmountUsed = Integer.parseInt(j.get("gross_used").toString());;
	  
	}

	public String getExpenseCode() {
		return expenseCode;
	}

	public void setExpenseCode(String expenseCode) {
		this.expenseCode = expenseCode;
	}

	public int getYear() {
		return year;
	}

	public void setYear(int year) {
		this.year = year;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNetAmountAllocated() {
		return netAmountAllocated;
	}

	public void setNetAmountAllocated(int netAmountAllocated) {
		this.netAmountAllocated = netAmountAllocated;
	}

	public int getNetAmountRevised() {
		return netAmountRevised;
	}

	public void setNetAmountRevised(int netAmountRevised) {
		this.netAmountRevised = netAmountRevised;
	}

	public int getNetAmountUsed() {
		return netAmountUsed;
	}

	public void setNetAmountUsed(int netAmountUsed) {
		this.netAmountUsed = netAmountUsed;
	}

	public int getGrossAmountAllocated() {
		return grosAmountAllocated;
	}

	public void setGrosAmountAllocated(int grosAmountAllocated) {
		this.grosAmountAllocated = grosAmountAllocated;
	}

	public int getGrossAmountRevised() {
		return grossAmountRevised;
	}

	public void setGrossAmountRevised(int grossAmountRevised) {
		this.grossAmountRevised = grossAmountRevised;
	}

	public int getGrossAmountUsed() {
		return grossAmountUsed;
	}

	public void setGrossAmountUsed(int grossAmountUsed) {
		this.grossAmountUsed = grossAmountUsed;
	}

	@Override
	public String toString() {
		return "ExpenseRecord [expenseCode=" + expenseCode
				+ ", grosAmountAllocated=" + grosAmountAllocated
				+ ", grossAmountRevised=" + grossAmountRevised
				+ ", grossAmountUsed=" + grossAmountUsed + ", name=" + name
				+ ", netAmountAllocated=" + netAmountAllocated
				+ ", netAmountRevised=" + netAmountRevised + ", netAmountUsed="
				+ netAmountUsed + ", year=" + year + "]";
	}
	
	public ExpenseRecord add(ExpenseRecord other) {		
		
		this.netAmountAllocated += other.netAmountAllocated;
		this.netAmountRevised += other.netAmountRevised;
		this.netAmountUsed += other.netAmountUsed;
		this.grosAmountAllocated += other.grosAmountAllocated;
		this.grossAmountRevised += other.grossAmountRevised;
		this.grossAmountUsed += other.grossAmountUsed;
		
		return this;		
	}
}
