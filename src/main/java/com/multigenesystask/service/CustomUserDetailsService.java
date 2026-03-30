package com.multigenesystask.service;




import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.multigenesystask.entity.User;
import com.multigenesystask.repository.UserRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
	
	
		
		private UserRepository userRepository;
	

		@Override
	    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

	        User user = userRepository.findByEmail(email);
	        
	        if (user == null) throw new UsernameNotFoundException("User not found: " + email);

	        return CustomUserDetails.build(user);
	      
	        
	 }

}
