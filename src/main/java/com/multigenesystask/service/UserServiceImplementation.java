package com.multigenesystask.service;


import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.multigenesystask.config.jwt.JwtAuthenticationResponse;
import com.multigenesystask.config.jwt.JwtUtils;
import com.multigenesystask.dtos.LoginRequest;
import com.multigenesystask.entity.User;
import com.multigenesystask.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserServiceImplementation {

	 private PasswordEncoder passwordEncoder;

	    private UserRepository userRepository;
	    private JwtUtils jwtUtils;
	    private AuthenticationManager authenticationManager;

	    public User registerUser(User user){
	        user.setPassword(passwordEncoder.encode(user.getPassword()));
	        return userRepository.save(user);
	    }

	    public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest){
	    	
	      Authentication authentication =  authenticationManager.authenticate(
	                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(),
	                        loginRequest.getPassword()));
	      
	      
	        SecurityContextHolder.getContext().setAuthentication(authentication);

	        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
	        String jwt = jwtUtils.generateToken(userDetails);

	        return new JwtAuthenticationResponse(jwt);
	    }

	    public User findUserByEmail(String email){
	        return userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found with mail: " + email));
	    }
}
