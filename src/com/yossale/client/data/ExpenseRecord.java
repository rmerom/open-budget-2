package com.yossale.client.data;

import java.io.Serializable;
import com.allen_sauer.gwt.log.client.Log;
import com.smartgwt.client.data.Record;

public class ExpenseRecord implements Serializable  {

	private static final long serialVersionUID = -2358987888595684650L;
	
	// Mispar Se'if Takzivi as string.
	private String expenseCode = "";	
	private String parentCode = "";	
	private String name = "";
	private Integer year = 0;
	private Integer netAmountAllocated = 0;
	private Integer netAmountRevised = 0;
	private Integer netAmountUsed = 0;
	private Integer grosAmountAllocated = 0;
	private Integer grossAmountRevised = 0;
	private Integer grossAmountUsed = 0;
	private String id;
	
	public static Record getRecord(ExpenseRecord s) {
	  Record r = new Record();
	  Log.info(s.toString());
	  r.setAttribute("expenseCode", s.getExpenseCode());
	  r.setAttribute("parentCode", s.getParentCode());
	  r.setAttribute("name", s.getName());
	  r.setAttribute("year", s.getYear());
	  r.setAttribute("netAmountAllocated", s.getNetAmountAllocated());
	  r.setAttribute("netAmountRevised", s.getNetAmountRevised());
	  r.setAttribute("netAmountUsed", s.getNetAmountUsed());
	  r.setAttribute("grosAmountAllocated", s.getGrossAmountAllocated());
	  r.setAttribute("grossAmountRevised", s.getGrossAmountRevised());
	  r.setAttribute("grossAmountUsed", s.getGrossAmountUsed());
	  r.setAttribute("id", generateKey(s.getYear(), s.getExpenseCode()));
	  return r;
	}
		
	public static ExpenseRecord getExpenseRecord(Record r) {
    ExpenseRecord s = new ExpenseRecord();
    s.setExpenseCode(r.getAttribute("expenseCode"));
    s.setParentCode(r.getAttribute("parentCode"));
    s.setName(r.getAttribute("name"));
    
    s.setYear(r.getAttributeAsInt("year"));
    s.setNetAmountAllocated(getIntAttribute(r,"netAmountAllocated"));
    s.setNetAmountRevised(getIntAttribute(r,"netAmountRevised"));
    s.setNetAmountUsed(getIntAttribute(r,"netAmountUsed"));
    s.setGrosAmountAllocated(getIntAttribute(r,"grosAmountAllocated"));
    s.setGrossAmountRevised(getIntAttribute(r,"grossAmountRevised"));
    s.setGrossAmountUsed(getIntAttribute(r,"grossAmountUsed"));
    s.setId(generateKey(s.getYear(), s.getExpenseCode()));
    return s;
  }
	
	private static int getIntAttribute(Record r, String att, int defaultValue) {
	  String val = r.getAttribute(att);
	  if (val == null) {
	    return defaultValue;
	  }
	  
	  return Integer.parseInt(val);
	}
	
	private static int getIntAttribute(Record r, String att) {        
    return getIntAttribute(r, att, 0);
  }
	
	
	
	public ExpenseRecord() {  
	}

	public ExpenseRecord(String expenseCode, String parentCode, Integer year, String name,
	    Integer netAmountAllocated, Integer netAmountRevised, Integer netAmountUsed,
	    Integer grosAmountAllocated, Integer grossAmountRevised, Integer grossAmountUsed) {
		super();
		this.expenseCode = expenseCode;
		this.parentCode = parentCode;
		this.year = year;
		this.name = name;
		this.netAmountAllocated = netAmountAllocated;
		this.netAmountRevised = netAmountRevised;
		this.netAmountUsed = netAmountUsed;
		this.grosAmountAllocated = grosAmountAllocated;
		this.grossAmountRevised = grossAmountRevised;
		this.grossAmountUsed = grossAmountUsed;
		this.id = generateKey(year, expenseCode);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getExpenseCode() {
		return expenseCode;
	}

	public void setExpenseCode(String expenseCode) {
		this.expenseCode = expenseCode;
	}

	public String getParentCode() {
    return parentCode;
  }

  public void setParentCode(String parentCode) {
    this.parentCode = parentCode;
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
		return grosAmountAllocated;
	}

	public void setGrosAmountAllocated(int grosAmountAllocated) {
		this.grosAmountAllocated = grosAmountAllocated;
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
	
	// TODO(ronme): integrate with Expense.generateKey().
	public static String generateKey(int year, String expenseCode) {
		return new StringBuilder().append(year).append("_").append(expenseCode).toString();
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
