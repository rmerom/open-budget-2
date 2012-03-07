package com.yossale.client.gui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.widgets.Canvas;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.TextAreaItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.yossale.client.actions.BucketService;
import com.yossale.client.actions.BucketServiceAsync;
import com.yossale.client.actions.SectionService;
import com.yossale.client.actions.SectionServiceAsync;
import com.yossale.client.data.BucketRecord;
import com.yossale.client.data.SectionRecord;

public class DBPanel extends HLayout {
  
  private final SectionServiceAsync sectionsService = GWT
  .create(SectionService.class);
  
  private final BucketServiceAsync bucketService = GWT
  .create(BucketService.class);

  public DBPanel() {

    VLayout messageLayout = new VLayout();
    messageLayout.setWidth(200);
    messageLayout.setHeight(300);
    messageLayout.setBorder("1px solid #6a6a6a");
    messageLayout.setLayoutMargin(5);

    final Canvas textCanvas = new Canvas();
    textCanvas.setPrefix("<b>Testing the DB:</b><BR>");
    textCanvas.setPadding(5);
    textCanvas.setHeight(1);

    final TextItem commitText = new TextItem();
    commitText.setTitle("Commit");
    commitText.setWidth("*");
    commitText.setDefaultValue("Commit");

    final TextItem retrieveText = new TextItem();
    retrieveText.setTitle("Retrieve");
    retrieveText.setWidth("*");
    retrieveText.setDefaultValue("Retrieve");

    final DynamicForm form = new DynamicForm();
    form.setNumCols(2);
    form.setHeight("*");
    form.setColWidths(60, "*");

    final TextAreaItem jsonText = new TextAreaItem();
    jsonText.setShowTitle(false);
    jsonText.setLength(5000);
    jsonText.setColSpan(2);
    jsonText.setWidth("*");
    jsonText.setHeight("*");

    form.setFields(commitText, retrieveText, jsonText);

    messageLayout.addMember(textCanvas);
    messageLayout.addMember(form);

    VLayout buttonLayout = new VLayout(10);

    IButton commitButton = new IButton("Commit");
    commitButton.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {

        final SectionRecord e = new SectionRecord("001122", "0011", 9999,
            "SomeName", 101, 102, 103, 104, 105, 106);

        sectionsService.addSectionRecord(e, new AsyncCallback<Void>() {

          @Override
          public void onSuccess(Void result) {
            commitText.setValue("Success!");
            textCanvas.setContents(textCanvas.getPrefix() + e.toString());
          }

          @Override
          public void onFailure(Throwable caught) {
            commitText.setValue("Failure :(");
          }

        });

      }
    });

    IButton retrieveButton = new IButton("Retrieve");
    retrieveButton.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {

        String content = jsonText.getValueAsString();
        if (null == content) {
          return;
        }

        sectionsService.getSectionsByYear(Integer.parseInt(content),
            new AsyncCallback<SectionRecord[]>() {

              @Override
              public void onFailure(Throwable caught) {
                retrieveText.setValue("Failure :(");
              }

              @Override
              public void onSuccess(SectionRecord[] result) {
                retrieveText.setValue("Success!");

                textCanvas.setContents(textCanvas.getPrefix() + " Retrieved "
                    + result.length + " records");
              }

            });

      }
    });

    IButton deleteAll = new IButton("Delete All");
    deleteAll.addClickHandler(new ClickHandler() {
      public void onClick(ClickEvent event) {

        sectionsService.removeAll(new AsyncCallback<Void>() {

          @Override
          public void onSuccess(Void result) {
            commitText.setValue("Deleted everything");
          }

          @Override
          public void onFailure(Throwable caught) {
            commitText.setValue("Failure to delete");
          }

        });

      }
    });

    IButton commitJson = new IButton("CommitJson");
    commitJson.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {

        String content = jsonText.getValueAsString();
        if (content == null) {
          return;
        }
        System.out.println("Updating data for year " + content);

        sectionsService.loadYearData(content, new AsyncCallback<Void>() {

          @Override
          public void onSuccess(Void result) {
            commitText.setValue("Success!");
            textCanvas.setContents(textCanvas.getPrefix());
          }

          @Override
          public void onFailure(Throwable caught) {
            commitText.setValue("Failure :(");
          }

        });

      }
    });

//    final IButton updateBudget = new IButton("Update budget tree");
//    updateBudget.addClickHandler(new ClickHandler() {
//
//      @Override
//      public void onClick(ClickEvent event) {
//        String content = jsonText.getValueAsString();
//        if (content == null) {
//          return;
//        }
//        updateTree(Integer.parseInt(content));
//      }
//    });

    IButton addBucket = new IButton("AddBucket");
    addBucket.addClickHandler(new ClickHandler() {

      @Override
      public void onClick(ClickEvent event) {

        String content = jsonText.getValueAsString();
        if (content == null) {
          return;
        }
        System.out.println("adding bucket " + content);

        bucketService.addBucket(content, new AsyncCallback<BucketRecord>() {

          @Override
          public void onSuccess(BucketRecord bucket) {
            commitText.setValue("Success!");
            textCanvas.setContents(textCanvas.getPrefix());
          }

          @Override
          public void onFailure(Throwable caught) {
            commitText.setValue("Failure :(");
          }
        });
      }
    });
    
    IButton getFromSql = new IButton("SQL Query");
//    getFromSql.addClickHandler(new ClickHandler() {
//      
//      @Override
//      public void onClick(ClickEvent event) {
//        try {
//          DriverManager.registerDriver(new AppEngineDriver());
//          Connection c = DriverManager.getConnection("jdbc:google:rdbms://open-budget-1:openbudget-1/obudget_dev");
//          
//          String statement ="SELECT obsc_name FROM obsc_section";
//          PreparedStatement stmt = c.prepareStatement(statement);
//          ResultSet res = stmt.executeQuery();
//          System.out.println("Res: " + res.getFetchSize());
//        } catch (SQLException e) {
//          System.out.println("Failed to execute select");
//          e.printStackTrace();
//        }
//        
//       }        
//    });

    buttonLayout.addMember(commitButton);
    buttonLayout.addMember(retrieveButton);
    buttonLayout.addMember(deleteAll);
    buttonLayout.addMember(commitJson);
//    buttonLayout.addMember(updateBudget);
    buttonLayout.addMember(addBucket);

    addMember(messageLayout);
    addMember(buttonLayout);

  }

}
