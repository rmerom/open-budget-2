package com.yossale.server.data;

import javax.persistence.Id;

import com.yossale.client.data.SectionRecord;

public class Section {

	@Id
  private String key;

  // Mispar Se'if Takzivi as string.
  private String sectionCode;
  
  private String parentCode;

  private Integer year;

  private String name;

  private Integer netAmountAllocated;

  private Integer netAmountRevised;

  private Integer netAmountUsed;

  private Integer grossAmountAllocated;

  private Integer grossAmountRevised;

  private Integer grossAmountUsed;

  public Section(String sectionCode, String parentCode, Integer year, String name,
      Integer netAmountAllocated, Integer netAmountRevised, Integer netAmountUsed,
      Integer grosAmountAllocated, Integer grossAmountRevised, Integer grossAmountUsed) {
    this.sectionCode = sectionCode;  
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

	public Section() {
  }

  public String getKey() {
    return key;
  }

  public Section setKey(String key) {
    this.key = key;
    return this;
  }

  public Section(SectionRecord r) {
    this.sectionCode = r.getSectionCode();
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

  public Section setParentCode(String parentCode) {
    this.parentCode = parentCode;
    return this;
  }

  public String getSectionCode() {
    return sectionCode;
  }

  public Section setSectionCode(String sectionCode) {
    this.sectionCode = sectionCode;
    return this;
  }

  public Integer getYear() {
    return year;
  }

  public Section setYear(Integer year) {
    this.year = year;
    return this;
  }

  public String getName() {
    return name;
  }

  public Section setName(String name) {
    this.name = name;
    return this;
  }

  public Integer getNetAmountAllocated() {
    return netAmountAllocated;
  }

  public Section setNetAmountAllocated(Integer netAmountAllocated) {
    this.netAmountAllocated = netAmountAllocated;
    return this;
  }

  public Integer getNetAmountRevised() {
    return netAmountRevised;
  }

  public Section setNetAmountRevised(Integer netAmountRevised) {
    this.netAmountRevised = netAmountRevised;
    return this;
  }

  public Integer getNetAmountUsed() {
    return netAmountUsed;
  }

  public Section setNetAmountUsed(Integer netAmountUsed) {
    this.netAmountUsed = netAmountUsed;
    return this;
  }

  public Integer getGrossAmountAllocated() {
    return grossAmountAllocated;
  }

  public Section setGrossAmountAllocated(Integer grossAmountAllocated) {
    this.grossAmountAllocated = grossAmountAllocated;
    return this;
  }

  public Integer getGrossAmountRevised() {
    return grossAmountRevised;
  }

  public Section setGrossAmountRevised(Integer grossAmountRevised) {
    this.grossAmountRevised = grossAmountRevised;
    return this;
  }

  public Integer getGrossAmountUsed() {
    return grossAmountUsed;
  }

  public Section setGrossAmountUsed(Integer grossAmountUsed) {
    this.grossAmountUsed = grossAmountUsed;
    return this;
  }

  public SectionRecord toSectionRecord() {
    return new SectionRecord(sectionCode, parentCode, year, name, netAmountAllocated,
        netAmountRevised, netAmountUsed, grossAmountAllocated,
        grossAmountRevised, grossAmountUsed);
  }
  
	// TODO(ronme): integrate with SectionRecord.generateKey().
  private void generateKey() {
  	key =	new StringBuilder().append(year).append("_").append(sectionCode).toString();
  }

  @Override
  public String toString() {
    return "Section [key=" + key + ", sectionCode=" + sectionCode
        + ", parentCode=" + parentCode + ", year=" + year + ", name=" + name
        + "]";
  }
}
