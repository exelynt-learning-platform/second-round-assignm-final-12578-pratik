package com.multigenesystask.service;

import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.CartItem;
import com.multigenesystask.entity.Product;
import com.multigenesystask.exception.CartItemException;
import com.multigenesystask.exception.UserException;

public interface CartItemService {
	
public CartItem createCartItem(CartItem cartItem) throws CartItemException;
	
	public CartItem updateCartItem(Long userId, Long id,CartItem cartItem) throws CartItemException, UserException;
	
	public CartItem isCartItemExist(Cart cart,Product product,String size, Long userId);
	
	public void removeCartItem(Long userId,Long cartItemId) throws CartItemException, UserException;
	
	public CartItem findCartItemById(Long cartItemId) throws CartItemException;

}
