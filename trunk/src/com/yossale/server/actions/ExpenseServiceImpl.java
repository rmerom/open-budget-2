package com.yossale.server.actions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Vector;
import java.util.logging.Logger;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import com.yossale.client.actions.ExpenseService;
import com.yossale.client.data.ExpenseRecord;
import com.yossale.server.data.DAO;
import com.yossale.server.data.Expense;

@SuppressWarnings("serial")
public class ExpenseServiceImpl extends RemoteServiceServlet implements
    ExpenseService {
  
  private static Logger logger = Logger.getLogger(ExpenseServiceImpl.class.getName());

  public void addExpenseRecord(ExpenseRecord record) {
    Expense expense = new Expense(record);
    Objectify ofy = new DAO().ofy();
    try {
      ofy.put(expense);
    } catch (Exception ex) {
      System.out.println("Failed to commit to DB");
    }
  }

  public void removeAll() {
  	Objectify ofy = new DAO().ofy();
  	QueryResultIterable<Expense> results = ofy.query(Expense.class).fetch();
  	ofy.delete(results);
  }

  private ExpenseRecord generateExpenseRecord(JSONObject j) throws JSONException {    
    String expenseCode = j.getString("code");
    /**
     * Forgive me father, for I have sinned. This line is a logic duplication... :(
     * The same happens in the UpdateDBFromYedaServlet
     */
    String parentCode = expenseCode.length() == 2 ? "" : expenseCode
        .substring(0, expenseCode.length() - 2);
    String name = j.get("title").toString();
    Integer year = parseJson(j,"year");    
    Integer netAmountAllocated = parseJson(j, "net_allocated");
    Integer netAmountRevised = parseJson(j, "net_revised");
    Integer netAmountUsed = parseJson(j, "net_used");
    Integer grosAmountAllocated = parseJson(j, "gross_allocated");
    Integer grossAmountRevised = parseJson(j, "gross_revised");
    Integer grossAmountUsed = parseJson(j, "gross_used");

    ExpenseRecord r = new ExpenseRecord(expenseCode, parentCode, year, name,
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
 
  public ExpenseRecord[] getExpensesByYear(int year) {
    Objectify ofy = new DAO().ofy();
    logger.info("Loading expenses by year: " + year);  	
    Query<Expense> query = ofy.query(Expense.class).filter("year", year).order("expenseCode");
    
    ExpenseRecord[] results = executeQuery(query);

    System.out.println("Found " + results.length + " records");

    return results;
  }

  @Override
  public ExpenseRecord[] getExpensesByYearAndParent(int year, String parentCode) {
    logger.info("getExpensesByYearAndParent : " + year + "," + parentCode);
    
    String parentCodeFixed = (parentCode == null) ? "" : parentCode;
    Objectify ofy = new DAO().ofy();
    Query<Expense> query = ofy.query(Expense.class).filter("year", year).filter("parentCode", parentCodeFixed).order("expenseCode");
    
    ExpenseRecord[] results = executeQuery(query);

    logger.info("Found " + results.length  + " results for getExpensesByYearAndParent : " + year + "," + parentCode);

    return results;
  }

  @Override
  public String[] getAvailableBudgetYears() {    
    
    System.out.println("Querying getAvailableBudgetYears");
    logger.info("Querying getAvailableBudgetYears");

    Objectify ofy = new DAO().ofy();
    Query<Expense> query = ofy.query(Expense.class).filter("expenseCode", "00").order("year");

    QueryResultIterator<Expense> results = query.fetch().iterator();

    Vector<String> years = new Vector<String>();
    while (results.hasNext()) {
    	Expense expense = results.next();
    	years.add(Integer.valueOf(expense.getYear()).toString());
    }
    System.out.println("Found " + years.size() + " years");
    return years.toArray(new String[0]);
  }

  @Override
  public ExpenseRecord[] getExpenseByYearAndCode(int year, String expenseCode) {
    
    logger.info("getExpensesByYearAndCode: " + year + "," + expenseCode);
    
    Objectify ofy = new DAO().ofy();
    Query<Expense> query = ofy.query(Expense.class).filter("year", year).filter("expenseCode", expenseCode).order("expenseCode");
    
    ExpenseRecord[] results = executeQuery(query);

    logger.info("Found " + results.length  + " results found for getExpensesByYearAndCode: " + year + "," + expenseCode);

    return results;
  }

  private ExpenseRecord[] executeQuery(Query<Expense> query) {
    QueryResultIterator<Expense> results = query.fetch().iterator();

    Vector<ExpenseRecord> expenses = new Vector<ExpenseRecord>();
    while (results.hasNext()) {
      Expense expense = results.next();
      expenses.add(expense.toExpenseRecord());
    }

    System.out.println("Found " + expenses.size() + " records");

    return expenses.toArray(new ExpenseRecord[0]);
  	
  }
  
  @Override
  public ExpenseRecord[] getExpensesByNameAndCode(int year, String nameLike) {
    return new ExpenseRecord[]{};
  }

	@Override
	public void clear1000Expenses() {
    Objectify ofy = new DAO().ofy();
    Query<Expense> query = ofy.query(Expense.class);
    ofy.delete(query.fetch());
	}
}
