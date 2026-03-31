package com.multigenesystask.service;

import org.springframework.stereotype.Service;

import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.CartItem;

@Service
public class CartCalculatorService {

	public void calculateCartTotals(Cart cart) {
		if (cart == null || cart.getCartItems() == null || cart.getCartItems().isEmpty()) {
			return; 
		}

		int totalPrice = 0;
		int totalDiscountedPrice = 0;
		int totalItem = 0;

		for (CartItem item : cart.getCartItems()) {
			totalPrice += item.getPrice() != null ? item.getPrice() : 0;
			totalDiscountedPrice += item.getDiscountedPrice() != null ? item.getDiscountedPrice() : 0;
			totalItem += item.getQuantity();
		}

		cart.setTotalPrice(totalPrice);
		cart.setTotalDiscountedPrice(totalDiscountedPrice);
		cart.setDiscount(totalPrice - totalDiscountedPrice);
		cart.setTotalItem(totalItem);
	}
}
