package com.multigenesystask.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
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

@RestController
@RequestMapping("/api/orders")
public class OrderController {
	
	@Autowired
	private OrderService orderService;
	@Autowired
	private UserService userService;
	
	@PostMapping("/")
	public ResponseEntity<Order> createOrder(@RequestBody Address shippingAddress, Principal principal) throws OrderException{
		User user = userService.findUserByEmail(principal.getName());
		
		Order order = orderService.createOrder(user, shippingAddress);
		
		return new ResponseEntity<Order>(order, HttpStatus.CREATED);
	}

	
	@GetMapping("/user")
	public ResponseEntity<List<Order>> userOrderHistroy(Principal principal) throws UserException, OrderException{
		User user = userService.findUserByEmail(principal.getName());
		List<Order> orders = orderService.usersOrderHistory(user.getId());
		
		return new ResponseEntity<List<Order>>(orders, HttpStatus.CREATED);
		
	}
	
	@GetMapping("/{id}")
	public ResponseEntity<Order> findOrderById(@PathVariable("id") Long orderId, Principal principal) throws OrderException{
		User user = userService.findUserByEmail(principal.getName());
		Order order = orderService.findOrderById(orderId);
		return new ResponseEntity<Order>(order, HttpStatus.CREATED);
	}
}
