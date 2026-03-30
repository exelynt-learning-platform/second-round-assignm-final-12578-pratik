package com.multigenesystask.controller;

import java.security.Principal;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.entity.CartItem;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.CartItemException;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.response.ApiResponse;
import com.multigenesystask.service.CartItemService;
import com.multigenesystask.service.UserService;

@RestController
@RequestMapping("/api/cart_items")
public class CartItemController {

	private CartItemService cartItemService;
	private UserService userService;
	
	public CartItemController(CartItemService cartItemService,UserService userService) {
		this.cartItemService=cartItemService;
		this.userService=userService;
	}
	
	@DeleteMapping("/{cartItemId}")
	public ResponseEntity<ApiResponse>deleteCartItemHandler(@PathVariable Long cartItemId, Principal principal) throws CartItemException, UserException{
		
		User user=userService.findUserByEmail(principal.getName());
		cartItemService.removeCartItem(user.getId(), cartItemId);
		
		ApiResponse res=new ApiResponse("Item Remove From Cart",true);
		
		return new ResponseEntity<>(res,HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/{cartItemId}")
	public ResponseEntity<CartItem>updateCartItemHandler(@PathVariable Long cartItemId, @RequestBody CartItem cartItem, Principal principal) throws CartItemException, UserException{
		
		User user=userService.findUserByEmail(principal.getName());
		
		CartItem updatedCartItem =cartItemService.updateCartItem(user.getId(), cartItemId, cartItem);
		
		
		return new ResponseEntity<>(updatedCartItem,HttpStatus.ACCEPTED);
	}
}

