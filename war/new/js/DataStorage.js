// Represents the Data Storage (מחסן הנתונים)
function DataStorage() {
  this.hello = 'hello';
}

DataStorage.retrieveData = function(expenseCodes, callback) {
  var query = {};
  query.code = {"$in": expenseCodes };
  
  aRequest(true);
  $.getJSON("http://api.yeda.us/data/gov/mof/budget/?callback=?", {
    "o" : "jsonp",
    "query" : JSON.stringify(query)
  }).success(function(data) {
    aRequest(false);
    callback(expenseCodes, data);
    callback(data);
  }).error(function() {
    alert('שגיאה בהתחברות למחסן הנתונים');
  });
}
