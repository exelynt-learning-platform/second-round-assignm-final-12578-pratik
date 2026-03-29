package com.multigenesystask.requests;

import java.util.HashSet;
import java.util.Set;

import com.multigenesystask.entity.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {
	
	 private String title;

	    private String description;

	    private int price;

	    private int discountedPrice;
	   
	    private int discountPersent;

	    private int quantity;

	    private String brand;

	    private String color;

	    private Set<Size> size=new HashSet<>();

	    private String imageUrl;

	    private String topLavelCategory;
	    private String secondLavelCategory;
	    private String thirdLavelCategory;

	

}
