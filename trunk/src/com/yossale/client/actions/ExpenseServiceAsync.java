package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.yossale.client.data.ExpenseRecord;

public interface ExpenseServiceAsync {
	void getExpenses(int years, AsyncCallback<ExpenseRecord[]> callback);

  void addExpenseRecord(ExpenseRecord record, AsyncCallback<Void> callback);

  void getExpensesByYear(int year, AsyncCallback<ExpenseRecord[]> callback);

  void removeAll(AsyncCallback<Void> callback);

  void loadYearData(String year, AsyncCallback<Void> callback);
}
