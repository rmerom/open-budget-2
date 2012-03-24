package com.yossale.server.data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.persistence.Id;

import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.yossale.client.data.BucketRecord;
import com.yossale.client.data.SectionRecord;

public class Bucket {

	@Id
	private Long key;

	Key<User> owner;
	
	private String name;

	private List<String> sections;

	private Boolean isPublic;

	public Bucket() {
		sections = new ArrayList<String>();
	}
	
	public Bucket(BucketRecord bucketRecord, User owner) {
		assignBucketRecord(bucketRecord, owner);
	}
	
	public Bucket assignBucketRecord(BucketRecord bucketRecord, User owner) {
		name = bucketRecord.getName();
		isPublic = bucketRecord.isPublic();
		sections = new ArrayList<String>();
		for (SectionRecord sectionRecord : bucketRecord.getSections()) {
			sections.add(new Section(sectionRecord).getKey());
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

	public List<String> getSections() {
		if (sections == null) {
			return new ArrayList<String>();
		}
		return sections;
	}

	public Bucket setSections(List<String> sections) {
		this.sections = sections;
		return this;
	}
	
	public BucketRecord toBucketRecord() {
		Objectify otfy = new DAO().fact().begin();
		Map<String, Section> loadedSections = otfy.get(Section.class, sections);
		List<SectionRecord> sectionRecords = new ArrayList<SectionRecord>();
		for (Section loadedSection : loadedSections.values()) {
			sectionRecords.add(loadedSection.toSectionRecord());
		}
		return new BucketRecord(getKey(), getName(), sectionRecords);
	}

	public Boolean getIsPublic() {
		return isPublic == null ? false : isPublic;
	}

	public Bucket setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
		return this;
	}

}
