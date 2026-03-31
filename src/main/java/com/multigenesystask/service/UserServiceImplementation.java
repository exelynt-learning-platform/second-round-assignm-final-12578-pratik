package com.multigenesystask.service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Service;

import com.multigenesystask.config.jwt.JwtUtils;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.repository.UserRepository;
import com.multigenesystask.requests.LoginRequest;
import com.multigenesystask.response.JwtAuthenticationResponse;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImplementation implements UserService {

	private UserRepository userRepository;
	private JwtUtils jwtUtils;
	private AuthenticationManager authenticationManager;

	@Override
	public User registerUser(User user) throws UserException  {
		return userRepository.save(user);
	}

	@Override

	public JwtAuthenticationResponse authenticateUser(LoginRequest loginRequest) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
		String jwt = jwtUtils.generateToken(userDetails);

		return new JwtAuthenticationResponse(jwt);
	}

	@Override
	public User findUserByEmail(String email) throws UserException {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UserException("User not found with email: " + email);
		}
		return user;
	}

	@Override
	public User findUserById(Long userId) throws UserException {
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			return user.get();
		}

		throw new UserException("user Not Found with id: " + userId);
	}

	@Override
	public User findUserProfileByJwt(String jwt) throws UserException {
		String email = jwtUtils.getUserNameFromJwtToken(jwt);

		User user = userRepository.findByEmail(email);
		if (user == null) {
			log.warn("User not found with email: {}", email);

			throw new UserException("User not found");
		}

		return user;
	}

}
