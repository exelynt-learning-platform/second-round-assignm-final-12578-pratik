package com.multigenesystask.controller;



import org.springframework.http.HttpStatus;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.entity.User;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.repository.UserRepository;
import com.multigenesystask.requests.LoginRequest;
import com.multigenesystask.requests.RegisterRequest;
import com.multigenesystask.response.JwtAuthenticationResponse;
import com.multigenesystask.service.CartService;
import com.multigenesystask.service.UserService;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {
	private UserService userService;
	private UserRepository userRepository;

	private PasswordEncoder passwordEncoder;

	private CartService cartService;
	
	@PostMapping("/public/register")
	public ResponseEntity<String> registerUser(@Valid @RequestBody RegisterRequest registerRequest)
			throws UserException {

		if (userRepository.findByEmail(registerRequest.getEmail()) != null) {
            throw new UserException("Email is Already Used with Another account");
        }

		User user = registerRequest.convertToUser(passwordEncoder);

		User savedUser = userService.registerUser(user);
		
		cartService.createCart(savedUser);
		
		return ResponseEntity.status(HttpStatus.CREATED).body("User created Successfully");
	}

	@PostMapping("/public/login")
	public ResponseEntity<JwtAuthenticationResponse> loginUser(@RequestBody LoginRequest loginRequest) {
		return ResponseEntity.ok(userService.authenticateUser(loginRequest));
	}

}
