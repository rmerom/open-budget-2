package com.yossale.server.tasks;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import com.yossale.server.data.DAO;
import com.yossale.server.data.Expense;
import com.yossale.server.util.Emailer;

/**
 * Deletes expenses from the DB. Takes one optional parameter, "limit" - the number of 
 * expenses to delete. If omitted, tries to remove all expenses. 
 */
public class DeleteExpensesTask extends HttpServlet {

  private Logger logger = Logger.getLogger(DeleteExpensesTask.class
      .getName());

	private static final long serialVersionUID = 3912348812676963824L;
	private static final String LIMIT = "limit";
  private final Emailer emailer = new Emailer();

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    logger.warning("delete started");
    
    int limit = 0;
    if (req.getParameter(LIMIT) != null) {
    	limit = Integer.valueOf(req.getParameter(LIMIT));
    }
    Objectify ofy = new DAO().ofy();
    Query<Expense> query = ofy.query(Expense.class);
    if (limit > 0) {
    	query = query.limit(limit);
    }
    try {
    	ofy.delete(query.fetch());
    } catch (RuntimeException e) {
    	emailer.sendSadMailToAdmins("Deleting expenses failed", "exception:\n" + e.getMessage());
    	throw e;
    }
    emailer.sendHappyMailToAdmins("Expenses removed successfully: " + limit, "");
  }
}
