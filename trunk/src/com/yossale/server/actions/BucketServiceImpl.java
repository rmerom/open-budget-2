package com.yossale.server.actions;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;
import javax.jdo.Query;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.yossale.client.actions.BucketService;
import com.yossale.client.data.BucketRecord;
import com.yossale.server.Common;
import com.yossale.server.PMF;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.User;

@SuppressWarnings("serial")
public class BucketServiceImpl extends RemoteServiceServlet implements
    BucketService {

	private Logger logger = Logger.getLogger(BucketServiceImpl.class.getName());
	
	@Override
	public BucketRecord[] getBucketsOfLoggedInUser() {
		User user = Common.getLoggedInUserRecord();
		List<Bucket> buckets = user.getBuckets();
		BucketRecord[] output = new BucketRecord[buckets.size()];
		int i = 0;
		for (Bucket bucket : buckets) {
			output[i++] = bucket.toBucketRecord();
		}
		return output;
	}
	
	@Override
	public BucketRecord addBucket(String name) {
		PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
		User user = Common.getLoggedInUserRecord();
		List<Bucket> buckets = user.getBuckets();
		Bucket b = new Bucket();
		try {
    	b.setName(name);
  		buckets.add(b);
  		user.setBuckets(buckets);
  		pm.makePersistent(user);
    } catch (Exception ex) {
    	logger.severe("Could not add bucket to the DB:" + ex.getMessage());
    } finally {
      pm.close();
    }
    return b.toBucketRecord();
	}

	@Override
	public BucketRecord[] getAllPublicBuckets() {
		PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
		Query query = pm.newQuery("select from Bucket where isPublic == true sort by name");
		Vector<BucketRecord> bucketRecords = new Vector<BucketRecord>(); 
		@SuppressWarnings("unchecked")
		List<Bucket> buckets = (List<Bucket>)query.execute();
		for (Bucket bucket : buckets) {
			bucketRecords.add(bucket.toBucketRecord());
			
		}
		return bucketRecords.toArray(new BucketRecord[0]);
	}

	@Override
	public void updateBucket(BucketRecord bucketRecord) {
		PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
		User user = Common.getLoggedInUserRecord();
		for (Bucket bucket : user.getBuckets()) {
			if (bucket.getName().equals(bucketRecord.getName())) {
				bucket.assignBucketRecord(bucketRecord);
				break;
			}
		}
		pm.makePersistent(user);
	}
}
