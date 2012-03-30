package com.yossale.server.tasks;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

/**
 * Adds a task to delete expenses.
 * 
 * Takes one optional parameter, "limit" - the number of 
 * expenses to delete. If omitted, tries to remove all expenses.
 *
 */
public class AddDeleteExpensesTaskServlet extends HttpServlet {

	private static final long serialVersionUID = -265214814502888955L;
	private static final String LIMIT = "limit";

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
		
	  Queue defaultQueue = QueueFactory.getDefaultQueue();
	  String url = "/tasks/deleteexpenses";
	  if (req.getParameter(LIMIT ) != null) {
	  	url = url + "?limit=" + req.getParameter(LIMIT); 
	  }
	  TaskOptions taskOptions = 
	  		TaskOptions.Builder.withUrl(url).method(Method.GET);
	  defaultQueue.add(taskOptions);
		resp.getWriter().println("task added!");
  }

}
