package com.multigenesystask.service;

import com.multigenesystask.entity.User;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.requests.RegisterRequest;

public interface UserService {

	public User findUserById(Long userId) throws UserException;
	
	public User findUserProfileByJwt(String jwt) throws UserException;
	
	public User findUserByEmail(String email);
	
	public User registerUser(User user);
}
