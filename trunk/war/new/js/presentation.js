// Constants
var MIN_YEAR = 1992;

// Expenses for which we already warnted that their name changes over the years.
var expensesWarned = [];

$(document).ready(function() {
  $.getScript('js/number-commas-sort.js');  // Show commas thousands in numbers.

  var tableDef = { "bLengthChange": false, "bPaginate": false};
  tableDef.aaData = [];
  tableDef.aoColumns = [
    { sTitle : "סעיף", "sWidth": '40px' },
    { sTitle : "שם", "sWidth": '300px' },
    { sTitle : "שנה", "sWidth": '40px' },
    { sTitle : "אחוז מסעיף", sType: 'numeric' },
    { sTitle : "הקצאה נטו", sType: "number-commas", fnRender: renderNumberWithCommas },
    { sTitle : "הקצאה מעודכנת נטו", sType: "number-commas", fnRender: renderNumberWithCommas },
    { sTitle : "הקצאה מעודכנת ברוטו", sType: "number-commas", fnRender: renderNumberWithCommas },
    { sTitle : "שימוש נטו", sType: "number-commas", fnRender: renderNumberWithCommas },
    { sTitle : "שימוש ברוטו", sType: "number-commas", fnRender: renderNumberWithCommas },
    { sTitle : "מחיקה", sType: "text" } ];

  tableDef.oLanguage = {  // Make GUI hebrew
	  "sProcessing":   "מעבד...",
	  "sLengthMenu":   "הצג _MENU_ פריטים",
	  "sZeroRecords":  "לא נמצאו רשומות מתאימות",
	  "sInfo": "_START_ עד _END_ מתוך _TOTAL_ רשומות" ,
	  "sInfoEmpty":    "0 עד 0 מתוך 0 רשומות",
	  "sInfoFiltered": "(מסונן מסך _MAX_  רשומות)",
	  "sInfoPostFix":  "",
	  "sSearch":       "חפשו בטבלה:",
	  "sUrl":          "",
	  "oPaginate": {
	      "sFirst":    "ראשון",
	      "sPrevious": "קודם",
	      "sNext":     "הבא",
	      "sLast":     "אחרון"
    }
  }

   oTable = $('#output_table').dataTable(tableDef);
   oTable.rowGrouping({
       bHideGroupingColumn: false,
       bExpandableGrouping: true,
       iGroupingColumnIndex: 0,
       sGroupingColumnSortDirection: "asc",
      // iGroupingOrderByColumnIndex: 1
	});
	// Register an event on the add button retrieve the given code.
	$('#add_expense').click(function() {
	  var code = ""+$('#expense_code').val();
    $('#expense_code').val('');  // Clear textbox of old value.
    var expense = {
      code: code,
      weight: parseInt($('#expense_weight').val()) / 100.0
    };
	  getExpenses([expense], getExpenseInfoCallback);
	});
	var thisYear = new Date().getFullYear();
  var select = $('#yearsSelect');
	for (var i = MIN_YEAR; i <= thisYear; ++i) {
	   select.append(
	     $("<option></option>")
	       .attr("value", i)
	       .attr("selected", "selected")	
	        .text(i));
	}
	$('#yearsSelect').change(function() {
	  refreshUI();
	});
  $('#partialWeightCb').click(function() {
    $('#expense_weight').val(100);  // reset to 100%.
  });
  $(".multiselect").multiselect();  // Enable multiple select.
	prepareUserBuckets();
  initDialogBoxes();
  refreshUI();
}); // end of ready()

function renderNumberWithCommas(o, val) {
  return numberWithCommas(val);
}

function numberWithCommas(x) {
  return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

// Reads all of the user's buckets, and sets up the load button behavior.
function prepareUserBuckets() {
  readBuckets(); 
  $('#loadBucketButton').click(function() {
    var id = $('#bucketSaveSelect').val();
    var bucket = userBuckets[id];
    $('#bucketSaveSelect').val(id);
    $('#yearsSelect').val(bucket.years);
    $('#isPublicCheckbox').attr('checked', bucket.isPublic);
    $('#yearsSelect').multiselect('refresh');
    // Clear existing expenses.
    expensesByCodeThenYear = {};
    // And get the new ones.
	  getExpenses(bucket.expenses, getExpenseInfoCallback);
    refreshUI();
  });

  $('#bucketSaveSelect').change(function() {
    $('#newBucketName').toggle($('#bucketSaveSelect').val() == '');
  });

  $('#saveBucketButton').click(function() { saveBucket(); });
  $('#deleteBucketButton').click(function() { 
    deleteBucket();
  });
  // No need for the "search in this table" feature.
  $('#output_table_filter').hide();
}

function refreshUI() {
  // Refresh the years textbox.
  $('#yearsText').show().text(numberArrayToText($('#yearsSelect').val()));
  oTable.fnClearTable();
  
  // Refresh the table.
  // Sum over the years.
  var sums = {};
  for (code in expensesByCodeThenYear) {
    var expense = expensesByCodeThenYear[code];
    var differentTitles = false;
    for (year in expense.years) {
      if (-1 == $.inArray(year, $('#yearsSelect').val())) {
        continue; 
      }
      var item = expense.years[year];
	    if (!sums[code]) {
	      sums[code] = { 
	        code: item.code, 
          weight: expense.weight,
	        title: item.title, 
	        net_allocated: 0, 
	        net_revised: 0, 
	        net_used: 0,
	        gross_revised: 0,
	        gross_used: 0 };
	    } else {
        if (sums[code].title != item.title && $.inArray(code, expensesWarned) == -1) {
          expensesWarned.push(code);
          prettyAlert('שימו לב: סעיף ' + code + ' מכיל לאורך השנים תיאורים שונים.</br>' +
              'בשלב זה חוקר התקציב אינו מאפשר הפרדה אוטומטית בין שנים אלה. אנא בידקו בתקציב הפתוח אילו שנים רלוונטיות עבורכם.');
        }
      }
	    item.net_allocated = parseInt(item.net_allocated);
	    item.net_revisited = parseInt(item.net_revisited);
	    item.net_used = parseInt(item.net_used);
	    item.gross_revised = parseInt(item.gross_revised);
	    var currentSums = sums[code];
	    currentSums.net_allocated += nanZero(item.net_allocated);
	    currentSums.net_revised += nanZero(item.net_revised);
	    currentSums.net_used += nanZero(item.net_used);
	    currentSums.gross_revised += nanZero(item.gross_revised);
	    currentSums.gross_used += nanZero(item.gross_used);
	    
	    var row = [];
  	  row.push(item.code);
  	  row.push(item.title);
  	  row.push(year);
      row.push(Math.round(expense.weight * 100));
  	  row.push(parseInt(item.net_allocated * expense.weight));
  	  row.push(parseInt(item.net_revised * expense.weight));
  	  row.push(parseInt(item.gross_revised * expense.weight));
  	  row.push(parseInt(item.net_used * expense.weight));
  	  row.push(parseInt(item.gross_used * expense.weight));
      row.push('<a href="javascript:deleteExpense(\'' + item.code + '\')">מחקו</a>');
      oTable.fnAddData(row);
    
    }
  }
  // Now add the sums to the table.
  $.each(sums, function(i, item) {
    var groupTr = $("#group-id-output_table-"+item.code);
    var tdClass = "group group-item-expander";
    
    var row = [];
	  row.push(item.code);
	  $("#group-id-output_table-"+item.code+" td").attr("colspan",1);
    row.push(item.title);
	  groupTr.append($("<td>").addClass(tdClass).text(item.title));
	  groupTr.append($("<td>").addClass(tdClass).text(""));
    row.push(Math.round(item.weight * 100));
    groupTr.append($("<td>").addClass(tdClass).text(Math.round(item.weight * 100)));
    row.push(parseInt(item.net_allocated * item.weight));
	  groupTr.append($("<td>").addClass(tdClass).text(parseInt(item.net_allocated * item.weight)));
    row.push(parseInt(item.net_revised * item.weight));
	  groupTr.append($("<td>").addClass(tdClass).text(parseInt(item.net_revised * item.weight)));
    row.push(parseInt(item.gross_revised * item.weight));
	  groupTr.append($("<td>").addClass(tdClass).text(parseInt(item.gross_revised * item.weight)));
	  row.push(parseInt(item.net_used * item.weight));
	  groupTr.append($("<td>").addClass(tdClass).text(parseInt(item.net_used * item.weight)));
    row.push(parseInt(item.gross_used * item.weight));
	  groupTr.append($("<td>").addClass(tdClass).text(parseInt(item.gross_used * item.weight)));
    row.push('<a href="javascript:deleteExpense(\'' + item.code + '\')">מחקו</a>');
    groupTr.append($("<td>").addClass(tdClass).text(""));
    //oTable.fnAddData(row);
  });
}

function aRequest(isStart) {
  if (isStart) 
    ++reqCounter;
  else 
    --reqCounter;
  $('#spinner').toggle(reqCounter > 0);
}

function initDialogBoxes(){
  // Alert / message box.
  $("<div id='msgBox'></div>").dialog({
		autoOpen: false,
		title: '',
		bgiframe: true,
		modal: true,
		buttons: {
		    'סגור': function() {
		        $(this).dialog('close');
		    }
		}
  });

  // Confirm box.
  $("<div id='confirmBox'></div>").dialog({
		autoOpen: false,
		title: '',
		bgiframe: true,
		modal: true,
		buttons: {  // Filled dynamically
		}
  });
}

function prettyAlert(msg) {
	$("#msgBox").empty();
	$("#msgBox").append(msg);
	$("#msgBox").dialog('open');
}

function prettyConfirm(msg, callbackYes, callbackNo) {
  $('#confirmBox').empty();
  $('#confirmBox').append(msg);
  $('#confirmBox').dialog("option", "buttons", {
      'כן': function() {
        $(this).dialog('close');
        callbackYes();
      },
	    'לא': function() {
      $(this).dialog('close');
      if (callbackNo) {
        callbackNo(); 
      }
    }
  });
  $('#confirmBox').dialog('open');
}
