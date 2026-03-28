package com.multigenesystask.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.multigenesystask.entity.Product;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.requests.CreateProductRequest;
import com.multigenesystask.response.ApiResponse;
import com.multigenesystask.service.ProductService;


@RestController
@RequestMapping("/api/admin/products")
public class AdminProductController {

	@Autowired
	private ProductService productService;

	@PostMapping("/")
	public ResponseEntity<Product> createdProducts(@RequestBody CreateProductRequest req) {
		Product product = productService.createProduct(req);

		return new ResponseEntity<Product>(product, HttpStatus.CREATED);

	}

	@DeleteMapping("/{productId}/delete")
	public ResponseEntity<String> deleteProduct(@PathVariable Long productId) throws ProductException {
		productService.deleteProduct(productId);

		return new ResponseEntity<String>("Product deleted successfully", HttpStatus.OK);
	}

	@GetMapping("/all")
	public ResponseEntity<List<Product>> findAllProduct(){
		
		List<Product> products = productService.findAllProducts();
		return new ResponseEntity<List<Product>>(products, HttpStatus.OK);
	}
	
	
	@PutMapping("/{productId}/update")
	public ResponseEntity<Product> updateProduct(@RequestBody Product req, @PathVariable Long productId) throws ProductException{
		
		Product product = productService.updateProduct(productId, req);
		return new ResponseEntity<Product>(product, HttpStatus.CREATED);
	}

	
	@PostMapping("/creates")
	public ResponseEntity<ApiResponse> createMultipleProducts(@RequestBody CreateProductRequest[] req){
		
		for(CreateProductRequest product: req) {
			productService.createProduct(product);
		}
		
		ApiResponse apiResponse = new ApiResponse();
		apiResponse.setMessage("Multiple products created successfully");
		apiResponse.setStatus(true);
		
		return new ResponseEntity<ApiResponse>(apiResponse, HttpStatus.CREATED);
		
	}
}
