package com.yossale.server.admin;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.yossale.server.Common;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.DAO;
import com.yossale.server.data.User;
import com.yossale.server.util.ConvertUtil;

/**
 * Sets a bucket to public or not public.
 * Temporary servlet, until we have a UI for making buckets public.
 * 
 * Parameters: 
 *             type - "json" or "jsonp" (json if omitted).
 *             callback - for the case type equals "jsonp".
 *             bucketid - long
 *             ispublic - "true" or "false"
 *             
 * @author ronme
 */
public class SetIsPublicBucket extends HttpServlet {

	private Logger logger = Logger.getLogger(SetIsPublicBucket.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		User user = Common.getLoggedInUserRecord();
	  if (user == null) {
	  	// Should never happen, this directory is protected in web.xml .
	  	resp.sendError(400, "No user logged in");
	  	return;
	  }
	  
		if (req.getParameter("bucketid") == null || req.getParameter("ispublic") == null) {
			resp.sendError(400, "bad request");
			return;
		}
		long bucketId = Long.valueOf(req.getParameter("bucketid"));
		boolean isPublic = Boolean.valueOf(req.getParameter("ispublic"));

	  try {
	  	Objectify ofy = new DAO().ofy();
	  	Bucket bucket = 
	  			ofy.get(new Key<Bucket>(Bucket.class, bucketId));
	  	if (bucket == null) {
		  	resp.setContentType("text/html; charset=UTF-8");
	  		resp.getWriter().print("Bucket not found");
	  		return;
	  	}
  		bucket.setIsPublic(isPublic);
  		ofy.put(bucket);
	  	resp.setContentType("text/html; charset=UTF-8");
	  	JSONObject result = new JSONObject();
	  	result.put("bucket", ConvertUtil.bucketToJson(bucket));
	  	String outputText = ConvertUtil.jsonObjectAsformat(result, req);
	  	resp.getWriter().print("changed this bucket: <br/>" + outputText);
	  } catch (JSONException e) {
	  	resp.sendError(500, "JSONException: " + e.getMessage());
	  	return;
	  }
	}

	private static final long serialVersionUID = 1L;

}
