package com.yossale.server.actions;

import java.util.List;

import com.yossale.client.actions.BucketService;
import com.yossale.client.data.BucketRecord;
import com.yossale.server.Common;
import com.yossale.server.data.Bucket;
import com.yossale.server.data.User;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

@SuppressWarnings("serial")
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
