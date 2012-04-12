package com.yossale.server.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;

import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.yossale.server.Common;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.DAO;
import com.yossale.server.data.User;
import com.yossale.server.util.ConvertUtil;

/**
 * Saves a bucket for the logged in user.
 * TODO(ronme): protect from XSRF - for example, by adding a secret token.
 * 
 * Parameters: 
 *   request = { 
 *     'request' :
 *       { "name": "bucket1", "years": [2000, 2001, 2002], "expenses": ["0001", "00231053"], "id": "54" }
 *     }
 * "id" is optional - if exists and such a bucket exists, it is overridden. Otherwise, a new one is created.
 * @author ronme
 */
public class SaveUserBucketServlet extends HttpServlet {

	private static final long serialVersionUID = 2802836422382125454L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		User user = Common.getLoggedInUserRecord();
	  if (user == null) {
	  	// Should never happen, this directory is protected in web.xml .
	  	resp.sendError(400, "No user logged in");
	  	return;
	  }
		if (req.getParameter("request") == null) {
			resp.sendError(400, "no request specified");
			return;
		}
		Bucket bucket;
		try {
			bucket = ConvertUtil.jsonToBucket(req, user);
		} catch (JSONException e) {
			resp.sendError(400, "Error in request. " + e.getMessage());
			return;
		}
		if (bucket == null) {
			resp.sendError(400, "Error in request..");
			return;
		}
		Objectify ofy = new DAO().ofy();
		if (bucket.getKey() != null) {
	  	QueryResultIterator<Bucket> bucketIterator = 
	  			ofy.query(Bucket.class).filter("owner", Key.create(User.class, user.getEmail()))
	  			    .filter("key", bucket.getKey()).fetch().iterator();
	  	if (!bucketIterator.hasNext()) {
	  		// No such bucket exists with this owner; create a new one by setting the id back to null.
	  		bucket.setKey(null);
	  	}
		}
		ofy.put(bucket);
	}
}
