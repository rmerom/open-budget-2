package com.yossale.server;


import com.google.appengine.api.users.UserServiceFactory;
import com.googlecode.objectify.NotFoundException;
import com.googlecode.objectify.Objectify;
import com.yossale.server.data.DAO;
import com.yossale.server.data.User;

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
		Objectify otfy = new DAO().fact().begin();
		User user;
		try {
		  user = otfy.get(User.class, email);
		} catch (NotFoundException e) {
			user = null;
		}
		if (user == null) {
			user = new User().setEmail(email);
			otfy.put(user);
		}
		return user;
	}
}
