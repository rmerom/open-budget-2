package com.yossale.server.actions;

import java.util.List;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.yossale.client.actions.BucketSectionService;
import com.yossale.client.data.SectionRecord;
import com.yossale.server.PMF;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.Section;

public class BucketSectionServiceImpl extends RemoteServiceServlet implements
    BucketSectionService {

	private static final long serialVersionUID = -4964841133537947859L;

	@Override
	public SectionRecord[] getSections(long bucketId) {
		PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
		Key key = KeyFactory.createKey(Bucket.class.getSimpleName(), bucketId);
		Bucket bucket = null;
		try {
		  bucket = pm.getObjectById(Bucket.class, key);
		} catch (Exception e) {
			bucket = null;
		}
		if (bucket == null) {
			return null;
		}
		
		List<Section> sections = bucket.getSections();
		SectionRecord[] output = new SectionRecord[sections.size()];
		int i = 0;
		for (Section section : sections) {
			output[i++] = section.toSectionRecord();
		}
		return output;
	}

}
