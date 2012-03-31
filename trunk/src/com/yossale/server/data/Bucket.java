package com.yossale.server.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Id;

import org.json.JSONObject;

import com.googlecode.objectify.Key;
import com.yossale.client.data.BucketRecord;

public class Bucket {

	@Id
	private Long key;

	Key<User> owner;
	
	private String name;

	private List<String> expenseCodes;
	
	private List<Integer> years;

	private Boolean isPublic;

	public Bucket() {
		expenseCodes = new ArrayList<String>();
	}
	
	public Bucket(BucketRecord bucketRecord, User owner) {
		assignBucketRecord(bucketRecord, owner);
	}
	
	public Bucket assignBucketRecord(BucketRecord bucketRecord, User owner) {
		key = bucketRecord.getId();
		name = bucketRecord.getName();
		isPublic = bucketRecord.isPublic();
		years = bucketRecord.getYears();
		expenseCodes = new ArrayList<String>();
		for (String expenseRecord : bucketRecord.getExpenseCodes()) {
			expenseCodes.add(expenseRecord);
		}
		this.owner = Key.create(User.class, owner.getEmail());
		
		return this;
	}

	public Bucket setOwner(String ownerEmail) {
		this.owner = Key.create(User.class, ownerEmail);
		return this;
	}
	
	public Long getKey() {
		return key;
	}

	public String getName() {
		return name;
	}

	public Bucket setName(String name) {
		this.name = name;
		return this;
	}

	public List<String> getExpenses() {
		if (expenseCodes == null) {
			return new ArrayList<String>();
		}
		return expenseCodes;
	}

	public Bucket setExpenses(List<String> expenses) {
		this.expenseCodes = expenses;
		return this;
	}
	
	public BucketRecord toBucketRecord() {
		return new BucketRecord(getKey(), getName(), expenseCodes, years);
	}

	public Boolean getIsPublic() {
		return isPublic == null ? false : isPublic;
	}

	public Bucket setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
		return this;
	}

	/**
	 * Years which this bucket spans. All expenseCodes are relative to these years.
	 */
	public List<Integer> getYears() {
		return years;
	}

	public void setYears(List<Integer> years) {
		this.years = years;
	}
}
