package com.yossale.server.actions;

import com.yossale.client.actions.ExpenseService;
import com.yossale.client.data.ExpenseRecord;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
public class ExpenseServiceImpl extends RemoteServiceServlet implements ExpenseService{

	public ExpenseRecord[] getExpenses(int year) {
		
	  ExpenseRecord rec1 = new ExpenseRecord("00",     year, "expense1", 101, 102, 103, 104, 105, 106);
	  ExpenseRecord rec2 = new ExpenseRecord("0001",   year, "expense2", 201, 202, 203, 204, 205, 206);
	  ExpenseRecord rec3 = new ExpenseRecord("000101", year, "expense3", 301, 302, 303, 304, 305, 306);
	  ExpenseRecord rec4 = new ExpenseRecord("0002",   year, "expense4", 401, 402, 403, 404, 405, 406);
	  
	  return new ExpenseRecord[] {rec1, rec2, rec3, rec4}; 
	} 
}
