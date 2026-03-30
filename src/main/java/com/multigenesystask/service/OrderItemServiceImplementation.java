package com.multigenesystask.service;

import org.springframework.stereotype.Service;

import com.multigenesystask.entity.OrderItem;
import com.multigenesystask.repository.OrderItemRepository;

import lombok.AllArgsConstructor;


@AllArgsConstructor
@Service
public class OrderItemServiceImplementation implements OrderItemService{
	
	
	private OrderItemRepository orderItemRepository;

	@Override
	public OrderItem createOrderItem(OrderItem orderItem) {
		return orderItemRepository.save(orderItem);
	}

}
