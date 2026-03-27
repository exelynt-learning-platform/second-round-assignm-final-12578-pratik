package com.multigenesystask.service;

import com.multigenesystask.dtos.RegisterRequest;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.UserException;

public interface UserService {

	public User findUserById(Long userId) throws UserException;
	
	public User findUserProfileByJwt(String jwt) throws UserException;
	
	
	public void registerUser(RegisterRequest registerRequest);
}
