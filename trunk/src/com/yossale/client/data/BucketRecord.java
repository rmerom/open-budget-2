package com.yossale.client.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class BucketRecord implements Serializable {

	private String name;
	private long id;
	private boolean isPublic;
	
	public boolean isPublic() {
		return isPublic;
	}

	public void setPublic(boolean isPublic) {
		this.isPublic = isPublic;
	}

	public BucketRecord() {		
		super();
	}
	
	public BucketRecord(long id, String name) {
	  super();
	  this.name = name;
	  this.id = id;
	  this.isPublic = false;
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
