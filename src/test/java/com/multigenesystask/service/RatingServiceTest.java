package com.multigenesystask.service;

import com.multigenesystask.entity.Product;
import com.multigenesystask.entity.Rating;
import com.multigenesystask.entity.User;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.repository.RatingRepository;
import com.multigenesystask.requests.RatingRequest;
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
class RatingServiceTest {

    @Mock private RatingRepository ratingRepository;
    @Mock private ProductService productService;

    @InjectMocks
    private RatingServiceImplementation ratingService;

    private User testUser;
    private Product testProduct;
    private RatingRequest ratingRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);

        testProduct = new Product();
        testProduct.setId(1L);

        ratingRequest = new RatingRequest();
        ratingRequest.setProductId(1L);
        ratingRequest.setRating(4.5);
    }

    @Test
    void createRating_validRequest_savesAndReturnsRating() throws ProductException {
        Rating savedRating = new Rating();
        savedRating.setId(1L);
        savedRating.setRating(4.5);

        when(productService.findProductById(1L)).thenReturn(testProduct);
        when(ratingRepository.save(any(Rating.class))).thenReturn(savedRating);

        Rating result = ratingService.createRating(ratingRequest, testUser);

        assertNotNull(result);
        assertEquals(4.5, result.getRating());
        verify(ratingRepository).save(any(Rating.class));
    }

    @Test
    void createRating_productNotFound_throwsProductException() throws ProductException {
        when(productService.findProductById(99L)).thenThrow(new ProductException("Product not found"));
        ratingRequest.setProductId(99L);

        assertThrows(ProductException.class, () -> ratingService.createRating(ratingRequest, testUser));
        verify(ratingRepository, never()).save(any());
    }

    @Test
    void createRating_setsProductAndUserAndTimestamp() throws ProductException {
        when(productService.findProductById(1L)).thenReturn(testProduct);
        when(ratingRepository.save(any(Rating.class))).thenAnswer(i -> i.getArgument(0));

        Rating result = ratingService.createRating(ratingRequest, testUser);

        assertEquals(testProduct, result.getProduct());
        assertEquals(testUser, result.getUser());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    void getProductsRating_returnsListFromRepo() {
        Rating r1 = new Rating(); r1.setRating(4.0);
        Rating r2 = new Rating(); r2.setRating(5.0);

        when(ratingRepository.getAllProductsRating(1L)).thenReturn(List.of(r1, r2));

        List<Rating> result = ratingService.getProductsRating(1L);

        assertEquals(2, result.size());
    }

    @Test
    void getProductsRating_emptyProduct_returnsEmptyList() {
        when(ratingRepository.getAllProductsRating(99L)).thenReturn(List.of());

        List<Rating> result = ratingService.getProductsRating(99L);

        assertTrue(result.isEmpty());
    }
}
