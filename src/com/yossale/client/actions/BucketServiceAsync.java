package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.yossale.client.data.BucketRecord;

public interface BucketServiceAsync {
	void getBucketsOfLoggedInUser(AsyncCallback<BucketRecord[]> callback);
	void addBucket(String name, AsyncCallback<BucketRecord> callback);
}
