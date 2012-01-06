package com.yossale.server.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.yossale.client.data.ExpenseRecord;

@PersistenceCapable
public class Expense {

  // Mispar Se'if Takzivi as string.
  @Persistent
  private String expenseCode;

  @Persistent
  private Integer year;

  @Persistent
  private String name;

  @Persistent
  private Integer netAmountAllocated;

  @Persistent
  private Integer netAmountRevised;

  @Persistent
  private Integer netAmountUsed;

  @Persistent
  private Integer grossAmountAllocated;

  @Persistent
  private Integer grossAmountRevised;

  @Persistent
  private Integer grossAmountUsed;

  public Expense(Key key, String expenseCode, int year, String name,
      int netAmountAllocated, int netAmountRevised, int netAmountUsed,
      int grosAmountAllocated, int grossAmountRevised, int grossAmountUsed) {
    this.key = key;
    this.expenseCode = expenseCode;
    this.year = year;
    this.name = name;
    this.netAmountAllocated = netAmountAllocated;
    this.netAmountRevised = netAmountRevised;
    this.netAmountUsed = netAmountUsed;
    this.grossAmountAllocated = grosAmountAllocated;
    this.grossAmountRevised = grossAmountRevised;
    this.grossAmountUsed = grossAmountUsed;
  }

  public Expense() {
  }

  public Expense(ExpenseRecord r) {

    this.expenseCode = r.getExpenseCode();
    this.year = r.getYear();
    this.name = r.getName();
    this.netAmountAllocated = r.getNetAmountAllocated();
    this.netAmountRevised = r.getNetAmountRevised();
    this.netAmountUsed = r.getNetAmountUsed();
    this.grossAmountAllocated = r.getGrossAmountAllocated();
    this.grossAmountRevised = r.getGrossAmountRevised();
    this.grossAmountUsed = r.getGrossAmountUsed();

  }

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;

  public Key getKey() {
    return key;
  }

  public void setKey(Key key) {
    this.key = key;
  }

  public String getExpenseCode() {
    return expenseCode;
  }

  public void setExpenseCode(String expenseCode) {
    this.expenseCode = expenseCode;
  }

  public Integer getYear() {
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

  public Integer getNetAmountAllocated() {
    return netAmountAllocated;
  }

  public void setNetAmountAllocated(int netAmountAllocated) {
    this.netAmountAllocated = netAmountAllocated;
  }

  public Integer getNetAmountRevised() {
    return netAmountRevised;
  }

  public void setNetAmountRevised(int netAmountRevised) {
    this.netAmountRevised = netAmountRevised;
  }

  public Integer getNetAmountUsed() {
    return netAmountUsed;
  }

  public void setNetAmountUsed(int netAmountUsed) {
    this.netAmountUsed = netAmountUsed;
  }

  public Integer getGrossAmountAllocated() {
    return grossAmountAllocated;
  }

  public void setGrossAmountAllocated(int grossAmountAllocated) {
    this.grossAmountAllocated = grossAmountAllocated;
  }

  public Integer getGrossAmountRevised() {
    return grossAmountRevised;
  }

  public void setGrossAmountRevised(int grossAmountRevised) {
    this.grossAmountRevised = grossAmountRevised;
  }

  public Integer getGrossAmountUsed() {
    return grossAmountUsed;
  }

  public void setGrossAmountUsed(int grossAmountUsed) {
    this.grossAmountUsed = grossAmountUsed;
  }

  public ExpenseRecord toExpenseRecord() {
    return new ExpenseRecord(expenseCode, year, name, netAmountAllocated,
        netAmountRevised, netAmountUsed, grossAmountAllocated,
        grossAmountRevised, grossAmountUsed);
  }
}
