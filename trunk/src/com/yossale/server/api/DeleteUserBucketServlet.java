package com.yossale.server.api;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.yossale.server.Common;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.DAO;
import com.yossale.server.data.User;

/**
 * Deletes a bucket of the logged in user.
 * TODO(ronme): protect from XSRF - for example, by adding a secret token.
 * 
 * Parameters: 
 *   request = { 
 *     'request' :
 *       { "bucketId": "59843543" }
 *   }
 * @author ronme
 */
public class DeleteUserBucketServlet extends HttpServlet {

	private static final long serialVersionUID = 4981679651383131178L;

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
		long bucketId;
		try {
			JSONObject request = new JSONObject(req.getParameter("request"));
			bucketId = Long.valueOf(request.getString("bucketId"));
		} catch (JSONException e) {
			resp.sendError(400, "invalid request");
			return;
		} catch (NumberFormatException e) {
			resp.sendError(400, "invalid request");
			return;
		}
		new DAO().ofy().delete(Bucket.class, bucketId);
	}
}
