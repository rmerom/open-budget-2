package com.yossale.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BucketRecord implements Serializable {

	private String name;
	private String id;
	
	public BucketRecord(String id, String name) {
	  super();
	  this.name = name;
	  this.id = id;
  }

	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getId() {
		return id;
	}
	
	public void setId(String id) {
		this.id = id;
	}
}
