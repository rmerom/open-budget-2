package com.yossale.server;

import com.yossale.client.BucketRecord;
import com.yossale.client.BucketService;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class BucketServiceImpl extends RemoteServiceServlet implements
    BucketService {

	@Override
	public BucketRecord[] getBuckets(int id) {
		// TODO Auto-generated method stub
		return null;
	}
}
