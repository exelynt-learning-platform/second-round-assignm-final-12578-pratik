package com.multigenesystask.service;

import org.springframework.stereotype.Service;



import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.CartItem;
import com.multigenesystask.entity.Product;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.repository.CartRepository;
import com.multigenesystask.requests.AddItemRequest;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor

public class CartServiceImplementation implements CartService {

	private CartRepository cartRepository;

	private CartItemService cartItemService;

	private ProductService productService;
	
	private CartCalculatorService cartCalculatorService;

	@Override
	public Cart createCart(User user) {
		
		  Cart existingCart = cartRepository.findByUserId(user.getId());
	        if (existingCart != null) {
	            return existingCart;
	        }
	        Cart cart = new Cart();
	        cart.setUser(user);
	        return cartRepository.save(cart);
	}
	
	public Cart findUserCart(Long userId) {
		Cart cart =	cartRepository.findByUserId(userId);
		  if (cart == null) {
	            throw new RuntimeException("Cart not found for user: " + userId
	                + ". Please re-register or contact support.");
	        }
		cartCalculatorService.calculateCartTotals(cart);
		
		return cartRepository.save(cart);
		
	}

	@Override
	public CartItem addCartItem(Long userId, AddItemRequest req) throws ProductException {
		Cart cart=cartRepository.findByUserId(userId);
		
		  if (cart == null) {                               // ← null check here too
	            throw new RuntimeException("Cart not found for user: " + userId);
	        }
		Product product=productService.findProductById(req.getProductId());
		
		CartItem isPresent=cartItemService.isCartItemExist(cart, product, req.getSize(),userId);
		
		if(isPresent == null) {
			CartItem cartItem = new CartItem();
			cartItem.setProduct(product);
			cartItem.setCart(cart);
			cartItem.setQuantity(req.getQuantity());
			cartItem.setUserId(userId);
			
			
			int price=req.getQuantity()*product.getDiscountedPrice();
			cartItem.setPrice(price);
			cartItem.setSize(req.getSize());
			
			CartItem createdCartItem=cartItemService.createCartItem(cartItem);
			cart.getCartItems().add(createdCartItem);
			return createdCartItem;
		}
		
		
		return isPresent;
	}
}
