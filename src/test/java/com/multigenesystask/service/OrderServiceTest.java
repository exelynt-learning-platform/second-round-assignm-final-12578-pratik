package com.multigenesystask.service;

import com.multigenesystask.entity.Order;
import com.multigenesystask.exception.OrderException;
import com.multigenesystask.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartService cartService;
    @Mock private com.multigenesystask.repository.UserRepository userRepository;
    @Mock private com.multigenesystask.repository.AddressRepository addressRepository;
    @Mock private com.multigenesystask.repository.OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderServiceImplementation orderService;

    @Test
    void findOrderById_exists_returnsOrder() throws OrderException {
        Order order = new Order();
        order.setId(1L);
        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order result = orderService.findOrderById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void findOrderById_notFound_throwsOrderException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(OrderException.class, () -> orderService.findOrderById(99L));
    }
}