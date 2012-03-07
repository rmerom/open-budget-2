package com.yossale.server.actions;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.yossale.client.actions.SectionService;
import com.yossale.client.data.SectionRecord;
import com.yossale.server.PMF;
import com.yossale.server.data.Section;

@SuppressWarnings("serial")
public class SectionServiceImpl extends RemoteServiceServlet implements
    SectionService {
  
  private static Logger logger = Logger.getLogger(SectionServiceImpl.class.getName());

  public SectionRecord[] getSections(int year) {

    SectionRecord rec1 = new SectionRecord("00", "", year, "section1", 101, 102,
        103, 104, 105, 106);
    SectionRecord rec2 = new SectionRecord("0001", "00", year, "section2", 201, 202,
        203, 204, 205, 206);
    SectionRecord rec3 = new SectionRecord("000101", "0001", year, "section3", 301,
        302, 303, 304, 305, 306);
    SectionRecord rec4 = new SectionRecord("0002", "00", year, "section4", 401, 402,
        403, 404, 405, 406);

    return new SectionRecord[] { rec1, rec2, rec3, rec4 };
  }

  public void addSectionRecord(SectionRecord record) {

    PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
    Section e = new Section(record);
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
    Query query = pm.newQuery(Section.class);

    try {
      List<Section> results = (List<Section>) query.execute();
      pm.deletePersistentAll(results);
    } catch (Exception e) {
      System.out.println("Failed to remove all instances");
    } finally {
      pm.close();
    }
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
 
  @SuppressWarnings("unchecked")
  public SectionRecord[] getSectionsByYear(int year) {
    
    logger.info("Loading secions by year: " + year);  	
  	PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
    Query query = pm.newQuery(Section.class);
    query.setFilter("year == sectionYearParam");
    query.setOrdering("sectionCode desc");
    query.declareParameters("Integer sectionYearParam");

    List<Section> results = (List<Section>) query.execute(year);

    if (results == null || results.isEmpty()) {
      return new SectionRecord[] {};
    }

    SectionRecord[] sectionsArr = new SectionRecord[results.size()];
    System.out.println("Found " + results.size() + " records");
    for (int i = 0; i < results.size(); i++) {
      Section e = results.get(i);
      sectionsArr[i] = e.toSectionRecord();
    }

    return sectionsArr;
  }

  @Override
  public SectionRecord[] getSectionsByYearAndParent(int year, String parentCode) {
    
    logger.info("getSectionsByYearAndParent : " + year + "," + parentCode);
    
    PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
    Query query = pm.newQuery(Section.class);
    query.setFilter("year == sectionYearParam && parentCode == sectionParentCode");
    
    parentCode = parentCode == null ? "" : parentCode;
    
    query.setOrdering("sectionCode desc");
    query.declareParameters("Integer sectionYearParam, String sectionParentCode");

    List<Section> results = (List<Section>) query.execute(year, parentCode);

    if (results == null || results.isEmpty()) {
      logger.info("No results found for getSectionsByYearAndParent : " + year + "," + parentCode);
      return new SectionRecord[] {};
    }

    SectionRecord[] sectionsArr = new SectionRecord[results.size()];
    System.out.println("Found " + results.size() + " records");
    logger.info("Found " + results.size()  + " results found for getSectionsByYearAndParent : " + year + "," + parentCode);
    for (int i = 0; i < results.size(); i++) {
      Section e = results.get(i);
      System.out.println(e);      
      sectionsArr[i] = e.toSectionRecord();
    }

    return sectionsArr;
    
  }

  @Override
  public String[] getAvailableBudgetYears() {    
    
    System.out.println("Querying getAvailableBudgetYears");
    logger.info("Querying getAvailableBudgetYears");
    
    PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();    
    
    Query query = pm.newQuery(Section.class);
    query.setFilter("sectionCode == sectionCodeParam");   
    query.declareParameters("String sectionCodeParam");        

    List<Section> results = (List<Section>) query.execute("00");
    
    if (results == null || results.isEmpty()) {
      return new String[] {};
    }

    String[] yearsArr = new String[results.size()];
    Collections.sort(results, new Comparator<Section>() {
      @Override
      public int compare(Section lhs, Section rhs) {        
        return lhs.getYear().compareTo(rhs.getYear());
      }
    });
    
    System.out.println("Found " + results.size() + " years");
    for (int i = 0; i < results.size(); i++) {
      Section e = results.get(i);
      yearsArr[i] = ""+e.getYear();
    }

    return yearsArr;
  }

  @Override
  public SectionRecord[] getSectionByYearAndCode(int year, String sectionCode) {
    
    logger.info("getSectionByYearAndCode : " + year + "," + sectionCode);
    PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
    Query query = pm.newQuery(Section.class);
    query.setFilter("year == sectionYearParam && sectionCode == sectionCodeParam");
    
    query.setOrdering("sectionCode desc");
    query.declareParameters("Integer sectionYearParam, String sectionCodeParam");

    List<Section> results = (List<Section>) query.execute(year, sectionCode);

    if (results == null || results.isEmpty()) {
      logger.info("No results were found for getSectionByYearAndCode : " + year + "," + sectionCode);
      return new SectionRecord[] {};
    }

    SectionRecord[] sectionsArr = new SectionRecord[results.size()];
    System.out.println("Found " + results.size() + " records");
    logger.info(results.size() +  "Results were found for getSectionByYearAndCode : " + year + "," + sectionCode);
    for (int i = 0; i < results.size(); i++) {
      Section e = results.get(i);      
      sectionsArr[i] = e.toSectionRecord();
    }

    return sectionsArr;
  }

  @Override
  public SectionRecord[] getSectionsByNameAndCode(int year, String nameLike) {
    return new SectionRecord[]{};
  }
}
