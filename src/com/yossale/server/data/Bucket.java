package com.yossale.server.data;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.annotations.IdGeneratorStrategy;
import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;

import com.google.appengine.api.datastore.Key;
import com.yossale.client.data.BucketRecord;
import com.yossale.client.data.SectionRecord;

@PersistenceCapable
public class Bucket {

	@PrimaryKey
	@Persistent(valueStrategy = IdGeneratorStrategy.IDENTITY)
	private Key key;
	
	@Persistent
	private String name;
	
	@Persistent
	private List<Section> sections;

	@Persistent
	private Boolean isPublic;

	public Bucket() {
		sections = new ArrayList<Section>();
	}
	
	public Bucket(BucketRecord bucketRecord) {
		assignBucketRecord(bucketRecord);
	}
	
	public Bucket assignBucketRecord(BucketRecord bucketRecord) {
		name = bucketRecord.getName();
		isPublic = bucketRecord.isPublic();
		sections = new ArrayList<Section>();
		for (SectionRecord sectionRecord : bucketRecord.getSections()) {
			sections.add(new Section(sectionRecord));
		}
		return this;
	}

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Section> getSections() {
		if (sections == null) {
			return new ArrayList<Section>();
		}
		return sections;
	}

	public void setSections(List<Section> sections) {
		this.sections = sections;
	}
	
	public BucketRecord toBucketRecord() {
		List<SectionRecord> sectionRecords = new ArrayList<SectionRecord>();
		for (Section section : sections) {
			sectionRecords.add(section.toSectionRecord());
		}
		return new BucketRecord(getKey().getId(), getName(), sectionRecords);
	}

	public Boolean getIsPublic() {
		return isPublic == null ? false : isPublic;
	}

	public void setIsPublic(Boolean isPublic) {
		this.isPublic = isPublic;
	}

}
