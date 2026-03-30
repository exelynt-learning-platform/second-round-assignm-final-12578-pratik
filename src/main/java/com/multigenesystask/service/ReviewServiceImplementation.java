package com.multigenesystask.service;

import java.time.LocalDateTime;

import java.util.List;

import org.springframework.stereotype.Service;

import com.multigenesystask.entity.Product;
import com.multigenesystask.entity.Review;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.repository.ReviewRepository;
import com.multigenesystask.requests.ReviewRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class ReviewServiceImplementation implements ReviewService{
	
	private ReviewRepository reviewRepository;
	private ProductService productService;
	
	

	@Override
	public Review createReview(ReviewRequest req, User user) throws ProductException {
		Product product = productService.findProductById(req.getProductId());
		
		Review review = new Review();
		review.setUser(user);
		review.setProduct(product);
		review.setReview(req.getReview());
		review.setCreatedAt(LocalDateTime.now());
		
		
		return reviewRepository.save(review);
	}

	@Override
	public List<Review> getAllReview(Long productId) {
		return reviewRepository.getAllProductsReview(productId);
	}

}
