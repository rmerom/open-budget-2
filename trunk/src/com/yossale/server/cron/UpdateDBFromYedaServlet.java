package com.yossale.server.cron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yossale.server.PMF;
import com.yossale.server.data.Expense;

public class UpdateDBFromYedaServlet extends HttpServlet {
	private Logger logger = Logger.getLogger(UpdateDBFromYedaServlet.class.getName());
	private static final long serialVersionUID = 1773352816547081584L;
	
	private static final int MAX_SECTIONS_PER_YEAR = 1000000;
	
	private static final String FROM_YEAR = "fromYear";  // GET parameter
	private static final String TO_YEAR = "toYear";  // GET parameter

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
		JSONArray sections;

		if (req.getParameter(FROM_YEAR) == null || req.getParameter(TO_YEAR) == null) {
			throw new IOException("both " + FROM_YEAR + " and " + TO_YEAR + " parameters must be specified");
		}
		int from = Integer.valueOf(req.getParameter(FROM_YEAR)); 
		int to = Integer.valueOf(req.getParameter(TO_YEAR));
	  if (to < from) {
	  	throw new IOException("invalid from/to year parameter");
	  }
				
	  for (int year = from; year <= to; ++year) {
			try {
				sections = fetchYearSections(year);
				storeSections(sections);
			} catch (JSONException e) {
				throw new IOException(e);
			}
	  }
		resp.getWriter().println("yay!");
  }
	
	private JSONArray fetchYearSections(int requestedYear) throws IOException, JSONException {
  	logger.warning("fetching: " + requestedYear);
  	URL url = new URL("http://api.yeda.us/data/gov/mof/budget/?o=json&query=%7B%22year%22:" 
	      + requestedYear + "%7D&limit=" + MAX_SECTIONS_PER_YEAR);
  	BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
  	String line = reader.readLine();
  	JSONArray sections = new JSONArray(line);
  	return sections;
	}
	
	private Integer getIntField(JSONObject section, String fieldName) throws JSONException {
		if (section.has(fieldName)) {
			return section.getInt(fieldName);
		} else {
			return null;
		}
	}
	
	private void storeSections(JSONArray sections) throws JSONException {
    PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
  	for (int i = 0; i < sections.length(); ++i) {
  		JSONObject section = (JSONObject) sections.get(i);
      String expenseCode = section.get("code").toString();
      String name = section.get("title").toString();
      Integer year = getIntField(section, "year");    
      Integer netAmountAllocated = getIntField(section, "net_allocated");
      Integer netAmountRevised = getIntField(section, "net_revised");
      Integer netAmountUsed = getIntField(section, "net_used");
      Integer grosAmountAllocated = getIntField(section, "gross_allocated");
      Integer grossAmountRevised = getIntField(section, "gross_revised");
      Integer grossAmountUsed = getIntField(section, "gross_used");
      
      Expense expense = new Expense(expenseCode, year, name,
          netAmountAllocated, netAmountRevised, netAmountUsed,
          grosAmountAllocated, grossAmountRevised, grossAmountUsed);
      
      pm.makePersistent(expense);
  	}
  	pm.close();
	}
}
