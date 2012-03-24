package com.yossale.server.actions;

import java.util.ArrayList;
import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Objectify;
import com.yossale.client.actions.BucketSectionService;
import com.yossale.client.data.BucketRecord;
import com.yossale.client.data.SectionRecord;
import com.yossale.server.Common;
import com.yossale.server.PMF;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.DAO;
import com.yossale.server.data.Section;
import com.yossale.server.data.User;

public class BucketSectionServiceImpl extends RemoteServiceServlet implements
    BucketSectionService {

	private static final long serialVersionUID = -4964841133537947859L;

	@Override
	public SectionRecord[] getSections(long bucketId) {
		Objectify ofy = new DAO().ofy();
		// TODO(ronme): add error handling, check against curent user;
		Bucket bucket = ofy.get(Bucket.class, bucketId);
		
		BucketRecord bucketRecord = bucket.toBucketRecord();
		return bucketRecord.getSections().toArray(new SectionRecord[0]); 
	}
	
	public void updateBucketSections(long bucketId, SectionRecord[] sectionRecords) {
		Objectify ofy = new DAO().ofy();
		// TODO(ronme): add error handling, check against curent user;
		Bucket bucket = ofy.get(Bucket.class, bucketId);
		
		List<String> sectionStrings = new ArrayList<String>();
		for (SectionRecord sectionRecord : sectionRecords) {
			sectionStrings.add(sectionRecord.getId());
		}
		bucket.setSections(sectionStrings);
		ofy.put(bucket);
	}

}
