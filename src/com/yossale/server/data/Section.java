package com.yossale.server.data;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.yossale.client.data.SectionRecord;

@PersistenceCapable
public class Section {

  @PrimaryKey
  @Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
  private Key key;

  public Key getKey() {
    return key;
  }
  // Mispar Se'if Takzivi as string.
  @Persistent
  private String sectionCode;
  
  @Persistent
  private String parentCode;

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

  public Section(String sectionCode, String parentCode, Integer year, String name,
      Integer netAmountAllocated, Integer netAmountRevised, Integer netAmountUsed,
      Integer grosAmountAllocated, Integer grossAmountRevised, Integer grossAmountUsed) {
    this.sectionCode = sectionCode;  
//    this.parentCode = sectionCode.length() == 2 ? "" : sectionCode
//        .substring(0, sectionCode.length() - 2);
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

  public void setParentCode(String parentCode) {
    this.parentCode = parentCode;
  }

  public String getSectionCode() {
    return sectionCode;
  }

  public void setSectionCode(String sectionCode) {
    this.sectionCode = sectionCode;
  }

  public Integer getYear() {
    return year;
  }

  public void setYear(Integer year) {
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

  public void setNetAmountAllocated(Integer netAmountAllocated) {
    this.netAmountAllocated = netAmountAllocated;
  }

  public Integer getNetAmountRevised() {
    return netAmountRevised;
  }

  public void setNetAmountRevised(Integer netAmountRevised) {
    this.netAmountRevised = netAmountRevised;
  }

  public Integer getNetAmountUsed() {
    return netAmountUsed;
  }

  public void setNetAmountUsed(Integer netAmountUsed) {
    this.netAmountUsed = netAmountUsed;
  }

  public Integer getGrossAmountAllocated() {
    return grossAmountAllocated;
  }

  public void setGrossAmountAllocated(Integer grossAmountAllocated) {
    this.grossAmountAllocated = grossAmountAllocated;
  }

  public Integer getGrossAmountRevised() {
    return grossAmountRevised;
  }

  public void setGrossAmountRevised(Integer grossAmountRevised) {
    this.grossAmountRevised = grossAmountRevised;
  }

  public Integer getGrossAmountUsed() {
    return grossAmountUsed;
  }

  public void setGrossAmountUsed(Integer grossAmountUsed) {
    this.grossAmountUsed = grossAmountUsed;
  }

  public SectionRecord toSectionRecord() {
    return new SectionRecord(sectionCode, parentCode, year, name, netAmountAllocated,
        netAmountRevised, netAmountUsed, grossAmountAllocated,
        grossAmountRevised, grossAmountUsed);
  }
  
  private void generateKey() {
  	key =	KeyFactory.createKey(getClass().getSimpleName(), "" + year + sectionCode);
  }

  @Override
  public String toString() {
    return "Section [key=" + key + ", sectionCode=" + sectionCode
        + ", parentCode=" + parentCode + ", year=" + year + ", name=" + name
        + "]";
  }
  
  
}
