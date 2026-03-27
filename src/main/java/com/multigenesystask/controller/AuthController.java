package com.multigenesystask.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.dtos.LoginRequest;
import com.multigenesystask.dtos.RegisterRequest;
import com.multigenesystask.entity.User;
import com.multigenesystask.service.UserServiceImplementation;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {

	private UserServiceImplementation userService;

	@PostMapping("/public/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) {
		User user = new User();
		user.setFirstName(registerRequest.getFirstName());
		user.setLastName(registerRequest.getLastName());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(registerRequest.getPassword());
		user.setRole("ROLE_USER");
		user.setMobile(registerRequest.getMobile());
		userService.registerUser(user);

		return ResponseEntity.status(HttpStatus.CREATED).body("User created Successfully");
	}

	@PostMapping("/public/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(userService.authenticateUser(loginRequest));
	}

}
