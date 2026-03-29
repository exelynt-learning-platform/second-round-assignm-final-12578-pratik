package com.multigenesystask.service;

import org.springframework.stereotype.Service;

import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.CartItem;

@Service
public class CartCalculatorService {

	
	public void calculateCartTotals(Cart cart) {
		int totalPrice = 0;
		int totalDiscountedPrice = 0;
		int totalItem = 0;
		for(CartItem item: cart.getCartItems()) {
			totalPrice =totalPrice +  item.getPrice();
			totalDiscountedPrice = totalDiscountedPrice + item.getDiscountedPrice();
			totalItem = totalItem + item.getQuantity();
			
		}
		cart.setTotalPrice(totalPrice);
		cart.setTotalDiscountedPrice(totalDiscountedPrice);
		cart.setDiscounte(totalPrice - totalDiscountedPrice);
		cart.setTotalItem(totalItem);
	}
}
