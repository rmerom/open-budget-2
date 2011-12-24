package com.yossale.server.actions;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.yossale.client.actions.BucketExpenseService;
import com.yossale.client.data.BucketRecord;
import com.yossale.client.data.ExpenseRecord;
import com.yossale.server.PMF;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.Expense;
import com.yossale.server.data.User;

public class BucketExpenseServiceImpl extends RemoteServiceServlet implements
    BucketExpenseService {

	@Override
	public ExpenseRecord[] getExpenses(long bucketId) {
		PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
		Key key = KeyFactory.createKey(Bucket.class.getSimpleName(), bucketId);
		Bucket bucket = null;
		try {
		  bucket = pm.getObjectById(Bucket.class, key);
		} catch (Exception e) {
			bucket = null;
		}
		if (bucket == null) {
			return null;
		}
		
		List<Expense> expenses = bucket.getExpenses();
		ExpenseRecord[] output = new ExpenseRecord[expenses.size()];
		int i = 0;
		for (Expense expense : expenses) {
			output[i++] = expense.toExpenseRecord();
		}
		return output;
	}

}
