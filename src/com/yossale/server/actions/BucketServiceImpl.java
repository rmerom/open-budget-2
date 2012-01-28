package com.yossale.server.actions;

import java.util.List;
import java.util.logging.Logger;

import javax.jdo.PersistenceManager;

import com.yossale.client.actions.BucketService;
import com.yossale.client.data.BucketRecord;
import com.yossale.server.Common;
import com.yossale.server.PMF;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

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
}
