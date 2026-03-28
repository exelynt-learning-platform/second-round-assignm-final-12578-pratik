package com.multigenesystask.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.multigenesystask.entity.OrderItems;

public interface OrderItemRepository extends JpaRepository<OrderItems, Long>{

}
