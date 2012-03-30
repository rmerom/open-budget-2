package com.yossale.server.tasks;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Vector;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.googlecode.objectify.Objectify;
import com.yossale.server.data.DAO;
import com.yossale.server.data.Expense;
import com.yossale.server.util.Emailer;

/**
 * Retrieves data from the yeda API for a given year. 
 * Takes one parameter, "year" - the year to fetch.
 * 
 * Should be run as a task.
 */
public class UpdateDBFromYedaTask extends HttpServlet {
  private Logger logger = Logger.getLogger(UpdateDBFromYedaTask.class
      .getName());
  
  private final Emailer emailer = new Emailer();
  
  private static final long serialVersionUID = 1773352816547081584L;
  private static final int MAX_EXPENSES_PER_YEAR = 1000000;
  private static final String YEAR = "year"; // GET parameter    

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    logger.warning("updateDbFromYedaTask started");
    boolean succeeded = false;
    Date startTime = null;
    Date endTime = null;
    
    if (req.getParameter(YEAR) == null) {
      throw new IOException(YEAR + " parameter must be specified");
    }
    int year = Integer.valueOf(req.getParameter(YEAR));
    String expensesLine = null;
    try {
      startTime = GregorianCalendar.getInstance().getTime();
      logger.warning("starting " + year);
      expensesLine = fetchYearExpenses(year);
      JSONArray expenses = new JSONArray(expensesLine);
      logger.warning("fetched, going to store " + year);
      storeExpenses(expenses);
      logger.warning("stored " + year);
      endTime = GregorianCalendar.getInstance().getTime();
      
      succeeded = true;           

    } catch (JSONException e) {
      logger.warning("got JSONException for " + expensesLine);
      throw new IOException(e);
    } finally {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");      
      if (succeeded) {
        emailer.sendHappyMailToAdmins("Cron load for year" + year + " succeded",
            "Started at " + df.format(startTime) + " and ended at " + df.format(endTime));
      } else {
        emailer.sendSadMailToAdmins("Cron load for year" + year + " failed",
            "Started at " + df.format(startTime) + "\n\nresult started with:" 
            + (expensesLine != null && !expensesLine.isEmpty() 
                ? expensesLine.substring(0, Math.min(expensesLine.length(), 1000)) 
                : "null"));
      }
      
    }
    resp.getWriter().println(
        "yay!\ntook " + (endTime.getTime() - startTime.getTime()) / 1000
            + " secs");
  }

  private String fetchYearExpenses(int requestedYear) throws IOException,
      JSONException {
    URL url = new URL("http://api.yeda.us/data/gov/mof/budget/"
        + "?o=json&query="
        + URLEncoder.encode("{\"year\":" + requestedYear + "}", "utf-8")
        + "&limit=" + MAX_EXPENSES_PER_YEAR);
    logger.warning("fetching: " + requestedYear + " from url: "
        + url.toString());
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setConnectTimeout(50000);
    connection.setReadTimeout(50000);
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        connection.getInputStream()));
    String line;
    StringBuilder result = new StringBuilder();
    while ((line = reader.readLine()) != null) {
    	result.append(line);
    }
    return result.toString();
  }

  private Integer getIntField(JSONObject expense, String fieldName)
      throws JSONException {
    if (expense.has(fieldName)) {
      return expense.getInt(fieldName);
    } else {
      return null;
    }
  }

  private void storeExpenses(JSONArray expenseArray) throws JSONException {
  	Objectify ofy = new DAO().ofy();
    Vector<Expense> expenses = new Vector<Expense>();
    int errors = 0;
    for (int i = 0; i < expenseArray.length(); ++i) {
      try {
        JSONObject expenseObject = (JSONObject) expenseArray.get(i);
        String expenseCode = expenseObject.get("code").toString();
        String parentCode = expenseCode.length() == 2 ? "" : expenseCode
            .substring(0, expenseCode.length() - 2);
        String name = expenseObject.get("title").toString();
        Integer year = getIntField(expenseObject, "year");
        Integer netAmountAllocated = getIntField(expenseObject, "net_allocated");
        Integer netAmountRevised = getIntField(expenseObject, "net_revised");
        Integer netAmountUsed = getIntField(expenseObject, "net_used");
        Integer grosAmountAllocated = getIntField(expenseObject,
            "gross_allocated");
        Integer grossAmountRevised = getIntField(expenseObject, "gross_revised");
        Integer grossAmountUsed = getIntField(expenseObject, "gross_used");

        Expense expense = new Expense(expenseCode, parentCode, year, name,
            netAmountAllocated, netAmountRevised, netAmountUsed,
            grosAmountAllocated, grossAmountRevised, grossAmountUsed);

        expenses.add(expense);
      } catch (JSONException e) {
        logger.warning("data error " + errors++ + ": " + e.getMessage());
      }
    }
    ofy.put(expenses);
  }
}
