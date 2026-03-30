package com.multigenesystask.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.multigenesystask.entity.Product;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.requests.CreateProductRequest;
import com.multigenesystask.requests.UpdateProductRequest;

public interface ProductService {
	
public Product createProduct(CreateProductRequest req) throws ProductException;
	
	public String deleteProduct(Long productId) throws ProductException;
	
	public Product updateProduct(Long productId,UpdateProductRequest product)throws ProductException;
	
	public List<Product> getAllProducts();
	
	// for user and admin both
	public Product findProductById(Long id) throws ProductException;
	
	public List<Product> findProductByCategory(String category);
	
	public List<Product> searchProduct(String query);
	
	public Page<Product> getAllProduct(String category, List<String>colors, List<String> sizes, Integer minPrice, Integer maxPrice, Integer minDiscount,String sort, String stock, Integer pageNumber, Integer pageSize);
	
	public List<Product> recentlyAddedProduct();
}
