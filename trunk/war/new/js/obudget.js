// Of the type:
// { '0001': { 'weight': '1.0', 'years': { '2009': { title: '...', net_used: '...', ... } }
var expensesByCodeThenYear = {};
var userBuckets = {};
var reqCounter = 0;

$(document).ready(function() {
  $.getScript('js/number-commas-sort.js');  // Show commas thousands in numbers.
  var tableDef = {};
  tableDef.aaData = [];
  tableDef.aoColumns = [
    { sTitle : "סעיף", "sWidth": '40px' },
    { sTitle : "שם", "sWidth": '300px' },
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
	// Register an event on the add button retrieve the given code.
	$('#add_expense').click(function() {
	  var code = ""+$('#expense_code').val();
    var expense = {
      code: code,
      weight: parseInt($('#expense_weight').val()) / 100.0
    };
	  getExpenses([expense], getExpenseInfoCallback);
	});
	var thisYear = new Date().getFullYear();
  var select = $('#yearsSelect');
	for (var i = 1996; i <= thisYear; ++i) {
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
    $('#partialRow').toggle($('#partialWeightCb').val());
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

function saveBucket() {
  if (Object.keys(expensesByCodeThenYear).length == 0) {
    prettyAlert('אנא הוסיפו תחילה סעיפים לתוך המחרוזת');
    return;
  }
  if ($('#bucketSaveSelect').val() == '' && $('#newBucketName').val() == '') {
    prettyAlert('אנא העניקו שם למחרוזת החדשה');
    return;
  }
  if ($('#bucketSaveSelect').val() != '') {
    if (!confirm('בטוחים שברצונכם להחליף את המחרוזת?')) {
      return;
    }
  }
  var bucket = {};
  if ($('#bucketSaveSelect').val() != '') {
    bucket.id = $('#bucketSaveSelect').val(); 
  }
  var val = $('#bucketSaveSelect').val();
  bucket.title = val == '' ? $('#newBucketName').val() : $('#bucketSaveSelect option[value=\'' + val +'\']').text();
  bucket.years = $('#yearsSelect').val();
  bucket.expenses = [];
  for (var code in expensesByCodeThenYear) {
    var expense = { code: code, weight: expensesByCodeThenYear[code].weight };
 	  bucket.expenses.push(expense);
  }
  request = {};
  request.bucket = bucket;
  $.post('/api/saveuserbucket', { 'request' : JSON.stringify(request) }).success(
    function() { 
      readBuckets(); 
      prettyAlert('נשמר בהצלחה'); 
    }).error(
    function() { prettyAlert('אירעה שגיאה'); } );
}

function deleteBucket() {
  if ($('#bucketDeleteSelect').val() == '') {
    prettyAlert('אנא ביחרו איזו מחרוזת למחוק');
    return;
  }
  var bucketId = $('#bucketDeleteSelect').val();
  var request = { bucketId: bucketId };
  $.post('/api/deleteuserbucket', { 'request' : JSON.stringify(request) }).success(
    function() { 
      readBuckets(); 
      prettyAlert('המחיקה הצליחה'); 
    }).error(
    function() { prettyAlert('אירעה שגיאה'); } );

}

function readBuckets() {
  aRequest(true);
  $.getJSON("/api/getuserbuckets?type=json", {}, function(data) {
    aRequest(false);
    $('#useremail').text(data.email);
    $('#user').show();
    var selects = $('#bucketLoadSelect,#bucketSaveSelect,#bucketDeleteSelect');
    selects.empty();
    $.each(data.buckets, function(i, bucket) {
     userBuckets[bucket.id] = bucket;
	   selects.append(
	     $("<option></option>")
	       .attr("value", bucket.id)
	        .text(bucket.name));
    });
    $('#bucketSaveSelect').append($("<option></option>").attr('value','').text('מחרוזת חדשה...'));
    $('#newBucketName').toggle(data.buckets.length == 0);
  });
}

function prepareUserBuckets() {
  readBuckets(); 
  $('#loadBucketButton').click(function() {
    var id = $('#bucketLoadSelect').val();
    var bucket = userBuckets[id];
    $('#bucketSaveSelect').val(id);
    $('#yearsSelect').val(bucket.years);
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
}

function refreshUI() {
  // Refresh the years textbox.
  $('#yearsText').show().text(numberArrayToText($('#yearsSelect').val()));

  // Refresh the table.
  // Sum over the years.
  var sums = {};
  for (code in expensesByCodeThenYear) {
    var expense = expensesByCodeThenYear[code];
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
    }
  }
  oTable.fnClearTable();
  // Now add the sums to the table.
  $.each(sums, function(i, item) {
	  var row = [];
	  row.push(item.code);
	  row.push(item.title);
    row.push(Math.round(item.weight * 100));
	  row.push(parseInt(item.net_allocated * item.weight));
	  row.push(parseInt(item.net_revised * item.weight));
	  row.push(parseInt(item.gross_revised * item.weight));
	  row.push(parseInt(item.net_used * item.weight));
	  row.push(parseInt(item.gross_used * item.weight));
    row.push('<a href="javascript:deleteExpense(\'' + item.code + '\')">מחקו</a>');
    oTable.fnAddData(row);
  });
}

// Expenses is an array of items received from the "data store".
function addBucketExpenses(expenses, data) {
  // Translate expenses into a map.
  var expenseMap = {};
  $.each(expenses, function(i, expense) { expenseMap[expense.code] = expense.weight });

  // Add actual expenses.
  $.each(data, function(i, item) {
    if (!expensesByCodeThenYear[item.code]) {
      expensesByCodeThenYear[item.code] = {};
      expensesByCodeThenYear[item.code].years = {};
    }
    expensesByCodeThenYear[item.code].weight = expenseMap[item.code]
    expensesByCodeThenYear[item.code].years[item.year] = item;
  });
}

function getExpenseInfoCallback(expenses, data) {
  $('#spinner').hide();
  if (!data.length) {
    prettyAlert('לא נמצאו נתונים עבור סעיפ/ים ' + $.map(expenses, function(expense) { return expense.code; }));
  }
  addBucketExpenses(expenses, data);
  refreshUI();
}


// Call this function to resolve the given codes and years and get their data from yeda.us
// Example:
// getExpenses([{code: "00", weight: 0.5},{code:"0020", weight: 0.7}], function (data) {}); 
function getExpenses(expenses, callback) {
  if (expenses.length == 0) {
    // No expenses were selected.
    return;
  }
  var query = {};
  var codes = $.map(expenses, function(expense) { return expense.code; });
  query.code = {"$in": codes};
  aRequest(true);
  $.getJSON("http://api.yeda.us/data/gov/mof/budget/?callback=?", 
    {
      "o" : "jsonp",
      "query" : JSON.stringify(query)
    }, function(data) {
      aRequest(false);
      callback(expenses, data);
    });
}

function nanZero(num) {
  return isNaN(num) ? 0 : num;
}

// Turns input such as [2000, 2001, 2003, 2005, 2006, 2007] into "2000-2001, 2005-2007".
function numberArrayToText(array) {
  if (array.length == 0) {
    return "";
  }
  if (array.length == 1) {
    return "" + array[0];
  }
  var prev = 0;
  result = "";
  var i = 1; 
  var currentStart = 0;
  while (i < array.length) {
    if (parseInt(array[i]) != parseInt(array[i-1]) + 1) {
      var currentEnd = i-1;
      if (currentStart == currentEnd) {
        result += ', ' + array[currentStart];
      } else {
        result += ', ' + array[currentStart] + '-' + array[currentEnd];
      }
      currentStart = i;
    }
    i++;
  }
  if (currentStart != array.length - 1) {
    result += ', ' + array[currentStart] + '-' + array[array.length - 1];
  }
  return result.substring(2);
}

function aRequest(isStart) {
  if (isStart) 
    ++reqCounter;
  else 
    --reqCounter;
  $('#spinner').toggle(reqCounter > 0);
}

function deleteExpense(code) {
  prettyConfirm('האם אתם בטוחים שברצונכם למחוק סעיף זה?', function() {
    delete expensesByCodeThenYear[code];
    refreshUI();
  });
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
