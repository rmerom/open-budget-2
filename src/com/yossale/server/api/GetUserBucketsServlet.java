package com.yossale.server.api;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.yossale.server.Common;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.DAO;
import com.yossale.server.data.User;
import com.yossale.server.util.ConvertUtil;

/**
 * Returns the logged in user's name and her buckets buckets.
 * 
 * Parameters: type - "json" or "jsonp" (json if omitted).
 *             callback - for the case type equals "jsonp".
 * Returns: 
 *   Success:
 *     { "email":"rmerom@gmail.com",
 *       "buckets":[
 *       {
 *         "id":24001,
 *         "name":"somename",
 *         "years":[2010,2011,2012],
 *         "expenses":[{"years":[2010,2011,2012], "weight":1,"code":"00"}, {"years":[2010,2011,2012], "weight":0.5,"code":"0001"}]
 *       }]}
 *   
 * @author ronme
 */
public class GetUserBucketsServlet extends HttpServlet {

	private Logger logger = Logger.getLogger(GetUserBucketsServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		User user = Common.getLoggedInUserRecord();
	  if (user == null) {
	  	// Should never happen, this directory is protected in web.xml .
	  	resp.sendError(400, "No user logged in");
	  	return;
	  }

	  try {
	  	JSONArray buckets = new JSONArray();
	  	QueryResultIterator<Bucket> bucketIterator = 
	  			new DAO().ofy().query(Bucket.class).filter("owner", Key.create(User.class, user.getEmail())).fetch().iterator();
	  	while (bucketIterator.hasNext()) {
	  		Bucket bucket = bucketIterator.next();
	  		buckets.put(ConvertUtil.bucketToJson(bucket));;
	  	}
	  	resp.setContentType("text/html; charset=UTF-8");
	  	JSONObject result = new JSONObject();
	  	result.put("email", user.getEmail());
	  	result.put("buckets", buckets);
	  	String outputText = ConvertUtil.jsonObjectAsFormat(result, req);
	  	resp.getWriter().print(outputText);
	  } catch (JSONException e) {
	  	resp.sendError(500, "JSONException: " + e.getMessage());
	  	return;
	  }
	}

	private static final long serialVersionUID = 1L;

}
