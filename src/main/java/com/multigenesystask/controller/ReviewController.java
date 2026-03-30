package com.multigenesystask.controller;

import java.security.Principal;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.entity.Review;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.requests.ReviewRequest;
import com.multigenesystask.service.ReviewService;
import com.multigenesystask.service.UserService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/reviews")
@AllArgsConstructor
@Slf4j
public class ReviewController {


	private ReviewService reviewService;

	private UserService userService;

	@PostMapping("/create")
	public ResponseEntity<Review> createReviewHandler(@RequestBody ReviewRequest req, Principal principal) throws UserException, ProductException{
		User user=userService.findUserByEmail(principal.getName());
		
		Review review=reviewService.createReview(req, user);
		log.info("reivew created with product id: {} : {}", req.getProductId(), req.getReview());
		return new ResponseEntity<>(review,HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/product/{productId}")
	public ResponseEntity<List<Review>> getProductsReviewHandler(@PathVariable Long productId){
		List<Review>reviews=reviewService.getAllReview(productId);
		return new ResponseEntity<>(reviews,HttpStatus.OK);
	}

}
