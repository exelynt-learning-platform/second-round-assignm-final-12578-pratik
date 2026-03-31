package com.multigenesystask.controller;

import java.security.Principal;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.entity.Address;
import com.multigenesystask.entity.Order;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.OrderException;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.service.OrderService;
import com.multigenesystask.service.UserService;

import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/api/orders")
@AllArgsConstructor
public class OrderController {
	
	
	private OrderService orderService;

	private UserService userService;
	
	@PostMapping
	public ResponseEntity<Order> createOrderHandler(
			@RequestBody Address shippingAddress,
			Principal principal) throws UserException, OrderException{
		
		User user=userService.findUserByEmail(principal.getName());
		Order order =orderService.createOrder(user, shippingAddress);
		
		return new ResponseEntity<>(order,HttpStatus.OK);
		
	}
	
	@GetMapping("/user")
	public ResponseEntity< List<Order>> usersOrderHistoryHandler(Principal principal) throws OrderException, UserException{
		
		User user=userService.findUserByEmail(principal.getName());
		List<Order> orders=orderService.usersOrderHistory(user.getId());
		return new ResponseEntity<>(orders,HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/{orderId}")
	public ResponseEntity< Order> findOrderHandler(@PathVariable Long orderId) throws OrderException, UserException{
		
		Order orders=orderService.findOrderById(orderId);
		return new ResponseEntity<>(orders,HttpStatus.ACCEPTED);
	}
}
