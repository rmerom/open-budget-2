package com.yossale.shared;

import java.util.HashMap;
import java.util.Map;


public class RpcExpenseObject {

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

  public Map<String, Object> convertToPropertiesMap() {
    Map<String, Object> map = new HashMap<String, Object>();
    map.put("expenseCode", expenseCode);
    map.put("year", year);
    map.put("name", name);
    map.put("netAmountAllocated", netAmountAllocated);
    map.put("netAmountRevised", netAmountRevised);
    map.put("netAmountUsed", netAmountUsed);
    map.put("grosAmountAllocated", grosAmountAllocated);
    map.put("grossAmountRevised", grossAmountRevised);
    map.put("grossAmountUsed", grossAmountUsed);
    return map;
    
    
    
  }
  
  public RpcExpenseObject(String expenseCode, int year, String name,
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

  public int getGrosAmountAllocated() {
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
  
  

}
