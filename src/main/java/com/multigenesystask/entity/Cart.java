package com.multigenesystask.entity;

import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Cart {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@OneToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<CartItem> cartItems = new HashSet<>();

	@Column(name = "total_price")
	private int totalPrice;

	  @Column(name = "total_item", nullable = false, columnDefinition = "int default 0")
	    private int totalItem;

	    @Column(name = "total_discounted_price", nullable = false, columnDefinition = "int default 0")  // ← was missing
	    private int totalDiscountedPrice;

	    @Column(name = "discount", nullable = false, columnDefinition = "int default 0")                // ← was missing
	    private int discount;

}
