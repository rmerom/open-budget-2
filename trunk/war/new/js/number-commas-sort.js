jQuery.fn.dataTableExt.oSort['number-commas-asc'] = function(a,b) {
    var x = a == "" ? 0 : a.replace( /\,/g, "" );
    var y = b == "" ? 0 : b.replace( /\,/g, "" );
     
    /* Parse and return */
    x = parseFloat( x );
    y = parseFloat( y );
    return x - y;
};
 
jQuery.fn.dataTableExt.oSort['number-commas-desc'] = function(a,b) {
    var x = a == "" ? 0 : a.replace( /\,/g, "" );
    var y = b == "" ? 0 : b.replace( /\,/g, "" );
     
    /* Parse and return */
    x = parseFloat( x );
    y = parseFloat( y );
    return y -x;
};

