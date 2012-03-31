var expensesByCodeThenYear = {};
var userBuckets = {};

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
    { sTitle : "שימוש ברוטו", sType: "number-commas", fnRender: renderNumberWithCommas }];
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
	  getExpense(code, getExpenseInfoCallback);
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
	  updateTable();
	});
  $(".multiselect").multiselect();  // Enable multiple select.
	perpareUserBuckets();
}); // end of ready()
  
function renderNumberWithCommas(o, val) {
  return numberWithCommas(val);
}

function numberWithCommas(x) {
  return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
};

function saveBucket() {
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
    function() { alert('נשמר בהצלחה'); }).error(
    function() { alert('אירעה שגיאה'); } );
}

function perpareUserBuckets() {
  $.getJSON("/api/getuserbuckets?type=json", {}, function(data) {
    $('#useremail').text(data.email);
    $('#user').show();
    var selects = $('#bucketLoadSelect,#bucketSaveSelect');
    $.each(data.buckets, function(i, bucket) {
     userBuckets[bucket.id] = bucket;
	   selects.append(
	     $("<option></option>")
	       .attr("value", bucket.id)
	       .attr("selected", "selected")	
	        .text(bucket.name));
    });
    $('#bucketSaveSelect').append($("<option></option>").attr('value','').text('מחרוזת חדשה...'));
  });
 
  $('#loadBucketButton').click(function() {
    var id = $('#bucketLoadSelect').val();
    var bucket = userBuckets[id];
    $('#yearsSelect').val(bucket.years);
    $('#yearsSelect').multiselect('refresh');
	  getExpense(bucket.expenses, getExpenseInfoCallback);
  });

  $('#bucketSaveSelect').change(function() {
    $('#newBucket').toggle($('#bucketSaveSelect').val() == '');
  });

  $('#saveBucketButton').click(function() {saveBucket();});

}

function updateTable(data) {
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
    oTable.fnAddData(row);
  });
}

// Expenses is an array of items received from the "data store".
function addData(expenses) {
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
    alert('לא נמצאו נתונים עבור סעיפ/ים ' + codes);
  }
  addData(data);
  updateTable();
}


// Call this function to resolve the given codes and years and get their data from yeda.us
// Example:
// getExpense(["00","0020"], ["2001", "2002"], function (data) {}); 
function getExpense(codes, callback) {
  var query = {};
  query.code = {"$in":codes};
  $.getJSON("http://api.yeda.us/data/gov/mof/budget/?callback=?", 
    {
      "o" : "jsonp",
      "query" : JSON.stringify(query)
    }, function(data) {callback(codes, data);});
}

function nanZero(num) {
  return isNaN(num) ? 0 : num;
}

