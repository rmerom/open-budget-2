package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.AsyncCallback;
import com.yossale.client.data.SectionRecord;

public interface BucketSectionServiceAsync {
	void getSections(long bucketId, AsyncCallback<SectionRecord[]> callback);
}
