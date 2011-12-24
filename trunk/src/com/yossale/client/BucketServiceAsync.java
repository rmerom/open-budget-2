package com.yossale.client;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface BucketServiceAsync {
	void getBuckets(int id, AsyncCallback<BucketRecord[]> callback);
}
