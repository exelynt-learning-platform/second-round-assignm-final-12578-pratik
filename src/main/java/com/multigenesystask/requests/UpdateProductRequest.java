package com.multigenesystask.requests;

import java.util.HashSet;
import java.util.Set;

import com.multigenesystask.entity.Product;
import com.multigenesystask.entity.Size;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateProductRequest {
	
	 private String title;

	    private String description;

	    private int price;

	    private int discountedPrice;
	   
	    private int discountPersent;

	    private int quantity;

	    private String brand;

	    private String color;

	    private Set<Size> sizes=new HashSet<>();

	    private String imageUrl;

	    private String topLevelCategory;
	    private String secondLevelCategory;
	    private String thirdLevelCategory;
	    
	    
	    public void convertToProduct(Product product) {
	        if (this.title != null)           product.setTitle(this.title);
	        if (this.description != null)     product.setDescription(this.description);
	        if (this.price != 0)              product.setPrice(this.price);
	        if (this.discountedPrice != 0)    product.setDiscountedPrice(this.discountedPrice);
	        if (this.discountPersent != 0)    product.setDiscountPersent(this.discountPersent);
	        if (this.quantity != 0)           product.setQuantity(this.quantity);
	        if (this.brand != null)           product.setBrand(this.brand);
	        if (this.color != null)           product.setColor(this.color);
	        if (this.sizes != null)           product.setSizes(this.sizes);
	        if (this.imageUrl != null)        product.setImageUrl(this.imageUrl);
	    }

	

}
