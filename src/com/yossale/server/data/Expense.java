package com.yossale.server.data;

import javax.persistence.Id;

import com.yossale.client.data.ExpenseRecord;

public class Expense {

	@Id
  private String key;

  // Mispar Se'if Takzivi as string.
  private String expenseCode;
  
  private String parentCode;

  private Integer year;

  private String name;

  private Integer netAmountAllocated;

  private Integer netAmountRevised;

  private Integer netAmountUsed;

  private Integer grossAmountAllocated;

  private Integer grossAmountRevised;

  private Integer grossAmountUsed;

  public Expense(String expenseCode, String parentCode, Integer year, String name,
      Integer netAmountAllocated, Integer netAmountRevised, Integer netAmountUsed,
      Integer grosAmountAllocated, Integer grossAmountRevised, Integer grossAmountUsed) {
    this.expenseCode = expenseCode;  
    this.parentCode = parentCode;
    this.year = year;
    this.name = name;
    this.netAmountAllocated = netAmountAllocated;
    this.netAmountRevised = netAmountRevised;
    this.netAmountUsed = netAmountUsed;
    this.grossAmountAllocated = grosAmountAllocated;
    this.grossAmountRevised = grossAmountRevised;
    this.grossAmountUsed = grossAmountUsed;
    generateKey();
  }

	public Expense() {
  }

  public String getKey() {
    return key;
  }

  public Expense setKey(String key) {
    this.key = key;
    return this;
  }

  public Expense(ExpenseRecord r) {
    this.expenseCode = r.getExpenseCode();
    this.parentCode = r.getParentCode();
    this.year = r.getYear();
    this.name = r.getName();
    this.netAmountAllocated = r.getNetAmountAllocated();
    this.netAmountRevised = r.getNetAmountRevised();
    this.netAmountUsed = r.getNetAmountUsed();
    this.grossAmountAllocated = r.getGrossAmountAllocated();
    this.grossAmountRevised = r.getGrossAmountRevised();
    this.grossAmountUsed = r.getGrossAmountUsed();
    		
    generateKey();
  }

  public String getParentCode() {
    return parentCode;
  }

  public Expense setParentCode(String parentCode) {
    this.parentCode = parentCode;
    return this;
  }

  public String getExpenseCode() {
    return expenseCode;
  }

  public Expense setExpenseCode(String expenseCode) {
    this.expenseCode = expenseCode;
    return this;
  }

  public Integer getYear() {
    return year;
  }

  public Expense setYear(Integer year) {
    this.year = year;
    return this;
  }

  public String getName() {
    return name;
  }

  public Expense setName(String name) {
    this.name = name;
    return this;
  }

  public Integer getNetAmountAllocated() {
    return netAmountAllocated;
  }

  public Expense setNetAmountAllocated(Integer netAmountAllocated) {
    this.netAmountAllocated = netAmountAllocated;
    return this;
  }

  public Integer getNetAmountRevised() {
    return netAmountRevised;
  }

  public Expense setNetAmountRevised(Integer netAmountRevised) {
    this.netAmountRevised = netAmountRevised;
    return this;
  }

  public Integer getNetAmountUsed() {
    return netAmountUsed;
  }

  public Expense setNetAmountUsed(Integer netAmountUsed) {
    this.netAmountUsed = netAmountUsed;
    return this;
  }

  public Integer getGrossAmountAllocated() {
    return grossAmountAllocated;
  }

  public Expense setGrossAmountAllocated(Integer grossAmountAllocated) {
    this.grossAmountAllocated = grossAmountAllocated;
    return this;
  }

  public Integer getGrossAmountRevised() {
    return grossAmountRevised;
  }

  public Expense setGrossAmountRevised(Integer grossAmountRevised) {
    this.grossAmountRevised = grossAmountRevised;
    return this;
  }

  public Integer getGrossAmountUsed() {
    return grossAmountUsed;
  }

  public Expense setGrossAmountUsed(Integer grossAmountUsed) {
    this.grossAmountUsed = grossAmountUsed;
    return this;
  }

  public ExpenseRecord toExpenseRecord() {
    return new ExpenseRecord(expenseCode, parentCode, year, name, netAmountAllocated,
        netAmountRevised, netAmountUsed, grossAmountAllocated,
        grossAmountRevised, grossAmountUsed);
  }
  
	// TODO(ronme): integrate with ExpenseRecord.generateKey().
  private void generateKey() {
  	key =	new StringBuilder().append(year).append("_").append(expenseCode).toString();
  }

  @Override
  public String toString() {
    return "Expense [key=" + key + ", expenseCode=" + expenseCode
        + ", parentCode=" + parentCode + ", year=" + year + ", name=" + name
        + "]";
  }
}
