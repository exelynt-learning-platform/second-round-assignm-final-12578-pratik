package com.multigenesystask.service;

import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.CartItem;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.requests.AddItemRequest;

public interface CartService {

	
public Cart createCart(User user);
	
	public CartItem addCartItem(Long userId,AddItemRequest req) throws ProductException;
	
	public Cart findUserCart(Long userId);
	
	
}
