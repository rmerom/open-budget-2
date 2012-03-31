  var years = [2001,2002];
  
  
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
		  "sSearch":       "חפש:",
		  "sUrl":          "",
		  "oPaginate": {
		      "sFirst":    "ראשון",
		      "sPrevious": "קודם",
		      "sNext":     "הבא",
		      "sLast":     "אחרון"
      }
    }
     oTable = $('#output_table').dataTable(tableDef);
   });
  
  // Register an event on the code input to resolve the given code.
  $('#add_expense').click(function() {
    var code = [""+$('#expense_code').val()];
    getExpense(code, years, getExpenseInfoCallback);
    $('#spinner').show();
  });
  
  function renderNumberWithCommas(o, val) {
    return numberWithCommas(val);
  }

	function numberWithCommas(x) {
		  return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
	}
  
  function addRowsToTable(data) {
    var sums = {};
    // Sum over the years.
    $.each(data, function(i, item) {
      var code = item.code;

      item.net_allocated = parseInt(item.net_allocated);
      item.net_revisited = parseInt(item.net_revisited);
      item.net_used = parseInt(item.net_used);
      item.gross_revised = parseInt(item.gross_revised);
      if (!sums[code]) {
        sums[code] = { 
          code: code, 
          title: item.title, 
          net_allocated: 0, 
          net_revised: 0, 
          net_used: 0,
          gross_revised: 0,
          gross_used: 0 };
      }
      var currentSums = sums[code];
      currentSums.net_allocated += item.net_allocated;
      currentSums.net_revised += item.net_revised;
      currentSums.net_used += item.net_used;
      currentSums.gross_revised += item.gross_revised;
      currentSums.gross_used += item.gross_used;
    });
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

  function getExpenseInfoCallback(codes, data) {
    $('#spinner').hide();
    var dataArr = [];
    if (!data.length) {
      alert('לא נמצאו נתונים עבור סעיפ/ים ' + codes);
    }
    addRowsToTable(data);
  }


  // Call this function to resolve the given codes and years and get their data from yeda.us
  // Example:
  // getExpense(["00","0020"], ["2001", "2002"], function (data) {}); 
  function getExpense(codes, years, callback) {
    var query = {};
    query.code = {"$in":codes};
    query.year = {"$in":years};
    $.getJSON("http://api.yeda.us/data/gov/mof/budget/?callback=?", 
      {
        "o" : "jsonp",
        "query" : JSON.stringify(query)
      }, function(data) {callback(codes, data);});
  }

//  $.getJSON("getBucket?j=?", {}, function(data) {
//    $.each(data, function(i, item){
//      $("<div>").html(item.num).appendTo("#buckets");
//      if ( i == 3 ) return false;
//    });
//  });

