package com.yossale.server.actions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

  @SuppressWarnings("unchecked")
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

  private ExpenseRecord generateExpenseRecord(JSONObject j) throws JSONException {    

    String expenseCode = j.getString("code");
    String name = j.get("title").toString();
    Integer year = parseJson(j,"year");    
    Integer netAmountAllocated = parseJson(j, "net_allocated");
    Integer netAmountRevised = parseJson(j, "net_revised");
    Integer netAmountUsed = parseJson(j, "net_used");
    Integer grosAmountAllocated = parseJson(j, "gross_allocated");
    Integer grossAmountRevised = parseJson(j, "gross_revised");
    Integer grossAmountUsed = parseJson(j, "gross_used");

    ExpenseRecord r = new ExpenseRecord(expenseCode, year, name,
        netAmountAllocated, netAmountRevised, netAmountUsed,
        grosAmountAllocated, grossAmountRevised, grossAmountUsed);

    return r;
  }

  private Integer parseJson(JSONObject j, String property) {

    if (!j.has(property)) {
      return 0;
    } 
    Integer p = 0;
    try {
      p = j.getInt(property);
    } catch (JSONException e) {
      // TODO Auto-generated catch block      
    }
    return p;

  }

  public void loadYearData(String year) {

    if (year == null) {
      return;
    }

    String fullPath = getServletConfig().getServletContext().getRealPath(
        "/data/"+ year +".txt");
    final StringBuilder builder = new StringBuilder("");
    try {
      // url = new URL("http://127.0.0.1:8888/data/testJson.txt");

      BufferedReader reader = new BufferedReader(new InputStreamReader(
          new FileInputStream(fullPath)));

      String line = null;

      while ((line = reader.readLine()) != null) {
        builder.append(line);
      }
      reader.close();
    } catch (MalformedURLException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    String json = builder.toString();

    try {

      JSONArray arr = new JSONArray(json);
      System.out.println("Found " + arr.length() + " records");
      
      for (int i = 0; i < arr.length(); i++) {

        JSONObject obj = arr.getJSONObject(i);
        final ExpenseRecord er = generateExpenseRecord(obj);
        if (er != null) {
          addExpenseRecord(er);
        }

      }

    } catch (Exception e) {
        System.out.println("Failed to commit Expsense record to DB");
    }
  }
 
  @SuppressWarnings("unchecked")
  public ExpenseRecord[] getExpensesByYear(int year) {
  	List<Expense> results = new ArrayList<Expense>();
  	
  	PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
    Query query = pm.newQuery(Expense.class);
    query.setFilter("year == expenseYearParam");
    query.setOrdering("expenseCode desc");
    query.declareParameters("Integer expenseYearParam");

    int startPos = 0;
    query.setRange(startPos, startPos + 1000);
    List<Expense> rangeResults = (List<Expense>) query.execute(year);
    while (rangeResults != null && !rangeResults.isEmpty()) {
    	results.addAll(rangeResults);
    	startPos = startPos + 1000;
    	query.setRange(startPos, startPos + 1000);
      rangeResults = (List<Expense>) query.execute(year);
    }

    if (results == null || results.isEmpty()) {
      return new ExpenseRecord[] {};
    }

    ExpenseRecord[] expensesArr = new ExpenseRecord[results.size()];
    System.out.println("Found " + results.size() + " records");
    for (int i = 0; i < results.size(); i++) {
      Expense e = results.get(i);
      expensesArr[i] = e.toExpenseRecord();
    }

    return expensesArr;
  }
}
