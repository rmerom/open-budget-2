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
import com.smartgwt.client.types.DSOperationType;
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
    
    setTitleField("name"); 
    
    DataSourceTextField idField = new DataSourceTextField("id", "ID");
    DataSourceTextField sectionField = new DataSourceTextField("sectionCode", "Code");
    DataSourceTextField nameField = new DataSourceTextField("name", "Name");
    DataSourceTextField yearField = new DataSourceTextField("year", "Year");
    DataSourceTextField parentField = new DataSourceTextField("parentCode",
        "Parent");

    setFields(idField, sectionField, nameField, yearField, parentField);

  }

  @Override
  protected Object transformRequest(final DSRequest dsRequest) {

    DSOperationType opType = dsRequest.getOperationType();
    
    System.out.println("Operation type:" + opType);
    final DSResponse response = new DSResponse();
    final String requestId = dsRequest.getRequestId();
    
    switch (opType) {
    case FETCH:
      executeFetch(requestId, dsRequest, response);
      break;
    default:
      System.out.println();
    }
    
    return dsRequest.getData();
  }

  private void executeFetch(final String requestId, DSRequest dsRequest,
      final DSResponse response) {
    
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
              recs[i++] = SectionRecord.getRecord(s);
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
    
  }
}
