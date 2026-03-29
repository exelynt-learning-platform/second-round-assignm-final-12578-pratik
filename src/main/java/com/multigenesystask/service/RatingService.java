package com.multigenesystask.service;

import java.util.List;

import com.multigenesystask.entity.Rating;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.requests.RatingRequest;

public interface RatingService {
	
public Rating createRating(RatingRequest req,User user) throws ProductException;
	
	public List<Rating> getProductsRating(Long productId);

}
