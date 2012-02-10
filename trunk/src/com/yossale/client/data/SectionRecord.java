package com.yossale.client.data;

import java.io.Serializable;

import com.smartgwt.client.data.Record;

public class SectionRecord implements Serializable  {

	private static final long serialVersionUID = -2358987888595684650L;
	
	// Mispar Se'if Takzivi as string.
	private String sectionCode = "";	
	private String parentCode = "";	
	private String name = "";
	private Integer year = 0;
	private Integer netAmountAllocated = 0;
	private Integer netAmountRevised = 0;
	private Integer netAmountUsed = 0;
	private Integer grosAmountAllocated = 0;
	private Integer grossAmountRevised = 0;
	private Integer grossAmountUsed = 0;
	
	public static Record getRecord(SectionRecord s) {
	  Record r = new Record();
	  r.setAttribute("sectionCode", s.getSectionCode());
	  r.setAttribute("parentCode", s.getParentCode());
	  r.setAttribute("name", s.getName());
	  r.setAttribute("year", s.getYear());
	  r.setAttribute("netAmountAllocated", s.getNetAmountAllocated());
	  r.setAttribute("netAmountRevised", s.getNetAmountRevised());
	  r.setAttribute("netAmountUsed", s.getNetAmountUsed());
	  r.setAttribute("grosAmountAllocated", s.getGrossAmountAllocated());
	  r.setAttribute("grossAmountRevised", s.getGrossAmountRevised());
	  r.setAttribute("grossAmountUsed", s.getGrossAmountUsed());
	  r.setAttribute("id", s.getYear()+"_"+s.getSectionCode());
	  return r;
	}
	
	public static SectionRecord getSectionRecord(Record r) {
    SectionRecord s = new SectionRecord();
    s.setSectionCode(r.getAttribute("sectionCode"));
    s.setSectionCode(r.getAttribute("parentCode"));
    s.setName(r.getAttribute("name"));
    
    s.setYear(r.getAttributeAsInt("year"));
    s.setNetAmountAllocated(r.getAttributeAsInt("netAmountAllocated"));
    s.setNetAmountRevised(r.getAttributeAsInt("netAmountRevised"));
    s.setNetAmountUsed(r.getAttributeAsInt("netAmountUsed"));
    s.setGrosAmountAllocated(r.getAttributeAsInt("grosAmountAllocated"));
    s.setGrossAmountRevised(r.getAttributeAsInt("grossAmountRevised"));
    s.setGrossAmountUsed(r.getAttributeAsInt("grossAmountUsed"));
    return s;
  }
	
	public SectionRecord() {  
	}

	public SectionRecord(String sectionCode, String parentCode, Integer year, String name,
	    Integer netAmountAllocated, Integer netAmountRevised, Integer netAmountUsed,
	    Integer grosAmountAllocated, Integer grossAmountRevised, Integer grossAmountUsed) {
		super();
		this.sectionCode = sectionCode;
		this.parentCode = parentCode;
		this.year = year;
		this.name = name;
		this.netAmountAllocated = netAmountAllocated;
		this.netAmountRevised = netAmountRevised;
		this.netAmountUsed = netAmountUsed;
		this.grosAmountAllocated = grosAmountAllocated;
		this.grossAmountRevised = grossAmountRevised;
		this.grossAmountUsed = grossAmountUsed;
	}

	public String getSectionCode() {
		return sectionCode;
	}

	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}

	public String getParentCode() {
    return parentCode;
  }

  public void setParentCode(String parentCode) {
    this.parentCode = parentCode;
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
		return "SectionRecord [sectionCode=" + sectionCode
				+ ", grosAmountAllocated=" + grosAmountAllocated
				+ ", grossAmountRevised=" + grossAmountRevised
				+ ", grossAmountUsed=" + grossAmountUsed + ", name=" + name
				+ ", netAmountAllocated=" + netAmountAllocated
				+ ", netAmountRevised=" + netAmountRevised + ", netAmountUsed="
				+ netAmountUsed + ", year=" + year + "]";
	}
	
	public SectionRecord add(SectionRecord other) {		
		
		this.netAmountAllocated += other.netAmountAllocated;
		this.netAmountRevised += other.netAmountRevised;
		this.netAmountUsed += other.netAmountUsed;
		this.grosAmountAllocated += other.grosAmountAllocated;
		this.grossAmountRevised += other.grossAmountRevised;
		this.grossAmountUsed += other.grossAmountUsed;
		
		return this;		
	}
}
