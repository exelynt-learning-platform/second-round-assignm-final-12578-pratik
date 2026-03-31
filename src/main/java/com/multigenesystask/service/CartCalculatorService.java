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


        if (cart.getCartItems() == null) {   // ← add this null check
            cart.setTotalPrice(0);
            cart.setTotalDiscountedPrice(0);
            cart.setDiscount(0);
            cart.setTotalItem(0);
            return;
        }

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
