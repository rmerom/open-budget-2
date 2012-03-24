package com.yossale.server.data;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Id;

public class User {
	@Id
	private String email;

	public User() {
	}

	public String getEmail() {
	  return email;
	}

	public User setEmail(String email) {
	  this.email = email;
	  return this;
	}
}
