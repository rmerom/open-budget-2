package com.yossale.server;

import java.util.List;

import com.yossale.client.BucketRecord;
import com.yossale.client.BucketService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class BucketServiceImpl extends RemoteServiceServlet implements
    BucketService {

	@Override
	public BucketRecord[] getBuckets(int id) {
		
		User user = Common.getLoggedInUserRecord();
		List<Bucket> buckets = user.getBuckets();
		BucketRecord[] output = new BucketRecord[buckets.size()];
		int i = 0;
		for (Bucket bucket : buckets) {
			output[i++] = bucket.toBucketRecord();
		}
		return output;
	}
}
