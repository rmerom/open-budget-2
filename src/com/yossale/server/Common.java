package com.yossale.server;

import javax.jdo.PersistenceManager;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.UserServiceFactory;
import com.yossale.server.User;

public class Common {

	/**
	 * @return Null if the user is not logged-in.
	 *     If the user is logged-in return the User object from the database.
	 */
	public static User getLoggedInUserRecord() {
		com.google.appengine.api.users.User user = 
			  UserServiceFactory.getUserService().getCurrentUser();
	  if (user == null) {
	  	return null;
	  }
	  return getUserByEmail(user.getEmail());
	}
	
	/**
	 * Return a User object for the given email address.
	 * If the email address is not found in the DB the user is created.
	 * @param email email address of the logged-in user.
	 * @return User object from the database.
	 */
	public static User getUserByEmail(String email) {
		PersistenceManager pm = PMF.INSTANCE.getPersistenceManager();
		Key key = getUserKey(email);
		try {
			User user = null;
			try {
			  user = pm.getObjectById(User.class, key);
			} catch (Exception e) {
				user = null;
			}
			if (user == null) {
				user = new User();
			  user.setKey(key);
			  user.setEmail(email);
			  pm.makePersistent(user);
			}
			return user;
		} finally {
			pm.close();
		}
	}
	
	/**
	 * Return the key to retrieve the user row from the database.
	 * @param email email address of the logged-in user.
	 * @return
	 */
	public static Key getUserKey(String email) {
		return KeyFactory.createKey(User.class.getSimpleName(), email);
	}
}
