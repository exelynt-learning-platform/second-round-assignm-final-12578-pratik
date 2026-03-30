package com.multigenesystask.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.entity.User;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/users")
@AllArgsConstructor
public class UserController {
	

	private UserService userService;
	
	@GetMapping("/profile")
	public ResponseEntity<User> getUserProfileHandler(Principal principal) throws UserException{
		
		User user = userService.findUserByEmail(principal.getName());
		
		return new ResponseEntity<>(user, HttpStatus.OK);
	}
	

}
