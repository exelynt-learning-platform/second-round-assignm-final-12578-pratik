package com.multigenesystask.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multigenesystask.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long>{

}
