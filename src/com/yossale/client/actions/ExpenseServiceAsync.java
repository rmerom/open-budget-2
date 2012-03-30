package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.yossale.client.data.ExpenseRecord;

public interface ExpenseServiceAsync {
  
  void addExpenseRecord(ExpenseRecord record, AsyncCallback<Void> callback);

  void getExpensesByYear(int year, AsyncCallback<ExpenseRecord[]> callback);

  void removeAll(AsyncCallback<Void> callback);

  void loadYearData(String year, AsyncCallback<Void> callback);

  void getExpensesByYearAndParent(int year, String parentCode,
      AsyncCallback<ExpenseRecord[]> callback);

  void getAvailableBudgetYears(AsyncCallback<String[]> callback);

  void getExpenseByYearAndCode(int year, String code,
      AsyncCallback<ExpenseRecord[]> callback);

  void getExpensesByNameAndCode(int year, String nameLike,
      AsyncCallback<ExpenseRecord[]> callback);

	void getExpensesByCodeAndYears(String expenseCode, Integer[] years,
			AsyncCallback<ExpenseRecord[]> callback);
}
