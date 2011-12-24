package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.yossale.client.data.ExpenseRecord;

@RemoteServiceRelativePath("bucket_expense")
public interface BucketExpenseService extends RemoteService {
	ExpenseRecord[] getExpenses(long bucketId);
}
