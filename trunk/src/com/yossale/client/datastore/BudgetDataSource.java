package com.yossale.client.datastore;

import java.util.Map;

import com.allen_sauer.gwt.log.client.Log;
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
    setDataFormat(DSDataFormat.CUSTOM);

    this.year = year;

    setTitleField("name");

    DataSourceTextField idField = new DataSourceTextField("id", "ID");
    DataSourceTextField sectionField = new DataSourceTextField("sectionCode",
        "Code");
    DataSourceTextField nameField = new DataSourceTextField("name", "Name");
    DataSourceTextField yearField = new DataSourceTextField("year", "Year");
    DataSourceTextField parentField = new DataSourceTextField("parentCode",
        "Parent");

    setFields(idField, sectionField, nameField, yearField, parentField);

  }

  @Override
  protected Object transformRequest(final DSRequest dsRequest) {

    DSOperationType opType = dsRequest.getOperationType();

    Log.info("Recieved DS request, operation type: " + opType);

    final DSResponse response = new DSResponse();
    final String requestId = dsRequest.getRequestId();

    switch (opType) {
    case FETCH:
      executeFetch(requestId, dsRequest, response);
      break;
    case REMOVE:
      break;
    default:
      Log.error("We recieved a request we don't know how to handle ");
    }

    Log.info("Fetch finished, returning from transform");
    return dsRequest.getData();
  }

  private void executeFetch(final String requestId, DSRequest dsRequest,
      final DSResponse response) {

    String parentId = null;
    String sectionCode = null;
    String sectionName = null;

    Log.info("Executing Fetch");

    Criteria criteria = dsRequest.getCriteria();
    if (criteria != null) {
      Map<?, ?> testValues = criteria.getValues();
      parentId = (String) testValues.get("parentId");
      sectionCode = (String) testValues.get("sectionCode");
      sectionName = (String) testValues.get("sectionName");
      Log.info("Found values: Parent " + parentId + "," + sectionCode + ","
          + sectionName);
      
    }

    if ((sectionCode == null && sectionName == null) || (parentId != null)) {
      /**
       * We got a regular fetch request - usually called when you open a parent
       * node and request it's Children
       */
      if (parentId != null && parentId.contains("_")) {
        parentId = parentId.split("_")[1];
      }
      executeFetchByParent(parentId, response, requestId);
    } else if (sectionCode != null) {
      /**
       * If we're being called via a filter, either the sectionCode or
       * sectionName should be legit.
       */
      filterByCode(sectionCode, response, requestId);

    } else if (sectionName != null) {

      Log.info("Should have filtered by name , isn't supported yet");

    }
    

  }

  private void filterByCode(final String sectionCode,
      final DSResponse response, final String requestId) {

    Log.info("Filtering tree by section code: " + sectionCode);
    sectionsService.getSectionByYearAndCode(year, sectionCode,
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
            response.setAttribute("reportCollisions", false);
          }

          @Override
          public void onFailure(Throwable caught) {
            Log
                .info("Failed to filter tree by section code: " + sectionCode);
            response.setStatus(RPCResponse.STATUS_FAILURE);
            processResponse(requestId, response);
          }
        });

  }

  private void executeFetchByParent(final String parentCode,
      final DSResponse response, final String requestId) {

    Log.info("Filtering tree by parentCode : " + parentCode);
    sectionsService.getSectionsByYearAndParent(year, parentCode,
        new AsyncCallback<SectionRecord[]>() {

          @Override
          public void onSuccess(SectionRecord[] result) {
            Record[] recs = new Record[result.length];
            int i = 0;
            for (SectionRecord s : result) {
              if (s == null) {
                continue;
              }
              recs[i++] = SectionRecord.getRecord(s);
            }
            response.setStatus(RPCResponse.STATUS_SUCCESS);
            response.setData(recs);
            Log.info("Recieved " + recs.length + " records in response to " 
                + year + "," + parentCode );
            processResponse(requestId, response);
          }

          @Override
          public void onFailure(Throwable caught) {
            Log.info("Failed filtering tree by parentCode : " + parentCode);
            response.setStatus(RPCResponse.STATUS_FAILURE);
            processResponse(requestId, response);
          }
        });

  }
}
