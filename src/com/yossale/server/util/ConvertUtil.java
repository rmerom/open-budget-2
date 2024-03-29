package com.yossale.server.util;

import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.yossale.server.data.Bucket;
import com.yossale.server.data.Bucket.Expense;
import com.yossale.server.data.User;

/**
 * Converts various data objects to/from JSON.
 */
public class ConvertUtil {

	private static Logger logger = Logger.getLogger(ConvertUtil.class.getName());

	/**
	 * NOTE: The old API returned years for the whole bucket, and this is what the client currently uses.
	 * The new API supports different years for different expenses, and this is what the external clients
	 * use for GetAllPublickBuckets.
	 * For the time being, we generate both here. 
	 * TODO(rmerom): if required, change the datastore objects to support per-expense years, and drop
	 * the per-bucket years (after updating the client).
	 * and deprecate the per-bucket years.  
	 */
	public static JSONObject bucketToJson(Bucket bucket) {
		JSONObject jsonBucket = new JSONObject();
		try {
			jsonBucket.put("id", bucket.getKey());
			jsonBucket.put("title", bucket.getName());
			if (bucket.getIsPublic()) {
				jsonBucket.put("isPublic", bucket.getIsPublic());
			}
			jsonBucket.put("years", bucket.getYears());
			
			JSONArray expenses = new JSONArray();
			for (Expense expense : bucket.getExpenses()) {
				JSONObject jsonExpense = new JSONObject();
				jsonExpense.put("code", expense.getExpenseCode());
				jsonExpense.put("weight", expense.getRatio());
				JSONArray yearsAndWeights = new JSONArray();
				for (int year : bucket.getYears()) {
					JSONObject yearAndWeight = new JSONObject();
					yearAndWeight.put("year", year);
					yearAndWeight.put("weight", expense.getRatio());
					yearsAndWeights.put(yearAndWeight);
				}
				jsonExpense.put("years", yearsAndWeights);
				expenses.put(jsonExpense);
			}
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
	public static String jsonObjectAsFormat(Object object, HttpServletRequest req) {
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

	public static Bucket jsonToBucket(HttpServletRequest req, User user) throws JSONException {
		Bucket bucket = new Bucket();
		if (req.getParameter("request") == null) {
			return null;
		}
		JSONObject reqObj = new JSONObject(req.getParameter("request"));
		JSONObject jsonObj;
		if (!reqObj.has("bucket")) {
			logger.warning("could not find 'bucket' in request");
			return null;
		}
		bucket.setOwner(user.getEmail());
		jsonObj = reqObj.getJSONObject("bucket");
		if (jsonObj.has("id")) {
			bucket.setKey(jsonObj.getLong("id"));
		}
		bucket.setName(jsonObj.getString("title"));
		bucket.setIsPublic(jsonObj.optBoolean("isPublic"));
		JSONArray jsonYears = jsonObj.getJSONArray("years");
		for (int i = 0; i < jsonYears.length(); ++i) {
			bucket.getYears().add(jsonYears.getInt(i));
		}
		JSONArray jsonExpenses = jsonObj.getJSONArray("expenses");
		for (int i = 0; i < jsonExpenses.length(); ++i) {
			JSONObject jsonExpense = jsonExpenses.getJSONObject(i);
			double weight = jsonExpense.optDouble("weight", 1.0);
			if (weight > 1.0 || weight < 0.0) {
				weight = 1.0;
			}
			bucket.getExpenses().add(new Expense(jsonExpense.getString("code"), weight));
		}
		return bucket;
	}
}
