package com.yossale.server.cron;

import java.io.IOException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Logger;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import com.google.appengine.api.taskqueue.TaskOptions;
import com.google.appengine.api.taskqueue.TaskOptions.Method;

public class AddUpdateDBTaskServlet extends HttpServlet {
	private static final long serialVersionUID = 4405709356318561160L;
	private Logger logger = Logger.getLogger(AddUpdateDBTaskServlet.class.getName());
	private final static int MIN_YEAR = 1992;
	private final static String YEAR_OVERRIDE = "year";  // GET parameter.

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {

		int currentYear = GregorianCalendar.getInstance().get(Calendar.YEAR);
		long daysSinceEpoch = (new Date().getTime() / 
				(1000L /* msecs/sec */ * 60 /* sec/min */ * 60 /* min/hour */ * 24 /* hour/day */));
		long year = (daysSinceEpoch % (currentYear - MIN_YEAR + 1)) + MIN_YEAR;
		if (req.getParameter(YEAR_OVERRIDE) != null) {
			year = Integer.valueOf(req.getParameter(YEAR_OVERRIDE));
		  assert (year >= MIN_YEAR && year <= currentYear + 1);
		}
		logger.config("adding task for year " + year);
		
	  Queue defaultQueue = QueueFactory.getDefaultQueue();
	  TaskOptions taskOptions = 
	  		TaskOptions.Builder.withUrl("/cron/updatedb?year=" + year).method(Method.GET);
	  defaultQueue.add(taskOptions);
		resp.getWriter().println("yay!");
  }
}
