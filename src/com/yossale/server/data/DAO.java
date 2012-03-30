package com.yossale.server.data;

import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.util.DAOBase;

public class DAO extends DAOBase {
	static {
		ObjectifyService.register(Bucket.class);
		ObjectifyService.register(Expense.class);
		ObjectifyService.register(User.class);
	}
}
