package com.yossale.client.datastore;

import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.smartgwt.client.types.DSProtocol;
import com.yossale.client.data.ExpenseRecord;

public class TestDS extends DataSource {

  private List<Record> recsList;
  
  public TestDS(List<ExpenseRecord> list) {
    this.recsList = tranformToRecord(list);   
    
    setDataProtocol(DSProtocol.POSTXML);
    
    setTitleField("title");    

    DataSourceTextField topicName = new DataSourceTextField("title",
        "שם סעיף");

    DataSourceTextField codeAsText = new DataSourceTextField("code", "מספר");
    codeAsText.setRequired(true);    
    codeAsText.setPrimaryKey(true);
    codeAsText.setRequired(true);
    
    setFields(topicName, codeAsText);
  }
  
  private List<Record> tranformToRecord(List<ExpenseRecord> rpcList) {      
    List<Record> recs = new ArrayList<Record>();
    for (ExpenseRecord o : rpcList) {
      Record r = new Record();
      r.setAttribute("Name", o.getName());
      r.setAttribute("code", o.getExpenseCode());
      recs.add(r);
    }
    return recs;
  }

  @Override
  protected Object transformRequest(DSRequest dsRequest) {
    return super.transformRequest(dsRequest);
  }

  @Override
  protected void transformResponse(DSResponse response, DSRequest request,
      Object data) {
    System.out.println(request.toString());    
    Record[] recArr = new Record[]{};
    response.setData(recsList.toArray(recArr));
    super.transformResponse(response, request, data);    
  }

}
