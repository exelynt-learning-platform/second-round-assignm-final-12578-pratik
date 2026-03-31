package com.multigenesystask.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.multigenesystask.user.domain.OrderStatus;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "orders")
public class Order {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@Column(name = "order_id")
	private String orderId;
	
	@ManyToOne
	@JoinColumn(name = "user_id")
	private User user;
	
	
	@OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
	private List<OrderItem> orderItems = new ArrayList<>();
	
	
	private LocalDateTime orderDate;
	
	
	private LocalDateTime deliveryDate;
	
	@OneToOne
	@JoinColumn(name = "shipping_address_id")
	private Address shippingAddress;
	
	
	@Embedded
	private PaymentDetails paymentDetails;
	
	
	private Integer totalPrice;
	
	
	private Integer totalDiscountedPrice;
	
	
	private Integer discount;
	
	@Enumerated(EnumType.STRING)
	private OrderStatus orderStatus;
	
	
	private int totalItem;
	
	private LocalDateTime createdAt;
	

}
