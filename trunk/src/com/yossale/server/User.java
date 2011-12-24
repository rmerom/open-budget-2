package com.yossale.server;

import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;

@PersistenceCapable
public class User {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private String email;
	
	@Persistent
	private List<Bucket> buckets;

	public User() {
	  super();
	}

	public Key getKey() {
	  return key;
	}

	public void setKey(Key key) {
	  this.key = key;
	}

	public String getEmail() {
	  return email;
	}

	public void setEmail(String email) {
	  this.email = email;
	}

	public List<Bucket> getBuckets() {
	  return buckets;
	}

	public void setBuckets(List<Bucket> buckets) {
	  this.buckets = buckets;
	}
	
	
}
