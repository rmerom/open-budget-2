package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.yossale.client.data.SectionRecord;

public interface SectionServiceAsync {
	void getSections(int years, AsyncCallback<SectionRecord[]> callback);

  void addSectionRecord(SectionRecord record, AsyncCallback<Void> callback);

  void getSectionsByYear(int year, AsyncCallback<SectionRecord[]> callback);

  void removeAll(AsyncCallback<Void> callback);

  void loadYearData(String year, AsyncCallback<Void> callback);
}
