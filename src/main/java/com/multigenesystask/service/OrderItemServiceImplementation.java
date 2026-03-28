package com.multigenesystask.service;

import com.multigenesystask.entity.OrderItems;
import com.multigenesystask.repository.OrderItemRepository;

public class OrderItemServiceImplementation implements OrderItemService{
	
	
	private OrderItemRepository orderItemRepository;

	@Override
	public OrderItems createOrderItem(OrderItems orderItem) {
		// TODO Auto-generated method stub
		return orderItemRepository.save(orderItem);
	}

}
