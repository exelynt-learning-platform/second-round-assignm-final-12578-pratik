package com.multigenesystask.service;

import java.util.ArrayList;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.multigenesystask.entity.User;
import com.multigenesystask.repository.UserRepository;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {
	
	
		private UserRepository userRepository;
	

		@Override
	    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

	        User user = userRepository.findByEmail(email)
	                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

	        return CustomUserDetails.build(user);
	      
	        
	 }

}
