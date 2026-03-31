package com.multigenesystask.service;

import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.CartItem;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class CartCalculatorServiceTest {

    private CartCalculatorService service;
    private Cart cart;

    @BeforeEach
    void setUp() {
        service = new CartCalculatorService();
        cart = new Cart();
    }

    @Test
    void calculateCartTotals_nullItems_setsAllZero() {
        cart.setCartItems(null);

        service.calculateCartTotals(cart);

        assertEquals(0, cart.getTotalPrice());
        assertEquals(0, cart.getTotalDiscountedPrice());
        assertEquals(0, cart.getDiscount());
        assertEquals(0, cart.getTotalItem());
    }

    @Test
    void calculateCartTotals_emptyItems_setsAllZero() {
        cart.setCartItems(new HashSet<>());

        service.calculateCartTotals(cart);

        assertEquals(0, cart.getTotalPrice());
        assertEquals(0, cart.getTotalDiscountedPrice());
        assertEquals(0, cart.getDiscount());
        assertEquals(0, cart.getTotalItem());
    }

    @Test
    void calculateCartTotals_singleItem_calculatesCorrectly() {
        CartItem item = new CartItem();
        item.setPrice(1000);
        item.setDiscountedPrice(800);
        item.setQuantity(2);

        Set<CartItem> items = new HashSet<>();
        items.add(item);
        cart.setCartItems(items);

        service.calculateCartTotals(cart);

        assertEquals(1000, cart.getTotalPrice());
        assertEquals(800, cart.getTotalDiscountedPrice());
        assertEquals(200, cart.getDiscount());
        assertEquals(2, cart.getTotalItem());
    }

    @Test
    void calculateCartTotals_multipleItems_sumsCorrectly() {
        CartItem item1 = new CartItem();
        item1.setPrice(1000);
        item1.setDiscountedPrice(800);
        item1.setQuantity(1);

        CartItem item2 = new CartItem();
        item2.setPrice(500);
        item2.setDiscountedPrice(400);
        item2.setQuantity(3);

        Set<CartItem> items = new HashSet<>();
        items.add(item1);
        items.add(item2);
        cart.setCartItems(items);

        service.calculateCartTotals(cart);

        assertEquals(1500, cart.getTotalPrice());
        assertEquals(1200, cart.getTotalDiscountedPrice());
        assertEquals(300, cart.getDiscount());
        assertEquals(4, cart.getTotalItem());
    }

    @Test
    void calculateCartTotals_nullPriceOnItem_treatsAsZero() {
        CartItem item = new CartItem();
        item.setPrice(null);
        item.setDiscountedPrice(null);
        item.setQuantity(2);

        Set<CartItem> items = new HashSet<>();
        items.add(item);
        cart.setCartItems(items);

        service.calculateCartTotals(cart);

        assertEquals(0, cart.getTotalPrice());
        assertEquals(0, cart.getTotalDiscountedPrice());
        assertEquals(0, cart.getDiscount());
        assertEquals(2, cart.getTotalItem());
    }

    @Test
    void calculateCartTotals_discountIsCorrectDifference() {
        CartItem item = new CartItem();
        item.setPrice(2000);
        item.setDiscountedPrice(1500);
        item.setQuantity(1);

        Set<CartItem> items = new HashSet<>();
        items.add(item);
        cart.setCartItems(items);

        service.calculateCartTotals(cart);

        assertEquals(500, cart.getDiscount()); // 2000 - 1500
    }
}
