package com.yossale.server.cron;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.Vector;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yossale.server.PMF;
import com.yossale.server.data.Section;

public class UpdateDBFromYedaServlet extends HttpServlet {
	private Logger logger = Logger.getLogger(UpdateDBFromYedaServlet.class.getName());
	private static final long serialVersionUID = 1773352816547081584L;
	
	private static final int MAX_SECTIONS_PER_YEAR = 1000000;
	
	private static final String FROM_YEAR = "fromYear";  // GET parameter
	private static final String TO_YEAR = "toYear";  // GET parameter

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
		JSONArray sections;

		logger.warning("doGet started");
		Date start = new Date();
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
				logger.warning("starting " + year);
				sections = fetchYearSections(year);
				logger.warning("fetched, going to store " + year);
				storeSections(sections);
				logger.warning("stored " + year);
			} catch (JSONException e) {
				throw new IOException(e);
			}
	  }
		resp.getWriter().println("yay!\ntook " + (new Date().getTime() - start.getTime()) / 1000 + " secs");
  }
	
	private JSONArray fetchYearSections(int requestedYear) throws IOException, JSONException {
  	logger.warning("fetching: " + requestedYear);
  	URL url = new URL("http://api.yeda.us/data/gov/mof/budget/?o=json&query=%7B%22year%22:" 
	      + requestedYear + "%7D&limit=" + MAX_SECTIONS_PER_YEAR);
  	HttpURLConnection connection = (HttpURLConnection) url.openConnection();
  	connection.setConnectTimeout(50000);
  	connection.setReadTimeout(50000);
  	BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
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
	
	private void storeSections(JSONArray sectionArray) throws JSONException {
    PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
    Vector<Section> sections = new Vector<Section>();
  	for (int i = 0; i < sectionArray.length(); ++i) {
  		JSONObject sectionObject = (JSONObject) sectionArray.get(i);
      String sectionCode = sectionObject.get("code").toString();
      String name = sectionObject.get("title").toString();
      Integer year = getIntField(sectionObject, "year");    
      Integer netAmountAllocated = getIntField(sectionObject, "net_allocated");
      Integer netAmountRevised = getIntField(sectionObject, "net_revised");
      Integer netAmountUsed = getIntField(sectionObject, "net_used");
      Integer grosAmountAllocated = getIntField(sectionObject, "gross_allocated");
      Integer grossAmountRevised = getIntField(sectionObject, "gross_revised");
      Integer grossAmountUsed = getIntField(sectionObject, "gross_used");
      
      Section section = new Section(sectionCode, year, name,
          netAmountAllocated, netAmountRevised, netAmountUsed,
          grosAmountAllocated, grossAmountRevised, grossAmountUsed);
      
      sections.add(section);
  	}
    pm.makePersistent(sections);
  	pm.close();
	}
}
