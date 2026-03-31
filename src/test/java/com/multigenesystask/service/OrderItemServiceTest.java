package com.multigenesystask.service;

import com.multigenesystask.entity.OrderItem;
import com.multigenesystask.repository.OrderItemRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderItemServiceTest {

    @Mock private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderItemServiceImplementation orderItemService;

    @Test
    void createOrderItem_savesAndReturns() {
        OrderItem item = new OrderItem();
        item.setId(1L);
        item.setQuantity(2);
        item.setPrice(500);

        when(orderItemRepository.save(any(OrderItem.class))).thenReturn(item);

        OrderItem result = orderItemService.createOrderItem(item);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        verify(orderItemRepository).save(item);
    }
}
