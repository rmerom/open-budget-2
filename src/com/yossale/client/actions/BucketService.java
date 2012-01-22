package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.yossale.client.data.BucketRecord;

@RemoteServiceRelativePath("bucket")
public interface BucketService extends RemoteService {
	BucketRecord[] getBuckets(int id);	
	BucketRecord addBucket(String name);
	//BucketRecord updateBucket(BucketRecord bucket);
	
}
