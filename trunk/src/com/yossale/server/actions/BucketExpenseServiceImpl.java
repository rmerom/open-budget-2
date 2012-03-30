package com.yossale.server.actions;

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Objectify;
import com.yossale.client.actions.BucketExpenseService;
import com.yossale.client.data.BucketRecord;
import com.yossale.client.data.ExpenseRecord;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.DAO;

public class BucketExpenseServiceImpl extends RemoteServiceServlet implements
    BucketExpenseService {

	private static final long serialVersionUID = -4964841133537947859L;

	@Override
	public ExpenseRecord[] getExpenses(long bucketId) {
		Objectify ofy = new DAO().ofy();
		// TODO(ronme): add error handling, check against curent user;
		Bucket bucket = ofy.get(Bucket.class, bucketId);
		
		BucketRecord bucketRecord = bucket.toBucketRecord();
		return bucketRecord.getExpenses().toArray(new ExpenseRecord[0]); 
	}
	
	@Override
	public void updateBucketExpenses(long bucketId, ExpenseRecord[] expenseRecords) {
		Objectify ofy = new DAO().ofy();
		// TODO(ronme): add error handling, check against curent user;
		Bucket bucket = ofy.get(Bucket.class, bucketId);
		
		List<String> expenseStrings = new ArrayList<String>();
		for (ExpenseRecord expenseRecord : expenseRecords) {
			expenseStrings.add(expenseRecord.getId());
		}
		bucket.setExpenses(expenseStrings);
		ofy.put(bucket);
	}

}
