package com.multigenesystask.service;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.multigenesystask.entity.Category;
import com.multigenesystask.entity.Product;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.repository.CategoryRepository;
import com.multigenesystask.repository.ProductRepository;
import com.multigenesystask.requests.CreateProductRequest;
import com.multigenesystask.requests.UpdateProductRequest;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class ProductServiceImplementation implements ProductService {

	private ProductRepository productRepository;

	private CategoryRepository categoryRepository;

	@Override
	public Product createProduct(CreateProductRequest req) throws ProductException {

		Category topLevel = getOrCreateCategory(req.getTopLevelCategory(), 1, null);

		Category secondLevel = getOrCreateCategory(req.getSecondLevelCategory(), 2, topLevel);

		Category thirdLevel = getOrCreateCategory(req.getThirdLevelCategory(), 3, secondLevel);

		Product product = new Product();
		product.setTitle(req.getTitle());
		product.setColor(req.getColor());
		product.setDescription(req.getDescription());
		product.setDiscountedPrice(req.getDiscountedPrice());
		product.setDiscountPersent(req.getDiscountPersent());
		product.setImageUrl(req.getImageUrl());
		product.setBrand(req.getBrand());
		product.setPrice(req.getPrice());
		product.setSizes(req.getSize());
		product.setQuantity(req.getQuantity());
		product.setCategory(thirdLevel);
		product.setCreatedAt(LocalDateTime.now());

		Product savedProduct = productRepository.save(product);

		log.info("Product is created {} ", savedProduct.getId());

		return savedProduct;
	}

	private Category getOrCreateCategory(String categoryName, int level, Category parent) {

		Category category;
		if (parent == null) {
			category = categoryRepository.findByName(categoryName);
		} else {
			category = categoryRepository.findByNameAndParant(categoryName, parent.getName());
		}

		if (category == null) {
			category = new Category();
			category.setName(categoryName);
			category.setLevel(level);
			category.setParentCategory(parent);
			category = categoryRepository.save(category);

		}
		return category;
	}

	@Override
	public String deleteProduct(Long productId) throws ProductException {

		Product product = findProductById(productId);
		if (product.getSizes() != null) {  // ← add null check
	        product.getSizes().clear();
	    }
		productRepository.delete(product);

		return "Product deleted Successfully";
	}

	@Override
	public Product updateProduct(Long productId, UpdateProductRequest req) throws ProductException {
		Product product = findProductById(productId);
		req.convertToProduct(product);

		return productRepository.save(product);
	}

	@Override
	public List<Product> getAllProducts() {
		return productRepository.findAll();
	}

	@Override
	public Product findProductById(Long id) throws ProductException {
		Optional<Product> opt = productRepository.findById(id);

		if (opt.isPresent()) {
			return opt.get();
		}
		throw new ProductException("product not found with id " + id);
	}

	@Override
	public List<Product> findProductByCategory(String category) {

		return productRepository.findByCategory(category);

		
	}

	@Override
	public List<Product> searchProduct(String query) {
	return productRepository.searchProduct(query);
		
	}

	@Override
	public Page<Product> getAllProduct(String category, List<String> colors, List<String> sizes,
	                                   Integer minPrice, Integer maxPrice, Integer minDiscount,
	                                   String sort, String stock, Integer pageNumber, Integer pageSize) {

	    Pageable pageable = PageRequest.of(pageNumber, pageSize);

	    String normalizedStock = (stock == null || stock.isBlank()) ? null : stock;

	    if (colors != null && colors.size() > 1) {
	        return productRepository.filterProductsByColors(
	                category, minPrice, maxPrice, minDiscount,
	                sort, colors, normalizedStock, pageable);
	    }

	    String color = (colors == null || colors.isEmpty()) ? null : colors.get(0);

	    return productRepository.filterProducts(
	            category, minPrice, maxPrice, minDiscount,
	            sort, color, normalizedStock, pageable);
	}
	@Override
	public List<Product> recentlyAddedProduct() {

		return productRepository.findTop10ByOrderByCreatedAtDesc();
	}

}
