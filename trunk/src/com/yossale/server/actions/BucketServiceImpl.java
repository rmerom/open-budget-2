package com.yossale.server.actions;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import com.google.appengine.api.datastore.QueryResultIterable;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.Objectify;
import com.googlecode.objectify.Query;
import com.yossale.client.actions.BucketService;
import com.yossale.client.data.BucketRecord;
import com.yossale.server.Common;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.DAO;
import com.yossale.server.data.User;

@SuppressWarnings("serial")
public class BucketServiceImpl extends RemoteServiceServlet implements
    BucketService {

	private Logger logger = Logger.getLogger(BucketServiceImpl.class.getName());
	
	@Override
	public BucketRecord[] getBucketsOfLoggedInUser() {
		User user = Common.getLoggedInUserRecord();
		if (user == null) {
			return new BucketRecord[0];
		}
		Vector<BucketRecord> output = new Vector<BucketRecord>();
		QueryResultIterator<Bucket> bucketIterator = 
				new DAO().ofy().query(Bucket.class).filter("owner", Key.create(User.class, user.getEmail())).fetch().iterator();
		while (bucketIterator.hasNext()) {
			Bucket bucket = bucketIterator.next();
			output.add(bucket.toBucketRecord());
		}
		logger.info("got " + output.size() + " buckets for user " + user.getEmail());
		return output.toArray(new BucketRecord[0]);
	}
	
	@Override
	public BucketRecord addBucket(String name) {
		User user = Common.getLoggedInUserRecord();
		Bucket bucket = new Bucket().setOwner(user.getEmail()).setName(name).setIsPublic(false);
		Objectify oty = new DAO().ofy();
		oty.put(bucket);
		return bucket.toBucketRecord();
	}

	@Override
	public BucketRecord[] getAllPublicBuckets() {
/*		PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
		Query query = pm.newQuery("select from Bucket where isPublic == true sort by name");
		Vector<BucketRecord> bucketRecords = new Vector<BucketRecord>(); 
		@SuppressWarnings("unchecked")
		List<Bucket> buckets = (List<Bucket>)query.execute();
		for (Bucket bucket : buckets) {
			bucketRecords.add(bucket.toBucketRecord());
			
		}
		return bucketRecords.toArray(new BucketRecord[0]);*/
		return null;
	}

	@Override
	public void updateBucket(BucketRecord bucketRecord) {
		User user = Common.getLoggedInUserRecord();
		Objectify ofy = new DAO().ofy();
		// TODO(ronme): add error handling, check against curent user;
		Bucket bucket = ofy.get(Bucket.class, bucketRecord.getId());
		bucket.assignBucketRecord(bucketRecord, user);
		ofy.put(bucket);
	}
}
