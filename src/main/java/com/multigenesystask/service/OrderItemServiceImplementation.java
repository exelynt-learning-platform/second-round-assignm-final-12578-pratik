package com.multigenesystask.service;

import com.multigenesystask.entity.OrderItem;
import com.multigenesystask.repository.OrderItemRepository;

public class OrderItemServiceImplementation implements OrderItemService{
	
	
	private OrderItemRepository orderItemRepository;

	@Override
	public OrderItem createOrderItem(OrderItem orderItem) {
		return orderItemRepository.save(orderItem);
	}

}
