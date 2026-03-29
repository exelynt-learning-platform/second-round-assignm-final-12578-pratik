package com.multigenesystask.controller;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.entity.User;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.repository.UserRepository;
import com.multigenesystask.requests.LoginRequest;
import com.multigenesystask.requests.RegisterRequest;
import com.multigenesystask.service.UserServiceImplementation;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
	private UserServiceImplementation userService;
	private UserRepository userRepository;

	@PostMapping("/public/register")
	public ResponseEntity<?> registerUser(@RequestBody RegisterRequest registerRequest) throws UserException {
		User user = new User();
		User isEmailExists = userRepository.findByEmail(registerRequest.getEmail());
		if(isEmailExists != null) {
			throw new UserException("Email is Already Used with Another account");
		}
		
		user.setFirstName(registerRequest.getFirstName());
		user.setLastName(registerRequest.getLastName());
		user.setEmail(registerRequest.getEmail());
		user.setPassword(registerRequest.getPassword());
		user.setRole("ROLE_USER");
		user.setMobile(registerRequest.getMobile());
		user.setCreatedAt(LocalDateTime.now());
		
		
		userService.registerUser(user);
		
		

		return ResponseEntity.status(HttpStatus.CREATED).body("User created Successfully");
	}

	@PostMapping("/public/login")
	public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(userService.authenticateUser(loginRequest));
	}

}
