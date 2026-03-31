package com.multigenesystask.service;

import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.repository.CartRepository;
import com.multigenesystask.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @Mock private CartRepository cartRepository;
    @Mock private UserRepository userRepository;
    @Mock private CartItemService cartItemService;
    @Mock private ProductService productService;
    @Mock private CartCalculatorService cartCalculatorService;

    @InjectMocks
    private CartServiceImplementation cartService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");
    }

    @Test
    void createCart_newCart_savesAndReturns() {
        when(cartRepository.findByUserId(1L)).thenReturn(null);
        Cart savedCart = new Cart();
        savedCart.setUser(testUser);
        when(cartRepository.save(any(Cart.class))).thenReturn(savedCart);

        Cart result = cartService.createCart(testUser);

        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void createCart_existingCart_returnsExisting() {
        Cart existing = new Cart();
        existing.setUser(testUser);
        when(cartRepository.findByUserId(1L)).thenReturn(existing);

        Cart result = cartService.createCart(testUser);

        assertSame(existing, result);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void findUserCart_userNotFound_throwsUserException() {
        when(cartRepository.findByUserId(99L)).thenReturn(null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> cartService.findUserCart(99L));
    }
}