package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.yossale.client.data.SectionRecord;

@RemoteServiceRelativePath("section")
public interface SectionService extends RemoteService {

	SectionRecord[] getSections(int year);	
	SectionRecord[] getSectionsByYear(int year);
	SectionRecord[] getSectionsByYearAndParent(int year, String parentCode);
	String[] getAvailableBudgetYears();
	SectionRecord[] getSectionByYearAndCode(int year, String code);
	SectionRecord[] getSectionsByNameAndCode(int year, String nameLike);
	void addSectionRecord(SectionRecord record);
	void removeAll();
	void loadYearData(String year);
	

	
}
