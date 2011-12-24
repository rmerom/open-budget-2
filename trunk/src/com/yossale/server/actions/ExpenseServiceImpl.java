package com.yossale.server.actions;

import com.yossale.client.actions.ExpenseService;
import com.yossale.client.data.ExpenseRecord;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ExpenseServiceImpl extends RemoteServiceServlet implements ExpenseService{

	public ExpenseRecord[] getExpenses(int year) {
		return null;
	} 
}
