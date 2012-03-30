package com.yossale.server.actions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.yossale.server.data.Item;

public class GetBucket extends HttpServlet {
	
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
  		throws IOException {
		resp.setContentType("application/json");
		resp.getWriter().print(req.getParameter("j")+"(");
    List<JSONObject> list = new ArrayList<JSONObject>();
    for (int i = 0; i < 10; i++) {
    	list.add(new JSONObject(new Item("00"+i, 1/(i+1))));
    }
    //Create a JSONArray based from the list of JSONObejcts  
    JSONArray jsonArray = new JSONArray(list);  
    //Then output the JSON string to the servlet response  
    resp.getWriter().print(jsonArray.toString() +");"); 
    
  }
}