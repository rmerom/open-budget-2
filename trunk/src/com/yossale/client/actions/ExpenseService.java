package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.yossale.client.data.ExpenseRecord;

@RemoteServiceRelativePath("expense")
public interface ExpenseService extends RemoteService {

	ExpenseRecord[] getExpensesByCodeAndYears(String expenseCode, Integer[] years);
	ExpenseRecord[] getExpensesByYear(int year);
	ExpenseRecord[] getExpensesByYearAndParent(int year, String parentCode);
	String[] getAvailableBudgetYears();
	ExpenseRecord[] getExpenseByYearAndCode(int year, String code);
	ExpenseRecord[] getExpensesByNameAndCode(int year, String nameLike);
	void addExpenseRecord(ExpenseRecord record);
	void removeAll();
	void loadYearData(String year);
}
