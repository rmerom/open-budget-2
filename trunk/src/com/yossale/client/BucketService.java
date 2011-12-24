package com.yossale.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("bucket")
public interface BucketService extends RemoteService {
	BucketRecord[] getBuckets(int id);	
}
