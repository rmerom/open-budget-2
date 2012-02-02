package com.yossale.client.datastore;

import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSProtocol;
import com.yossale.client.actions.SectionService;
import com.yossale.client.data.SectionRecord;


public class BudgetDataSource extends DataSource {
  
  private final SectionService sectionsService = GWT
  .create(SectionService.class);
  
  private final int year;
  
  public BudgetDataSource(int year) {
    this.year = year;
    setClientOnly(false);
    setDataProtocol(DSProtocol.CLIENTCUSTOM);
    setDataFormat (DSDataFormat.CUSTOM);
    
    DataSourceTextField nameField = new DataSourceTextField("Name", "Name");      
    DataSourceTextField yearField = new DataSourceTextField("Year", "Year");   
    DataSourceTextField parentField = new DataSourceTextField("Parent", "Parent");
    
    setFields(nameField, yearField, parentField);
  }

  @Override
  protected Object transformRequest(final DSRequest dsRequest) {

    System.out.println("ReqType:" + dsRequest.getOperationType());
    String parentId = null;
    
    Criteria criteria = dsRequest.getCriteria();
    if ( criteria != null ) {
      Collection values = criteria.getValues().values();
      if ( values.size() > 0 ) {          
        for ( Object searchTermObj : values ) {
          if ( searchTermObj instanceof String ) {
            parentId = (String)searchTermObj;
            System.out.println("QuerySpecDataSource:: ParentId = "+parentId);
            break;
          }
        }
      }
    }
    
    SectionRecord[] result = sectionsService.getSectionsByYearAndParent(year, parentId);
    
    DSResponse response = new DSResponse();
    System.out.println("Returned " + result.length + " expense record ");
    dsRequest.setAttribute("dsResult", result);
    
    Record[] recs = new Record[result.length];        
    int i = 0;
    for (SectionRecord s : result) {
      Record r = new Record();
      String id = year + "_" + s.getSectionCode();        
      r.setAttribute("parentId", id.substring(0, id.length() - 2));    
      r.setAttribute("ID", id);
      r.setAttribute("#", s.getSectionCode());
      r.setAttribute("Name", s.getName());    
      r.setAttribute("Year", s.getYear()); 
      recs[i++] = r;
    }
    
    response.setData(recs);
    processResponse(dsRequest.getRequestId(), response);

    return dsRequest;
  }

}
