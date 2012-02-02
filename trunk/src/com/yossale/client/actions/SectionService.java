package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.yossale.client.data.SectionRecord;

@RemoteServiceRelativePath("section")
public interface SectionService extends RemoteService {

	SectionRecord[] getSections(int year);
	void addSectionRecord(SectionRecord record);
	SectionRecord[] getSectionsByYear(int year);
	SectionRecord[] getSectionsByYearAndParent(int year, String parentCode);
	String[] getAvailableBudgetYears();
	void removeAll();
	void loadYearData(String year);

	
}
