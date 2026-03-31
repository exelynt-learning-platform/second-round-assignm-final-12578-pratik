package com.multigenesystask.service;

import com.multigenesystask.entity.*;
import com.multigenesystask.exception.OrderException;
import com.multigenesystask.exception.UserException;
import com.multigenesystask.repository.AddressRepository;
import com.multigenesystask.repository.OrderItemRepository;
import com.multigenesystask.repository.OrderRepository;
import com.multigenesystask.repository.UserRepository;
import com.multigenesystask.user.domain.OrderStatus;
import com.multigenesystask.user.domain.PaymentStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private CartService cartService;
    @Mock private UserRepository userRepository;
    @Mock private AddressRepository addressRepository;
    @Mock private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderServiceImplementation orderService;

    private User testUser;
    private Address shippingAddress;
    private Cart cart;
    private Order sampleOrder;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("user@test.com");
        testUser.setAddresses(new ArrayList<>());

        shippingAddress = new Address();
        shippingAddress.setId(1L);
        shippingAddress.setCity("Pune");

        Product product = new Product();
        product.setId(1L);
        product.setPrice(1000);
        product.setDiscountedPrice(800);

        CartItem cartItem = new CartItem();
        cartItem.setId(1L);
        cartItem.setProduct(product);
        cartItem.setQuantity(2);
        cartItem.setPrice(2000);
        cartItem.setDiscountedPrice(1600);
        cartItem.setSize("M");
        cartItem.setUserId(1L);

        Set<CartItem> items = new HashSet<>();
        items.add(cartItem);

        cart = new Cart();
        cart.setId(1L);
        cart.setUser(testUser);
        cart.setCartItems(items);
        cart.setTotalPrice(2000);
        cart.setTotalDiscountedPrice(1600);
        cart.setDiscount(400);
        cart.setTotalItem(2);

        sampleOrder = new Order();
        sampleOrder.setId(10L);
        sampleOrder.setOrderStatus(OrderStatus.PENDING);
        sampleOrder.setPaymentDetails(new PaymentDetails());
        sampleOrder.setOrderItems(new ArrayList<>());
    }

    // ── createOrder ────────────────────────────────────────────────────────────

    @Test
    void createOrder_validCart_createsOrderSuccessfully() throws OrderException, UserException {
        when(addressRepository.save(any(Address.class))).thenReturn(shippingAddress);
        when(userRepository.save(any(User.class))).thenReturn(testUser);
        when(cartService.findUserCart(1L)).thenReturn(cart);
        when(orderItemRepository.save(any(OrderItem.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> {
            Order o = i.getArgument(0);
            o.setId(10L);
            return o;
        });

        Order result = orderService.createOrder(testUser, shippingAddress);

        assertNotNull(result);
        assertEquals(OrderStatus.PENDING, result.getOrderStatus());
        verify(orderRepository, atLeastOnce()).save(any(Order.class));
    }

    @Test
    void createOrder_emptyCart_throwsOrderException() throws UserException {
        cart.setCartItems(new HashSet<>());

        when(addressRepository.save(any())).thenReturn(shippingAddress);
        when(userRepository.save(any())).thenReturn(testUser);
        when(cartService.findUserCart(1L)).thenReturn(cart);

        assertThrows(OrderException.class, () -> orderService.createOrder(testUser, shippingAddress));
    }

    @Test
    void createOrder_nullCartItems_throwsOrderException() throws UserException {
        cart.setCartItems(null);

        when(addressRepository.save(any())).thenReturn(shippingAddress);
        when(userRepository.save(any())).thenReturn(testUser);
        when(cartService.findUserCart(1L)).thenReturn(cart);

        assertThrows(OrderException.class, () -> orderService.createOrder(testUser, shippingAddress));
    }

    // ── findOrderById ──────────────────────────────────────────────────────────

    @Test
    void findOrderById_exists_returnsOrder() throws OrderException {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));

        Order result = orderService.findOrderById(10L);

        assertEquals(10L, result.getId());
    }

    @Test
    void findOrderById_notFound_throwsOrderException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderException.class, () -> orderService.findOrderById(99L));
    }

    // ── order status transitions ───────────────────────────────────────────────

    @Test
    void placedOrder_setsStatusPlacedAndPaymentCompleted() throws OrderException {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.placedOrder(10L);

        assertEquals(OrderStatus.PLACED, result.getOrderStatus());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentDetails().getStatus());
    }

    @Test
    void placedOrder_nullPaymentDetails_initializesAndSets() throws OrderException {
        sampleOrder.setPaymentDetails(null);
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.placedOrder(10L);

        assertNotNull(result.getPaymentDetails());
        assertEquals(PaymentStatus.COMPLETED, result.getPaymentDetails().getStatus());
    }

    @Test
    void confirmedOrder_setsStatusConfirmed() throws OrderException {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.confirmedOrder(10L);

        assertEquals(OrderStatus.CONFIRMED, result.getOrderStatus());
    }

    @Test
    void shippedOrder_setsStatusShipped() throws OrderException {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.shippedOrder(10L);

        assertEquals(OrderStatus.SHIPPED, result.getOrderStatus());
    }

    @Test
    void deliveredOrder_setsStatusDelivered() throws OrderException {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.deliveredOrder(10L);

        assertEquals(OrderStatus.DELIVERED, result.getOrderStatus());
    }

    @Test
    void cancleOrder_setsStatusCancelled() throws OrderException {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));
        when(orderRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Order result = orderService.cancleOrder(10L);

        assertEquals(OrderStatus.CANCELLED, result.getOrderStatus());
    }

    // ── usersOrderHistory & getAllOrders ───────────────────────────────────────

    @Test
    void usersOrderHistory_returnsListFromRepository() throws OrderException {
        List<Order> orders = List.of(sampleOrder);
        when(orderRepository.getUsersOrders(1L)).thenReturn(orders);

        List<Order> result = orderService.usersOrderHistory(1L);

        assertEquals(1, result.size());
    }

    @Test
    void getAllOrders_returnsAllOrders() {
        List<Order> orders = List.of(sampleOrder);
        when(orderRepository.findAllByOrderByCreatedAtDesc()).thenReturn(orders);

        List<Order> result = orderService.getAllOrders();

        assertEquals(1, result.size());
    }

    // ── deleteOrder ────────────────────────────────────────────────────────────

    @Test
    void deleteOrder_validId_deletesOrder() throws OrderException {
        when(orderRepository.findById(10L)).thenReturn(Optional.of(sampleOrder));

        orderService.deleteOrder(10L);

        verify(orderRepository).delete(sampleOrder);
    }

    @Test
    void deleteOrder_notFound_throwsOrderException() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(OrderException.class, () -> orderService.deleteOrder(99L));
    }
}
