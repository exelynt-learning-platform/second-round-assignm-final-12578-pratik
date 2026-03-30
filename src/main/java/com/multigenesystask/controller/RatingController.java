package com.multigenesystask.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.entity.Rating;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.requests.RatingRequest;
import com.multigenesystask.service.RatingService;
import com.multigenesystask.service.UserService;

@RestController
@RequestMapping("/api/ratings")
public class RatingController {
	
	@Autowired
	private UserService userService;
	@Autowired
	private RatingService ratingServices;

	
	@PostMapping("/create")
	public ResponseEntity<Rating> createRatingHandler(@RequestBody RatingRequest req,Principal principal) throws UserException, ProductException{
		User user=userService.findUserByEmail(principal.getName());
		Rating rating=ratingServices.createRating(req, user);
		return new ResponseEntity<>(rating,HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/product/{productId}")
	public ResponseEntity<List<Rating>> getProductsReviewHandler(@PathVariable Long productId){
	
		List<Rating> ratings=ratingServices.getProductsRating(productId);
		return new ResponseEntity<>(ratings,HttpStatus.OK);
	}
	
	
}
