package com.yossale.client.actions;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import com.yossale.client.data.SectionRecord;

@RemoteServiceRelativePath("bucket_section")
public interface BucketSectionService extends RemoteService {
	SectionRecord[] getSections(long bucketId);
	void updateBucketSections(long bucketId, SectionRecord[] sectionRecords);
	
}
