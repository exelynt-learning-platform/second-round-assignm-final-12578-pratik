package com.multigenesystask.controller;

import java.util.List;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.entity.Order;
import com.multigenesystask.exception.OrderException;
import com.multigenesystask.response.ApiResponse;
import com.multigenesystask.service.OrderService;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/admin/orders")
@AllArgsConstructor
@Slf4j
public class AdminOrderController {
	
	private OrderService orderService;
	
	@GetMapping
	public ResponseEntity<List<Order>> getAllOrdersHandler(){
		List<Order> orders=orderService.getAllOrders();
		
		return new ResponseEntity<>(orders,HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/{orderId}/confirmed")
	public ResponseEntity<Order> confirmedOrderHandler(@PathVariable Long orderId) throws OrderException{
		Order order=orderService.confirmedOrder(orderId);
		return new ResponseEntity<>(order,HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/{orderId}/ship")
	public ResponseEntity<Order> shippedOrderHandler(@PathVariable Long orderId) throws OrderException{
		Order order=orderService.shippedOrder(orderId);
		return new ResponseEntity<>(order,HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/{orderId}/deliver")
	public ResponseEntity<Order> deliveredOrderHandler(@PathVariable Long orderId) throws OrderException{
		Order order=orderService.deliveredOrder(orderId);
		return new ResponseEntity<>(order,HttpStatus.ACCEPTED);
	}
	
	@PutMapping("/{orderId}/cancel")
	public ResponseEntity<Order> canceledOrderHandler(@PathVariable Long orderId) throws OrderException{
		Order order=orderService.cancleOrder(orderId);
		return new ResponseEntity<>(order,HttpStatus.ACCEPTED);
	}
	
	@DeleteMapping("/{orderId}/delete")
	public ResponseEntity<ApiResponse> deleteOrderHandler(@PathVariable Long orderId) throws OrderException{
		orderService.deleteOrder(orderId);
		ApiResponse res=new ApiResponse("Order Deleted Successfully",true);
		log.info("Delete Method in AdminOrder run perfectly fine");
		return new ResponseEntity<>(res,HttpStatus.ACCEPTED);

	}

}
