package com.yossale.server.util;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yossale.server.data.Bucket;

/**
 * Converts various data objects to/from JSON.
 */
public class ConvertUtil {

	private static Logger logger = Logger.getLogger(ConvertUtil.class.getName());

	public static JSONObject bucketToJson(Bucket bucket) {
		JSONObject jsonBucket = new JSONObject();
		try {
			jsonBucket.put("name", bucket.getName());
			if (bucket.getIsPublic()) {
				jsonBucket.put("isPublic", bucket.getIsPublic());
			}
			JSONArray years = new JSONArray(bucket.getYears());
			jsonBucket.put("years", years);
			JSONArray expenses = new JSONArray(bucket.getExpenses());
			jsonBucket.put("expenses", expenses);
		} catch (JSONException e) {
			logger.severe("Cannot convert bucket to json: " + bucket.getKey() + ", name: " + bucket.getName());
		}
		
		return jsonBucket;
	}
	
	/**
	 * Returns object in the format asked for in the request.
	 * Request may have two paramteres, "format" and "type"
	 * 
	 * "format" can be either null, "json" or "jsonp".
	 * If it's null or any other string, we defualt to "json".
	 * 
	 * "callback" is the method to call when format is "jsonp". 
	 * Otherwise it's not used.   
	 */
	public static String jsonObjectAsformat(Object object, HttpServletRequest req) {
		String format = req.getParameter("format");
		if (format != null && format.equals("jsonp")) {
			String callback = req.getParameter("callback");
			if (callback == null) {
				callback = "callback";
			}
			StringBuilder result = new StringBuilder();
			result.append(callback).append("(").append(object.toString()).append(");");
			return result.toString();
		}
		// Default to json.
		return object.toString();
	}
}
