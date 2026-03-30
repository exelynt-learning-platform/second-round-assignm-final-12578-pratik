package com.multigenesystask.service;

import java.util.Optional;


import org.springframework.stereotype.Service;

import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.CartItem;
import com.multigenesystask.entity.Product;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.CartItemException;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.repository.CartItemRepository;

import lombok.AllArgsConstructor;


@Service
@AllArgsConstructor

public class CartItemServiceImplementation implements CartItemService {

	private CartItemRepository cartItemRepository;
	private UserService userService;

	@Override
	public CartItem createCartItem(CartItem cartItem) {

		

		return cartItemRepository.save(cartItem);

	}

	@Override
	public CartItem updateCartItem(Long userId, Long id, CartItem cartItem) throws CartItemException, UserException {

		CartItem item = findCartItemById(id);
		if (item.getUserId() == null) {
			throw new CartItemException("Cart item has no user ID");
		}
		User user = userService.findUserById(item.getUserId());

		

		if (user.getId().equals(userId)) {

			item.setQuantity(cartItem.getQuantity());
			item.setPrice(item.getQuantity() * item.getProduct().getPrice());
			item.setDiscountedPrice(item.getQuantity() * item.getProduct().getDiscountedPrice());

			return cartItemRepository.save(item);

		} else {
			throw new CartItemException("You can't update  another user's cart item");
		}

	}

	@Override
	public CartItem isCartItemExist(Cart cart, Product product, String size, Long userId) {

		return cartItemRepository.isCartItemExist(cart, product, size, userId);

	}

	@Override
	public void removeCartItem(Long userId, Long cartItemId) throws CartItemException, UserException {

		CartItem cartItem = findCartItemById(cartItemId);

		if (cartItem.getUserId() == null) {
			throw new CartItemException("Cart item has no user ID");
		}
		
		
		User user = userService.findUserById(cartItem.getUserId());
		User reqUser = userService.findUserById(userId);

		if (user.getId().equals(reqUser.getId())) {
			cartItemRepository.deleteById(cartItem.getId());
		} else {
			throw new UserException("you can't remove anothor users item");
		}

	}

	@Override
	public CartItem findCartItemById(Long cartItemId) throws CartItemException {
		Optional<CartItem> opt = cartItemRepository.findById(cartItemId);

		if (opt.isPresent()) {
			return opt.get();
		}
		throw new CartItemException("cartItem not found with id : " + cartItemId);
	}

}
