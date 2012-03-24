package com.yossale.server.cron;

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

import javax.jdo.PersistenceManager;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yossale.server.PMF;
import com.yossale.server.data.Section;
import com.yossale.server.util.Emailer;

/**
 * This servlet is broken right now, needs to be moved to Objectify.
 */
public class UpdateDBFromYedaServlet extends HttpServlet {
  private Logger logger = Logger.getLogger(UpdateDBFromYedaServlet.class
      .getName());
  
  private final Emailer emailer = new Emailer();
  
  private static final long serialVersionUID = 1773352816547081584L;
  private static final int MAX_SECTIONS_PER_YEAR = 1000000;
  private static final String YEAR = "year"; // GET parameter    

  public void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws IOException {
    if (1 == 1) {  // Fix this servlet
    	return;
    }
    logger.warning("doGet started");
    boolean succeeded = false;
    Date startTime = null;
    Date endTime = null;
    
    if (req.getParameter(YEAR) == null) {
      throw new IOException(YEAR + " parameter must be specified");
    }
    int year = Integer.valueOf(req.getParameter(YEAR));
    String sectionsLine = null;
    try {
      startTime = GregorianCalendar.getInstance().getTime();
      logger.warning("starting " + year);
      sectionsLine = fetchYearSections(year);
      JSONArray sections = new JSONArray(sectionsLine);
      logger.warning("fetched, going to store " + year);
      storeSections(sections);
      logger.warning("stored " + year);
      endTime = GregorianCalendar.getInstance().getTime();
      
      succeeded = true;           

    } catch (JSONException e) {
      logger.warning("got JSONException for " + sectionsLine);
      throw new IOException(e);
    } finally {
      SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");      
      if (succeeded) {
        emailer.sendHappyMailToAdmins("Cron load for year" + year + " succeded",
            "Started at " + df.format(startTime) + " and ended at " + df.format(endTime));
      } else {
        emailer.sendSadMailToAdmins("Cron load for year" + year + " failed",
            "Started at " + df.format(startTime));
      }
      
    }
    resp.getWriter().println(
        "yay!\ntook " + (endTime.getTime() - startTime.getTime()) / 1000
            + " secs");
  }

  private String fetchYearSections(int requestedYear) throws IOException,
      JSONException {
    URL url = new URL("http://api.yeda.us/data/gov/mof/budget/"
        + "?o=json&query="
        + URLEncoder.encode("{\"year\":" + requestedYear + "}", "utf-8")
        + "&limit=" + MAX_SECTIONS_PER_YEAR);
    logger.warning("fetching: " + requestedYear + " from url: "
        + url.toString());
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setConnectTimeout(50000);
    connection.setReadTimeout(50000);
    BufferedReader reader = new BufferedReader(new InputStreamReader(
        connection.getInputStream()));
    String line = reader.readLine();
    return line;
  }

  private Integer getIntField(JSONObject section, String fieldName)
      throws JSONException {
    if (section.has(fieldName)) {
      return section.getInt(fieldName);
    } else {
      return null;
    }
  }

  private void storeSections(JSONArray sectionArray) throws JSONException {
    PersistenceManager pm = null; // PMF.INSTANCE.getPersistenceManager();
    Vector<Section> sections = new Vector<Section>();
    int errors = 0;
    for (int i = 0; i < sectionArray.length(); ++i) {
      try {
        JSONObject sectionObject = (JSONObject) sectionArray.get(i);
        String sectionCode = sectionObject.get("code").toString();
        String parentCode = sectionCode.length() == 2 ? "" : sectionCode
            .substring(0, sectionCode.length() - 2);
        String name = sectionObject.get("title").toString();
        Integer year = getIntField(sectionObject, "year");
        Integer netAmountAllocated = getIntField(sectionObject, "net_allocated");
        Integer netAmountRevised = getIntField(sectionObject, "net_revised");
        Integer netAmountUsed = getIntField(sectionObject, "net_used");
        Integer grosAmountAllocated = getIntField(sectionObject,
            "gross_allocated");
        Integer grossAmountRevised = getIntField(sectionObject, "gross_revised");
        Integer grossAmountUsed = getIntField(sectionObject, "gross_used");

        Section section = new Section(sectionCode, parentCode, year, name,
            netAmountAllocated, netAmountRevised, netAmountUsed,
            grosAmountAllocated, grossAmountRevised, grossAmountUsed);

        sections.add(section);
      } catch (JSONException e) {
        logger.warning("data error " + errors++ + ": " + e.getMessage());
      }
    }
    pm.makePersistentAll(sections);
    pm.close();
  }
}
