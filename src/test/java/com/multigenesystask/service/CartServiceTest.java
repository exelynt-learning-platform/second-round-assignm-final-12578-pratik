package com.multigenesystask.service;

import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.CartItem;
import com.multigenesystask.entity.Product;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.CartItemException;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.repository.CartRepository;
import com.multigenesystask.repository.UserRepository;
import com.multigenesystask.requests.AddItemRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
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
    private Cart existingCart;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@test.com");

        existingCart = new Cart();
        existingCart.setId(1L);
        existingCart.setUser(testUser);
        existingCart.setCartItems(new HashSet<>());
    }

    @Test
    void createCart_newCart_savesAndReturns() {
        when(cartRepository.findByUserId(1L)).thenReturn(null);
        when(cartRepository.save(any(Cart.class))).thenReturn(existingCart);

        Cart result = cartService.createCart(testUser);

        assertNotNull(result);
        verify(cartRepository).save(any(Cart.class));
    }

    @Test
    void createCart_existingCart_returnsExistingWithoutSaving() {
        when(cartRepository.findByUserId(1L)).thenReturn(existingCart);

        Cart result = cartService.createCart(testUser);

        assertSame(existingCart, result);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void findUserCart_existingCart_calculatesAndSavesWhenChanged() throws UserException {
        existingCart.setTotalPrice(0);
        existingCart.setTotalDiscountedPrice(0);
        existingCart.setDiscount(0);
        existingCart.setTotalItem(0);

        when(cartRepository.findByUserId(1L)).thenReturn(existingCart);

        doAnswer(invocation -> {
            Cart c = invocation.getArgument(0);
            c.setTotalPrice(500);
            c.setTotalDiscountedPrice(400);
            c.setDiscount(100);
            c.setTotalItem(2);
            return null;
        }).when(cartCalculatorService).calculateCartTotals(any());

        when(cartRepository.save(any())).thenReturn(existingCart);

        Cart result = cartService.findUserCart(1L);

        assertNotNull(result);
        verify(cartCalculatorService).calculateCartTotals(existingCart);
        verify(cartRepository).save(existingCart);
    }

    @Test
    void findUserCart_noChange_doesNotSave() throws UserException {
        existingCart.setTotalPrice(100);
        existingCart.setTotalDiscountedPrice(80);
        existingCart.setDiscount(20);
        existingCart.setTotalItem(1);

        when(cartRepository.findByUserId(1L)).thenReturn(existingCart);
        doNothing().when(cartCalculatorService).calculateCartTotals(any());

        Cart result = cartService.findUserCart(1L);

        assertNotNull(result);
        verify(cartRepository, never()).save(any());
    }

    @Test
    void findUserCart_userNotFound_throwsUserException() {
        when(cartRepository.findByUserId(99L)).thenReturn(null);
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UserException.class, () -> cartService.findUserCart(99L));
    }

    @Test
    void findUserCart_cartNotFound_createsNewCart() throws UserException {
        when(cartRepository.findByUserId(1L)).thenReturn(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(cartRepository.save(any())).thenReturn(existingCart);
        doNothing().when(cartCalculatorService).calculateCartTotals(any());

        Cart result = cartService.findUserCart(1L);

        assertNotNull(result);
        verify(cartRepository, atLeastOnce()).save(any());
    }

    @Test
    void addCartItem_newItem_createsAndAddsToCart() throws ProductException, CartItemException, UserException {
        Product product = new Product();
        product.setId(10L);
        product.setPrice(500);
        product.setDiscountedPrice(400);

        AddItemRequest req = new AddItemRequest();
        req.setProductId(10L);
        req.setQuantity(2);
        req.setSize("M");

        CartItem created = new CartItem();
        created.setId(5L);
        created.setProduct(product);

        when(cartRepository.findByUserId(1L)).thenReturn(existingCart);
        when(productService.findProductById(10L)).thenReturn(product);
        when(cartItemService.isCartItemExist(existingCart, product, "M", 1L)).thenReturn(null);
        when(cartItemService.createCartItem(any(CartItem.class))).thenReturn(created);

        CartItem result = cartService.addCartItem(1L, req);

        assertNotNull(result);
        assertEquals(5L, result.getId());
        verify(cartItemService).createCartItem(any(CartItem.class));
    }

    @Test
    void addCartItem_existingItem_returnsExistingItem() throws ProductException, CartItemException, UserException {
        Product product = new Product();
        product.setId(10L);
        product.setDiscountedPrice(400);

        CartItem existing = new CartItem();
        existing.setId(3L);

        AddItemRequest req = new AddItemRequest();
        req.setProductId(10L);
        req.setQuantity(1);
        req.setSize("L");

        when(cartRepository.findByUserId(1L)).thenReturn(existingCart);
        when(productService.findProductById(10L)).thenReturn(product);
        when(cartItemService.isCartItemExist(existingCart, product, "L", 1L)).thenReturn(existing);

        CartItem result = cartService.addCartItem(1L, req);

        assertSame(existing, result);
        verify(cartItemService, never()).createCartItem(any());
    }
}
