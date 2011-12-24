package com.yossale.client.datastore;

import java.util.ArrayList;
import java.util.List;

import com.smartgwt.client.data.DSRequest;
import com.smartgwt.client.data.DSResponse;
import com.smartgwt.client.data.DataSource;
import com.smartgwt.client.data.Record;
import com.smartgwt.client.data.fields.DataSourceIntegerField;
import com.smartgwt.client.data.fields.DataSourceTextField;
import com.yossale.shared.RpcExpenseObject;

public class LocalDataStore extends DataSource {

  private List<Record> recsList;
  
  public LocalDataStore(List<RpcExpenseObject> list) {
    this.recsList = tranformToRecord(list);
    
    setTitleField("title");
    setDescriptionField("title");

    DataSourceTextField topicName = new DataSourceTextField("title",
        "שם סעיף");

    DataSourceTextField codeAsText = new DataSourceTextField("code", "מספר");
    codeAsText.setRequired(true);

    DataSourceIntegerField numericCode = new DataSourceIntegerField(
        "numericCode", "קוד נומרי");
    codeAsText.setPrimaryKey(true);
    codeAsText.setRequired(true);

    DataSourceTextField parentCode = new DataSourceTextField("parentCode",
        "סעיף אב");
    parentCode.setRequired(true);
    parentCode.setForeignKey("numericCode");
    parentCode.setRootValue(100);

    DataSourceIntegerField grossAllocated = new DataSourceIntegerField(
        "gross_allocated", "הקצאה ברוטו");

    setFields(topicName, codeAsText, numericCode, parentCode, grossAllocated);

  }
  
  private List<Record> tranformToRecord(List<RpcExpenseObject> rpcList) {
      
    List<Record> recs = new ArrayList<Record>();
    for (RpcExpenseObject o : rpcList) {
      recs.add(new Record(o.convertToPropertiesMap()));
    }
    return recs;
  }

  @Override
  protected Object transformRequest(DSRequest dsRequest) {
    return "This is a request! " + dsRequest.toString();
  }

  @Override
  protected void transformResponse(DSResponse response, DSRequest request,
      Object data) {
    Record[] recArr = new Record[]{};
    response.setData(recsList.toArray(recArr));
    super.transformResponse(response, request, data);
    
  }

}
