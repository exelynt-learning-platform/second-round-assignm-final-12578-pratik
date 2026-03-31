package com.multigenesystask.service;

import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.CartItem;
import com.multigenesystask.entity.Product;
import com.multigenesystask.exception.CartItemException;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.repository.CartItemRepository;
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
class CartItemServiceTest {

    @Mock
    private CartItemRepository cartItemRepository;

    @InjectMocks
    private CartItemServiceImplementation cartItemService;

    private Product product;
    private CartItem cartItem;
    private Cart cart;

    @BeforeEach
    void setUp() {
        product = new Product();
        product.setId(1L);
        product.setPrice(1000);
        product.setDiscountedPrice(800);

        cart = new Cart();
        cart.setId(1L);

        cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setCart(cart);
        cartItem.setQuantity(2);
        cartItem.setUserId(1L);
        cartItem.setPrice(2000);
        cartItem.setDiscountedPrice(1600);
    }

    // ── createCartItem ─────────────────────────────────────────────────────────

    @Test
    void createCartItem_validItem_calculatesAndSaves() throws CartItemException {
        CartItem input = new CartItem();
        input.setProduct(product);
        input.setQuantity(2);

        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartItem result = cartItemService.createCartItem(input);

        assertEquals(2000, result.getPrice());        // 2 * 1000
        assertEquals(1600, result.getDiscountedPrice()); // 2 * 800
        verify(cartItemRepository).save(input);
    }

    @Test
    void createCartItem_nullProduct_throwsCartItemException() {
        CartItem input = new CartItem();
        input.setProduct(null);

        assertThrows(CartItemException.class, () -> cartItemService.createCartItem(input));
        verify(cartItemRepository, never()).save(any());
    }

    // ── updateCartItem ─────────────────────────────────────────────────────────

    @Test
    void updateCartItem_validRequest_updatesAndSaves() throws CartItemException, UserException {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any(CartItem.class))).thenAnswer(i -> i.getArgument(0));

        CartItem update = new CartItem();
        update.setQuantity(3);

        CartItem result = cartItemService.updateCartItem(1L, 1L, update);

        assertEquals(3, result.getQuantity());
        assertEquals(3000, result.getPrice());       // 3 * 1000
        assertEquals(2400, result.getDiscountedPrice()); // 3 * 800
    }

    @Test
    void updateCartItem_wrongUser_throwsCartItemException() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartItem update = new CartItem();
        update.setQuantity(1);

        // userId 99 != cartItem.userId 1
        assertThrows(CartItemException.class, () -> cartItemService.updateCartItem(99L, 1L, update));
    }

    @Test
    void updateCartItem_nullUserId_throwsCartItemException() {
        cartItem.setUserId(null);
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartItem update = new CartItem();
        update.setQuantity(1);

        assertThrows(CartItemException.class, () -> cartItemService.updateCartItem(1L, 1L, update));
    }

    @Test
    void updateCartItem_nullProduct_throwsCartItemException() {
        cartItem.setProduct(null);
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartItem update = new CartItem();
        update.setQuantity(1);

        assertThrows(CartItemException.class, () -> cartItemService.updateCartItem(1L, 1L, update));
    }

    @Test
    void updateCartItem_productPriceNull_usesZero() throws CartItemException, UserException {
        product.setPrice(null);
        product.setDiscountedPrice(null);
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));
        when(cartItemRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CartItem update = new CartItem();
        update.setQuantity(2);

        CartItem result = cartItemService.updateCartItem(1L, 1L, update);

        assertEquals(0, result.getPrice());
        assertEquals(0, result.getDiscountedPrice());
    }

    // ── removeCartItem ─────────────────────────────────────────────────────────

    @Test
    void removeCartItem_validOwner_deletesItem() throws CartItemException, UserException {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        cartItemService.removeCartItem(1L, 1L);

        verify(cartItemRepository).deleteById(1L);
    }

    @Test
    void removeCartItem_wrongUser_throwsCartItemException() {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        assertThrows(CartItemException.class, () -> cartItemService.removeCartItem(99L, 1L));
        verify(cartItemRepository, never()).deleteById(any());
    }

    // ── findCartItemById ───────────────────────────────────────────────────────

    @Test
    void findCartItemById_exists_returnsItem() throws CartItemException {
        when(cartItemRepository.findById(1L)).thenReturn(Optional.of(cartItem));

        CartItem result = cartItemService.findCartItemById(1L);
        assertEquals(1L, result.getId());
    }

    @Test
    void findCartItemById_notFound_throwsCartItemException() {
        when(cartItemRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(CartItemException.class, () -> cartItemService.findCartItemById(99L));
    }

    // ── isCartItemExist ────────────────────────────────────────────────────────

    @Test
    void isCartItemExist_found_returnsItem() {
        when(cartItemRepository.isCartItemExist(cart, product, "M", 1L)).thenReturn(cartItem);

        CartItem result = cartItemService.isCartItemExist(cart, product, "M", 1L);
        assertNotNull(result);
    }

    @Test
    void isCartItemExist_notFound_returnsNull() {
        when(cartItemRepository.isCartItemExist(cart, product, "XL", 1L)).thenReturn(null);

        CartItem result = cartItemService.isCartItemExist(cart, product, "XL", 1L);
        assertNull(result);
    }
}
