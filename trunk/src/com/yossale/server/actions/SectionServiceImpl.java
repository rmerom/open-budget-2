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
import com.yossale.client.actions.SectionService;
import com.yossale.client.data.SectionRecord;
import com.yossale.server.data.DAO;
import com.yossale.server.data.Section;

@SuppressWarnings("serial")
public class SectionServiceImpl extends RemoteServiceServlet implements
    SectionService {
  
  private static Logger logger = Logger.getLogger(SectionServiceImpl.class.getName());

  public void addSectionRecord(SectionRecord record) {
    Section section = new Section(record);
    Objectify ofy = new DAO().ofy();
    try {
      ofy.put(section);
    } catch (Exception ex) {
      System.out.println("Failed to commit to DB");
    }
  }

  public void removeAll() {
  	Objectify ofy = new DAO().ofy();
  	QueryResultIterable<Section> results = ofy.query(Section.class).fetch();
  	ofy.delete(results);
  }

  private SectionRecord generateSectionRecord(JSONObject j) throws JSONException {    
    String sectionCode = j.getString("code");
    /**
     * Forgive me father, for I have sinned. This line is a logic duplication... :(
     * The same happens in the UpdateDBFromYedaServlet
     */
    String parentCode = sectionCode.length() == 2 ? "" : sectionCode
        .substring(0, sectionCode.length() - 2);
    String name = j.get("title").toString();
    Integer year = parseJson(j,"year");    
    Integer netAmountAllocated = parseJson(j, "net_allocated");
    Integer netAmountRevised = parseJson(j, "net_revised");
    Integer netAmountUsed = parseJson(j, "net_used");
    Integer grosAmountAllocated = parseJson(j, "gross_allocated");
    Integer grossAmountRevised = parseJson(j, "gross_revised");
    Integer grossAmountUsed = parseJson(j, "gross_used");

    SectionRecord r = new SectionRecord(sectionCode, parentCode, year, name,
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
        final SectionRecord er = generateSectionRecord(obj);
        if (er != null) {
          addSectionRecord(er);
        }
      }

    } catch (Exception e) {
        System.out.println("Failed to commit Expsense record to DB");
    }
  }
 
  public SectionRecord[] getSectionsByYear(int year) {
    Objectify ofy = new DAO().ofy();
    logger.info("Loading sections by year: " + year);  	
    Query<Section> query = ofy.query(Section.class).filter("year", year).order("sectionCode");
    
    SectionRecord[] results = executeQuery(query);

    System.out.println("Found " + results.length + " records");

    return results;
  }

  @Override
  public SectionRecord[] getSectionsByYearAndParent(int year, String parentCode) {
    logger.info("getSectionsByYearAndParent : " + year + "," + parentCode);
    
    String parentCodeFixed = (parentCode == null) ? "" : parentCode;
    Objectify ofy = new DAO().ofy();
    Query<Section> query = ofy.query(Section.class).filter("year", year).filter("parentCode", parentCodeFixed).order("sectionCode");
    
    SectionRecord[] results = executeQuery(query);

    logger.info("Found " + results.length  + " results for getSectionsByYearAndParent : " + year + "," + parentCode);

    return results;
  }

  @Override
  public String[] getAvailableBudgetYears() {    
    
    System.out.println("Querying getAvailableBudgetYears");
    logger.info("Querying getAvailableBudgetYears");

    Objectify ofy = new DAO().ofy();
    Query<Section> query = ofy.query(Section.class).filter("sectionCode", "00").order("year");

    QueryResultIterator<Section> results = query.fetch().iterator();

    Vector<String> years = new Vector<String>();
    while (results.hasNext()) {
    	Section section = results.next();
    	years.add(Integer.valueOf(section.getYear()).toString());
    }
    System.out.println("Found " + years.size() + " years");
    return years.toArray(new String[0]);
  }

  @Override
  public SectionRecord[] getSectionByYearAndCode(int year, String sectionCode) {
    
    logger.info("getSectionsByYearAndCode: " + year + "," + sectionCode);
    
    Objectify ofy = new DAO().ofy();
    Query<Section> query = ofy.query(Section.class).filter("year", year).filter("sectionCode", sectionCode).order("sectionCode");
    
    SectionRecord[] results = executeQuery(query);

    logger.info("Found " + results.length  + " results found for getSectionsByYearAndCode: " + year + "," + sectionCode);

    return results;
  }

  private SectionRecord[] executeQuery(Query<Section> query) {
    QueryResultIterator<Section> results = query.fetch().iterator();

    Vector<SectionRecord> sections = new Vector<SectionRecord>();
    while (results.hasNext()) {
      Section section = results.next();
      sections.add(section.toSectionRecord());
    }

    System.out.println("Found " + sections.size() + " records");

    return sections.toArray(new SectionRecord[0]);
  	
  }
  
  @Override
  public SectionRecord[] getSectionsByNameAndCode(int year, String nameLike) {
    return new SectionRecord[]{};
  }
}
