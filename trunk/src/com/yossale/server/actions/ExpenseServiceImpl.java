package com.yossale.server.actions;

import com.yossale.client.actions.ExpenseService;
import com.yossale.client.data.ExpenseRecord;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ExpenseServiceImpl extends RemoteServiceServlet implements ExpenseService{

	public ExpenseRecord[] getExpenses(int year) {
		
	  ExpenseRecord rec1 = new ExpenseRecord("00",     year, "expense1", 0, 0, 0, 0, 0, 0);
	  ExpenseRecord rec2 = new ExpenseRecord("0001",   year, "expense2", 0, 0, 0, 0, 0, 0);
	  ExpenseRecord rec3 = new ExpenseRecord("000101", year, "expense3", 0, 0, 0, 0, 0, 0);
	  ExpenseRecord rec4 = new ExpenseRecord("0002",   year, "expense4", 0, 0, 0, 0, 0, 0);
	  
	  return new ExpenseRecord[] {rec1, rec2, rec3, rec4}; 
	} 
}
