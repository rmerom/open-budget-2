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
	  var code = [""+$('#expense_code').val()];
	  getExpenses(code, getExpenseInfoCallback);
	  $('#spinner').show();
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
  $(".multiselect").multiselect();  // Enable multiple select.
	prepareUserBuckets();
  initDialogBox();
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
  for (var expense in expensesByCodeThenYear) {
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
	  getExpenses(bucket.expenses, getExpenseInfoCallback);
    refreshUI();
  });

  $('#bucketSaveSelect').change(function() {
    $('#newBucket').toggle($('#bucketSaveSelect').val() == '');
  });

  $('#saveBucketButton').click(function() { saveBucket(); });
  $('#deleteBucketButton').click(function() { prettyAlert('עדיין לא עובד...');} );
}

function refreshUI() {
  // Refresh the years textbox.
  $('#yearsText').show().text(numberArrayToText($('#yearsSelect').val()));

  // Refresh the table.
  // Sum over the years.
  var sums = {};
  for (code in expensesByCodeThenYear) {
    var expenses = expensesByCodeThenYear[code];
    for (year in expenses) {
      if (-1 == $.inArray(year, $('#yearsSelect').val())) {
        continue; 
      }
      var item = expenses[year];
	    if (!sums[code]) {
	      sums[code] = { 
	        code: item.code, 
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
	  row.push(parseInt(item.net_allocated));
	  row.push(parseInt(item.net_revised));
	  row.push(parseInt(item.gross_revised));
	  row.push(parseInt(item.net_used));
	  row.push(parseInt(item.gross_used));
    row.push('<a href="javascript:deleteExpense(\'' + item.code + '\')">מחקו</a>');
    oTable.fnAddData(row);
  });
}

// Expenses is an array of items received from the "data store".
function setBucketExpenses(expenses) {
  expensesByCodeThenYear = {};
  $.each(expenses, function(i, item) {
    if (!expensesByCodeThenYear[item.code]) {
      expensesByCodeThenYear[item.code] = {};
    }
    expensesByCodeThenYear[item.code][item.year] = item;
  });
}

function getExpenseInfoCallback(codes, data) {
  $('#spinner').hide();
  var dataArr = [];
  if (!data.length) {
    prettyAlert('לא נמצאו נתונים עבור סעיפ/ים ' + codes);
  }
  setBucketExpenses(data);
  refreshUI();
}


// Call this function to resolve the given codes and years and get their data from yeda.us
// Example:
// getExpenses(["00","0020"], function (data) {}); 
function getExpenses(codes, callback) {
  if (codes.length == 0) {
    // No expenses were selected.
    return;
  }
  var query = {};
  query.code = {"$in":codes};
  aRequest(true);
  $.getJSON("http://api.yeda.us/data/gov/mof/budget/?callback=?", 
    {
      "o" : "jsonp",
      "query" : JSON.stringify(query)
    }, function(data) {
      aRequest(false);
      callback(codes, data);
    });
}

function nanZero(num) {
  return isNaN(num) ? 0 : num;
}

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
  delete expensesByCodeThenYear[code];
  refreshUI();
}

function initDialogBox(){
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
}

function prettyAlert(msg) {
	$("#msgBox").empty();
	$("#msgBox").append(msg);
	$("#msgBox").dialog('open');
}
