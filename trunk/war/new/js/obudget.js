
// Of the type:
// { '0001': { 'weight': '1.0', 'years': { '2009': { title: '...', net_used: '...', ... } }
var expensesByCodeThenYear = {};
var userBuckets = {};
var reqCounter = 0;

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
  bucket.isPublic = $('#isPublicCheckbox').is(':checked');
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
  var bucketId = $('#bucketSelect').val();
  if (bucketId == '') {
    prettyAlert('אנא ביחרו איזו מחרוזת למחוק');
    return;
  }
  var bucketName = userBuckets[bucketId].name;
  prettyConfirm('האם אתם בטוחים שברצונכם למחוק את המחרוזת<br/>' + bucketName,
    function() {
      var request = { bucketId: bucketId };
      $.post('/api/deleteuserbucket', { 'request' : JSON.stringify(request) }).success(
        function() { 
          readBuckets(); 
          prettyAlert('המחיקה הצליחה'); 
        }).error(
        function() { prettyAlert('אירעה שגיאה'); } );
    });
}

function readBuckets() {
  aRequest(true);
  $.getJSON("/api/getuserbuckets?type=json", {}, function(data) {
    aRequest(false);
    $('#useremail').text(data.email);
    $('#user').show();
    var selects = $('#bucketSelect,#bucketSaveSelect');
    selects.empty();
    $('#bucketManagement,#bucketSaveSelect').toggle(data.buckets.length > 0);
    $.each(data.buckets, function(i, bucket) {
     userBuckets[bucket.id] = bucket;
	   selects.append(
	     $("<option></option>")
	       .attr("value", bucket.id)
	        .text(bucket.title));
    });
    $('#bucketSaveSelect').append($("<option></option>").attr('value','').text('מחרוזת חדשה...'));
    $('#newBucketName').toggle(data.buckets.length == 0);
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
  DataStorage.retrieveData(codes, function(data) {
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

function deleteExpense(code) {
  prettyConfirm('האם אתם בטוחים שברצונכם למחוק סעיף זה?', function() {
    delete expensesByCodeThenYear[code];
    refreshUI();
  });
}


