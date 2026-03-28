package com.multigenesystask.requests;

import java.util.HashSet;
import java.util.Set;

import com.multigenesystask.entity.Size;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateProductRequest {
	
	private String title;
	private String description;
	private int price;
	private int discountedPrice;
	private int discountPersent;
	private int quantity;
	private String brand;
	private String color;
	private Set<Size> sizes = new HashSet<>();
	
	private String imageUrl;
	private String topLevelCategory;
	private String secondLevelCategory;
	private String thirdLevelCategory;
	

}
