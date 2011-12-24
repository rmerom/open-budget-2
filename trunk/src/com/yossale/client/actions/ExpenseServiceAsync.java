package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.yossale.client.data.ExpenseRecord;

public interface ExpenseServiceAsync {
	void getExpenses(int years, AsyncCallback<ExpenseRecord[]> callback);
}
