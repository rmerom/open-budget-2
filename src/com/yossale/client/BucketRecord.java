package com.yossale.client;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BucketRecord implements Serializable {

	private String name;
	private long id;
	
	public BucketRecord(long id, String name) {
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
	
	public long getId() {
		return id;
	}
	
	public void setId(long id) {
		this.id = id;
	}
}
