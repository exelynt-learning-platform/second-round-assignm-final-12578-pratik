package com.multigenesystask.service;

import java.awt.event.ItemEvent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.springframework.stereotype.Service;

import com.multigenesystask.constants.OrderStatus;
import com.multigenesystask.constants.PaymentStatus;
import com.multigenesystask.entity.Address;
import com.multigenesystask.entity.Cart;
import com.multigenesystask.entity.CartItem;
import com.multigenesystask.entity.Order;
import com.multigenesystask.entity.OrderItems;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.OrderException;
import com.multigenesystask.repository.AddressRepository;
import com.multigenesystask.repository.CartRepository;
import com.multigenesystask.repository.OrderItemRepository;
import com.multigenesystask.repository.OrderRepository;
import com.multigenesystask.repository.UserRepository;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class OrderServiceImplementation implements OrderService{
	
	private CartRepository cartRepository;
	
	private CartService cartItemService;
	
	private ProductService productService;
	
	private CartService cartService;
	private UserRepository userRepository;
	
	private OrderRepository orderRepository;
	
	private AddressRepository addressRepository;
	
	
	private OrderItemService orderItemService;
	
	private OrderItemRepository orderItemRepository;

	@Override
	public Order createOrder(User user, Address shippingAdress) throws OrderException {
		
		shippingAdress.setUser(user);
		Address address = addressRepository.save(shippingAdress);
		user.getAddress().add(address);
		userRepository.save(user);
		Cart cart = cartService.findUserCart(user.getId());
		List<OrderItems> orderItems = new ArrayList<OrderItems>();
		
		for(CartItem item: cart.getCartItems()) {
			OrderItems orderitem= new OrderItems();
			orderitem.setPrice(item.getPrice());
			orderitem.setProduct(item.getProduct());
			orderitem.setQuantity(item.getQuantity());
			orderitem.setSize(item.getSize());
			orderitem.setUserId(item.getUserId());
			orderitem.setDiscountedPrice(item.getDiscountedPrice());
			
			OrderItems createdOrderItem = orderItemRepository.save(orderitem);
			
			orderItems.add(createdOrderItem);
			
		}
		
		Order createdOrder = new Order();
		createdOrder.setUser(user);
		createdOrder.setOrderItems(orderItems);
		createdOrder.setTotalPrice(cart.getTotalPrice());
		createdOrder.setTotalDiscountedPrice(cart.getTotalDiscountedPrice());
		createdOrder.setDiscount(cart.getDiscount());
		createdOrder.setTotalItem(cart.getTotalItem());
		createdOrder.setShippingAddress(shippingAdress);
		createdOrder.setOrderDate(LocalDateTime.now());
		createdOrder.setOrderStatus(OrderStatus.PENDING);
		createdOrder.getPaymentDetails().setStatus(PaymentStatus.PENDING);
		createdOrder.setCreatedAt(LocalDateTime.now());
		
		Order savedOrder = orderRepository.save(createdOrder);
		
		for(OrderItems item: orderItems) {
			item.setOrder(savedOrder);
			orderItemRepository.save(item);
		}
		return savedOrder;
	}

	@Override
	public Order findOrderById(Long orderId) throws OrderException {
		
		return orderRepository.findById(orderId).orElseThrow(() -> new OrderException("Order not fount"));
	}

	@Override
	public List<Order> usersOrderHistory(Long userId) throws OrderException {
		List<Order> orders = orderRepository.getUsersOrders(userId);
		return null;
	}

	@Override
	public Order placedOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		order.setOrderStatus(OrderStatus.PLACED);
		order.getPaymentDetails().setStatus(PaymentStatus.COMPLETED);
		
		return orderRepository.save(order);
	}

	@Override
	public Order confirmationOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		order.setOrderStatus(OrderStatus.CONFIRMED);
		return orderRepository.save(order);
	}

	@Override
	public Order shippedOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		order.setOrderStatus(OrderStatus.SHIPPED);
		return orderRepository.save(order);
	}

	@Override
	public Order deliveredOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		order.setOrderStatus(OrderStatus.DELIVERED);
		return orderRepository.save(order);
	}

	@Override
	public Order cancledOrder(Long orderId) throws OrderException {
		Order order = findOrderById(orderId);
		order.setOrderStatus(OrderStatus.CANCELLED);
		return orderRepository.save(order);
	}

	@Override
	public List<Order> getAllOrders() {
		
		return orderRepository.findAll();
	}

	@Override
	public void deleteOrder(Long orderId) throws OrderException {
		orderRepository.deleteById(orderId);
		
	}

}
