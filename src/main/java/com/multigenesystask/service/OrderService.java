package com.multigenesystask.service;

import java.util.List;

import com.multigenesystask.entity.Address;
import com.multigenesystask.entity.Order;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.OrderException;
import com.multigenesystask.exception.UserException;

public interface OrderService {
	
	public Order createOrder(User user, Address shippingAdress) throws OrderException, UserException;
	
	public Order findOrderById(Long orderId) throws OrderException;
	
	public List<Order> usersOrderHistory(Long userId) throws OrderException;
	
	public Order placedOrder(Long orderId) throws OrderException;
	
	public Order confirmedOrder(Long orderId)throws OrderException;
	
	public Order shippedOrder(Long orderId) throws OrderException;
	
	public Order deliveredOrder(Long orderId) throws OrderException;
	
	public Order cancleOrder(Long orderId) throws OrderException;
	
	public List<Order>getAllOrders();
	
	public void deleteOrder(Long orderId) throws OrderException;

}
