
function Expense(code, years) {
  this.code = code;
  this.years = years;
}

Expense.prototype.retrieveData = function() {
  DataStorage.retrieveData(codes, dataStorageCallback);
}

Expense.prototype.getCode = function() {
  return this.code;
}

Expense.prototype.dataStorageCallback_ = function(data) {
  
}
