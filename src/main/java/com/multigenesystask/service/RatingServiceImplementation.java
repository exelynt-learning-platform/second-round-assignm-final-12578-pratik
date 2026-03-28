package com.multigenesystask.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.multigenesystask.entity.Product;
import com.multigenesystask.entity.Rating;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.repository.RatingRepository;
import com.multigenesystask.requests.RatingRequest;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class RatingServiceImplementation implements RatingService{

	private RatingRepository ratingRepository;
	
	private ProductService productService;
	@Override
	public Rating createRating(RatingRequest req, User user) throws ProductException {
		Product product = productService.findProductById(req.getProductId());
		Rating rating = new Rating();
		rating.setProduct(product);
		rating.setUser(user);
		rating.setRating(req.getRating());
		rating.setCreatedDate(LocalDateTime.now());
		
		return ratingRepository.save(rating);


	}

	@Override
	public List<Rating> getProductRatings(Long productId) {
		return ratingRepository.getAllProductsRating(productId);
	}

}
