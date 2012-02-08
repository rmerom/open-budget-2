package com.yossale.client.datastore;

import java.util.Collection;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.data.Criteria;
import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.rpc.RPCResponse;
import com.smartgwt.client.types.DSDataFormat;
import com.smartgwt.client.types.DSProtocol;
import com.yossale.client.actions.SectionService;
import com.yossale.client.actions.SectionServiceAsync;
import com.yossale.client.data.SectionRecord;

public class BudgetDataSource extends DataSource {

  private final SectionServiceAsync sectionsService = GWT
      .create(SectionService.class);

  private final int year;

  public BudgetDataSource(int year) {
    
    super();
    setClientOnly(false);
    setDataProtocol(DSProtocol.CLIENTCUSTOM);
    setDataFormat (DSDataFormat.CUSTOM);
    
    this.year = year;
    
//    setID("BudgetDataStore_"+year);
    setTitleField("name"); 
    
    DataSourceTextField idField = new DataSourceTextField("id", "ID");
//    idField.setPrimaryKey(true);
//    idField.setRequired(true);

    DataSourceTextField nameField = new DataSourceTextField("name", "Name");
//    nameField.setRequired(true);

    DataSourceTextField yearField = new DataSourceTextField("year", "Year");
//    yearField.setRequired(true);

    DataSourceTextField parentField = new DataSourceTextField("parentId",
        "Parent");
    
//    parentField.setRequired(true);
//    parentField.setForeignKey("id");
//    parentField.setRootValue("");

    setFields(idField, nameField, yearField, parentField);

  }

  @Override
  protected Object transformRequest(final DSRequest dsRequest) {

    System.out.println("ReqType:" + dsRequest.getOperationType());
    String parentId = null;

    Criteria criteria = dsRequest.getCriteria();
    if (criteria != null) {
      Collection values = criteria.getValues().values();
      if (values.size() > 0) {
        for (Object searchTermObj : values) {
          if (searchTermObj instanceof String) {
            parentId = (String) searchTermObj;
            System.out.println("QuerySpecDataSource:: ParentId = " + parentId);
            break;
          }
        }
      }
    }

    final DSResponse response = new DSResponse();
    final String requestId = dsRequest.getRequestId();
    
    if (parentId != null && parentId.contains("_")) {
      parentId = parentId.split("_")[1];
    }

    sectionsService.getSectionsByYearAndParent(year, parentId,
        new AsyncCallback<SectionRecord[]>() {

          @Override
          public void onSuccess(SectionRecord[] result) {
            Record[] recs = new Record[result.length];
            int i = 0;
            for (SectionRecord s : result) {
              Record r = new Record();
              
              String id = year + "_" + s.getSectionCode();
              r.setAttribute("parentId", id.substring(0, id.length() - 2));
              r.setAttribute("id", id);
              r.setAttribute("#", s.getSectionCode());
              r.setAttribute("name", s.getName());
              r.setAttribute("year", s.getYear());
              recs[i++] = r;
            }
            response.setStatus(RPCResponse.STATUS_SUCCESS);
            response.setData(recs);
            processResponse(requestId, response);
          }

          @Override
          public void onFailure(Throwable caught) {
            response.setStatus(RPCResponse.STATUS_FAILURE);
            processResponse(requestId, response);
          }
        });

    return dsRequest.getData();
  }
}
