package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.yossale.client.data.ExpenseRecord;

public interface BucketExpenseServiceAsync {
	void getExpenses(long bucketId, AsyncCallback<ExpenseRecord[]> callback);
}
