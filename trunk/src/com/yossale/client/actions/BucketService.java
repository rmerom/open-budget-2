package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.yossale.client.data.BucketRecord;
import com.yossale.server.data.Bucket;

@RemoteServiceRelativePath("bucket")
public interface BucketService extends RemoteService {
	BucketRecord[] getBucketsOfLoggedInUser();	
	BucketRecord addBucket(String name);
	BucketRecord[] getAllPublicBuckets();
	//BucketRecord updateBucket(BucketRecord bucket);
	
}
