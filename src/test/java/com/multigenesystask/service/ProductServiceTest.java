package com.multigenesystask.service;

import com.multigenesystask.entity.Category;
import com.multigenesystask.entity.Product;
import com.multigenesystask.exception.ProductException;
import com.multigenesystask.repository.CategoryRepository;
import com.multigenesystask.repository.ProductRepository;
import com.multigenesystask.requests.CreateProductRequest;
import com.multigenesystask.requests.UpdateProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock private ProductRepository productRepository;
    @Mock private CategoryRepository categoryRepository;

    @InjectMocks
    private ProductServiceImplementation productService;

    private Product sampleProduct;
    private CreateProductRequest createReq;

    @BeforeEach
    void setUp() {
        sampleProduct = new Product();
        sampleProduct.setId(1L);
        sampleProduct.setTitle("Test Shirt");
        sampleProduct.setPrice(1000);
        sampleProduct.setDiscountedPrice(800);
        sampleProduct.setSizes(new HashSet<>());

        createReq = new CreateProductRequest();
        createReq.setTitle("New Shirt");
        createReq.setDescription("A nice shirt for testing purposes");
        createReq.setPrice(1200);
        createReq.setDiscountedPrice(1000);
        createReq.setDiscountPersent(17);
        createReq.setQuantity(50);
        createReq.setBrand("TestBrand");
        createReq.setColor("Blue");
        createReq.setImageUrl("http://example.com/img.jpg");
        createReq.setTopLevelCategory("Men");
        createReq.setSecondLevelCategory("Clothing");
        createReq.setThirdLevelCategory("Shirts");
        createReq.setSize(new HashSet<>());
    }

    // ── createProduct ──────────────────────────────────────────────────────────

    @Test
    void createProduct_allCategoriesNew_createsAndSaves() throws ProductException {
        Category top = new Category(); top.setName("Men"); top.setId(1L);
        Category second = new Category(); second.setName("Clothing"); second.setId(2L);
        Category third = new Category(); third.setName("Shirts"); third.setId(3L);

        when(categoryRepository.findByName("Men")).thenReturn(null);
        when(categoryRepository.save(any(Category.class))).thenReturn(top, second, third);
        when(categoryRepository.findByNameAndParant(anyString(), anyString())).thenReturn(null);
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product result = productService.createProduct(createReq);

        assertNotNull(result);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_existingCategories_reusesCategories() throws ProductException {
        Category top = new Category(); top.setName("Men"); top.setId(1L);
        Category second = new Category(); second.setName("Clothing"); second.setId(2L);
        Category third = new Category(); third.setName("Shirts"); third.setId(3L);

        when(categoryRepository.findByName("Men")).thenReturn(top);
        when(categoryRepository.findByNameAndParant("Clothing", "Men")).thenReturn(second);
        when(categoryRepository.findByNameAndParant("Shirts", "Clothing")).thenReturn(third);
        when(productRepository.save(any(Product.class))).thenReturn(sampleProduct);

        Product result = productService.createProduct(createReq);

        assertNotNull(result);
        verify(categoryRepository, never()).save(any());
    }

    // ── findProductById ────────────────────────────────────────────────────────

    @Test
    void findProductById_exists_returnsProduct() throws ProductException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        Product result = productService.findProductById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void findProductById_notFound_throwsProductException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductException.class, () -> productService.findProductById(99L));
    }

    // ── deleteProduct ──────────────────────────────────────────────────────────

    @Test
    void deleteProduct_withSizes_clearsSizesAndDeletes() throws ProductException {
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        String result = productService.deleteProduct(1L);

        assertEquals("Product deleted Successfully", result);
        verify(productRepository).delete(sampleProduct);
    }

    @Test
    void deleteProduct_nullSizes_deletesWithoutError() throws ProductException {
        sampleProduct.setSizes(null);
        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));

        String result = productService.deleteProduct(1L);

        assertEquals("Product deleted Successfully", result);
        verify(productRepository).delete(sampleProduct);
    }

    @Test
    void deleteProduct_notFound_throwsProductException() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ProductException.class, () -> productService.deleteProduct(99L));
    }

    // ── updateProduct ──────────────────────────────────────────────────────────

    @Test
    void updateProduct_validRequest_updatesAndSaves() throws ProductException {
        UpdateProductRequest req = new UpdateProductRequest();
        req.setTitle("Updated Title");
        req.setPrice(1500);

        when(productRepository.findById(1L)).thenReturn(Optional.of(sampleProduct));
        when(productRepository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

        Product result = productService.updateProduct(1L, req);

        assertEquals("Updated Title", result.getTitle());
        verify(productRepository).save(sampleProduct);
    }

    // ── getAllProducts ─────────────────────────────────────────────────────────

    @Test
    void getAllProducts_returnsAll() {
        when(productRepository.findAll()).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.getAllProducts();

        assertEquals(1, result.size());
    }

    // ── searchProduct ──────────────────────────────────────────────────────────

    @Test
    void searchProduct_returnsMatchingProducts() {
        when(productRepository.searchProduct("shirt")).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.searchProduct("shirt");

        assertEquals(1, result.size());
    }

    // ── recentlyAddedProduct ───────────────────────────────────────────────────

    @Test
    void recentlyAddedProduct_returnsTop10() {
        when(productRepository.findTop10ByOrderByCreatedAtDesc()).thenReturn(List.of(sampleProduct));

        List<Product> result = productService.recentlyAddedProduct();

        assertFalse(result.isEmpty());
    }

    // ── getAllProduct with filters ─────────────────────────────────────────────

    @Test
    void getAllProduct_singleColor_callsFilterProducts() {
        Page<Product> page = new PageImpl<>(List.of(sampleProduct));
        when(productRepository.filterProducts(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        Page<Product> result = productService.getAllProduct("Men", List.of("Blue"), List.of("M"),
                0, 5000, 0, "price_low", null, 0, 10);

        assertNotNull(result);
        verify(productRepository).filterProducts(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void getAllProduct_multipleColors_callsFilterProductsByColors() {
        Page<Product> page = new PageImpl<>(List.of(sampleProduct));
        when(productRepository.filterProductsByColors(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class)))
                .thenReturn(page);

        Page<Product> result = productService.getAllProduct("Men", List.of("Blue", "Red"), List.of("M"),
                0, 5000, 0, "price_low", null, 0, 10);

        assertNotNull(result);
        verify(productRepository).filterProductsByColors(any(), any(), any(), any(), any(), any(), any(), any(Pageable.class));
    }

    @Test
    void getAllProduct_nullColors_callsFilterProductsWithNullColor() {
        Page<Product> page = new PageImpl<>(List.of(sampleProduct));
        when(productRepository.filterProducts(any(), any(), any(), any(), any(), isNull(), any(), any(Pageable.class)))
                .thenReturn(page);

        Page<Product> result = productService.getAllProduct("Men", null, null,
                0, 5000, 0, "price_low", null, 0, 10);

        assertNotNull(result);
    }

    @Test
    void getAllProduct_blankStock_passesNullToRepo() {
        Page<Product> page = new PageImpl<>(List.of(sampleProduct));
        when(productRepository.filterProducts(any(), any(), any(), any(), any(), any(), isNull(), any(Pageable.class)))
                .thenReturn(page);

        Page<Product> result = productService.getAllProduct("Men", null, null,
                0, 5000, 0, "price_low", "   ", 0, 10);

        assertNotNull(result);
    }
}
