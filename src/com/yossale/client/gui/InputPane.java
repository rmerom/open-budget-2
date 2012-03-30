package com.yossale.client.gui;

import java.util.logging.Logger;

import com.allen_sauer.gwt.log.client.Log;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.smartgwt.client.types.MultipleAppearance;
import com.smartgwt.client.widgets.IButton;
import com.smartgwt.client.widgets.events.ClickEvent;
import com.smartgwt.client.widgets.events.ClickHandler;
import com.smartgwt.client.widgets.form.DynamicForm;
import com.smartgwt.client.widgets.form.fields.SelectItem;
import com.smartgwt.client.widgets.form.fields.TextItem;
import com.smartgwt.client.widgets.form.validator.RegExpValidator;
import com.smartgwt.client.widgets.layout.HLayout;
import com.smartgwt.client.widgets.layout.Layout;
import com.smartgwt.client.widgets.layout.VLayout;
import com.yossale.client.actions.ExpenseService;
import com.yossale.client.actions.ExpenseServiceAsync;

public class InputPane extends HLayout {

  VLayout vLayout = new VLayout();
  DynamicForm filtersPane = new DynamicForm();
  HLayout treesLayout = new HLayout();
  final BucketPane bucketPane;
  private final ExpenseServiceAsync expensesService = GWT
      .create(ExpenseService.class);

  public InputPane(BucketPane pane) {    

    this.bucketPane = pane;
    
    Log.info("Loading bucket pane");
    setBorder("5px solid blue");

    vLayout.addMember(buildFilterPane(), 0);
    addMember(vLayout);
  }

  private Layout buildFilterPane() {

    final DynamicForm form = new DynamicForm();
    form.setWidth(300);

    final SelectItem yearSelector = new SelectItem();
    yearSelector.setTitle("בחר שנים רלוונטיות");
    yearSelector.setMultiple(true);
    yearSelector.setMultipleAppearance(MultipleAppearance.GRID);

    expensesService.getAvailableBudgetYears(new AsyncCallback<String[]>() {

      @Override
      public void onFailure(Throwable caught) {
        System.out.println("Failed to retrieve years!");
      }

      @Override
      public void onSuccess(String[] result) {
        yearSelector.setValueMap(result);
      }
    });

    yearSelector.setTitle("Select years");
    
    RegExpValidator regExpValidator = new RegExpValidator();  
    regExpValidator.setExpression("^[0-9]{2,8}$");  
  
    final TextItem textItem = new TextItem();  
    textItem.setTitle("Section Id:");
    textItem.setHint("<nobr>הכנס מספר סעיף</nobr>");
    
    textItem.setValidators(regExpValidator);
    
    IButton addSectionButton = new IButton();  
    addSectionButton.setTitle("הוסף");  
    addSectionButton.addClickHandler(new ClickHandler() {  
        public void onClick(ClickEvent event) {  
            if(form.validate()) {
              Log.info("Whoo!");
              bucketPane.addSection(textItem.getValueAsString(),yearSelector.getValues());
            }
        }  
    });  
    
    form.setFields(yearSelector, textItem);
    
    VLayout vLayout = new VLayout();  
    vLayout.setMembersMargin(30);  
    vLayout.addMember(form);  
    vLayout.addMember(addSectionButton);
    
    vLayout.setSize("300", "300");
    
    return vLayout;

  }

}
