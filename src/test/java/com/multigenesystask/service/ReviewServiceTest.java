package com.multigenesystask.service;

import com.multigenesystask.entity.Product;
import com.multigenesystask.entity.Review;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.repository.ReviewRepository;
import com.multigenesystask.requests.ReviewRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

    @Mock private ReviewRepository reviewRepository;
    @Mock private ProductService productService;

    @InjectMocks
    private ReviewServiceImplementation reviewService;

    private User testUser;
    private Product testProduct;
    private ReviewRequest reviewRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testProduct = new Product();
        testProduct.setId(1L);

        reviewRequest = new ReviewRequest();
        reviewRequest.setProductId(1L);
        reviewRequest.setReview("Great product!");
    }

    @Test
    void createReview_validRequest_savesAndReturnsReview() throws ProductException {
        Review saved = new Review();
        saved.setId(1L);
        saved.setReview("Great product!");

        when(productService.findProductById(1L)).thenReturn(testProduct);
        when(reviewRepository.save(any(Review.class))).thenReturn(saved);

        Review result = reviewService.createReview(reviewRequest, testUser);

        assertNotNull(result);
        assertEquals("Great product!", result.getReview());
        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    void createReview_productNotFound_throwsProductException() throws ProductException {
        when(productService.findProductById(99L)).thenThrow(new ProductException("Not found"));
        reviewRequest.setProductId(99L);

        assertThrows(ProductException.class, () -> reviewService.createReview(reviewRequest, testUser));
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_setsAllFields() throws ProductException {
        when(productService.findProductById(1L)).thenReturn(testProduct);
        when(reviewRepository.save(any(Review.class))).thenAnswer(i -> i.getArgument(0));

        Review result = reviewService.createReview(reviewRequest, testUser);

        assertEquals(testUser, result.getUser());
        assertEquals(testProduct, result.getProduct());
        assertEquals("Great product!", result.getReview());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void getAllReview_returnsListFromRepo() {
        Review r1 = new Review(); r1.setReview("Nice!");
        Review r2 = new Review(); r2.setReview("Good quality");

        when(reviewRepository.getAllProductsReview(1L)).thenReturn(List.of(r1, r2));

        List<Review> result = reviewService.getAllReview(1L);

        assertEquals(2, result.size());
    }

    @Test
    void getAllReview_noReviews_returnsEmptyList() {
        when(reviewRepository.getAllProductsReview(99L)).thenReturn(List.of());

        List<Review> result = reviewService.getAllReview(99L);

        assertTrue(result.isEmpty());
    }
}
