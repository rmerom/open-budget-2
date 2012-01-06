package com.yossale.server.actions;

import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.yossale.client.actions.ExpenseService;
import com.yossale.client.data.ExpenseRecord;
import com.yossale.server.PMF;
import com.yossale.server.data.Expense;

@SuppressWarnings("serial")
public class ExpenseServiceImpl extends RemoteServiceServlet implements
    ExpenseService {

  public ExpenseRecord[] getExpenses(int year) {

    ExpenseRecord rec1 = new ExpenseRecord("00", year, "expense1", 101, 102,
        103, 104, 105, 106);
    ExpenseRecord rec2 = new ExpenseRecord("0001", year, "expense2", 201, 202,
        203, 204, 205, 206);
    ExpenseRecord rec3 = new ExpenseRecord("000101", year, "expense3", 301,
        302, 303, 304, 305, 306);
    ExpenseRecord rec4 = new ExpenseRecord("0002", year, "expense4", 401, 402,
        403, 404, 405, 406);

    return new ExpenseRecord[] { rec1, rec2, rec3, rec4 };
  }

  public void addExpenseRecord(ExpenseRecord record) {

    PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
    Expense e = new Expense(record);
    try {
      pm.makePersistent(e);
    } catch (Exception ex) {
      System.out.println("Failed to commit to DB");
    } finally {
      pm.close();
    }
  }

  public void removeAll() {
    PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
    Query query = pm.newQuery(Expense.class);
    
    try {
      List<Expense> results = (List<Expense>) query.execute();
      pm.deletePersistentAll(results);
    } catch (Exception e) {
      System.out.println("Failed to remove all instances");
    } finally {
      pm.close();
    }
  }

  public ExpenseRecord[] getExpensesByYear(int year) {

    PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
    Query query = pm.newQuery(Expense.class);

    // query.setFilter("year == expenseYearParam");
    // query.setOrdering("expenseCode desc");
    // query.declareParameters("String expenseYearParam");

    List<Expense> results = null;

    try {
      results = (List<Expense>) query.execute();
    } catch (Exception e) {
      System.out.println("Failed to run query");

    } finally {
      query.closeAll();
    }

    if (results == null || results.isEmpty()) {
      return new ExpenseRecord[] {};
    }

    ExpenseRecord[] expensesArr = new ExpenseRecord[results.size()];
    for (int i = 0; i < results.size(); i++) {

      Expense e = results.get(i);
      expensesArr[i] = e.toExpenseRecord();
    }

    return expensesArr;
  }
}
