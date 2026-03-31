package com.multigenesystask.service;

import com.multigenesystask.entity.User;

import com.multigenesystask.exception.UserException;
import com.multigenesystask.requests.LoginRequest;
import com.multigenesystask.response.JwtAuthenticationResponse;

public interface UserService {

	public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest);
	
	public User findUserById(Long userId) throws UserException;
	
	public User findUserProfileByJwt(String jwt) throws UserException;
	
	public User findUserByEmail(String email) throws UserException;
	
	public User registerUser(User user) throws UserException;
}
