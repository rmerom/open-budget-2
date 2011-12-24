package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.yossale.client.data.ExpenseRecord;

@RemoteServiceRelativePath("expense")
public interface ExpenseService extends RemoteService {

	ExpenseRecord[] getExpenses(int year);
}
