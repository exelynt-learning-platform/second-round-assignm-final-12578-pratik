package com.multigenesystask.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.multigenesystask.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE LOWER(p.category.name) = :category")
    List<Product> findByCategory(@Param("category") String category);

    @Query("SELECT p FROM Product p WHERE " +
           "LOWER(p.title) LIKE %:query% OR " +
           "LOWER(p.description) LIKE %:query% OR " +
           "LOWER(p.brand) LIKE %:query% OR " +
           "LOWER(p.category.name) LIKE %:query%")
    List<Product> searchProduct(@Param("query") String query);

    @Query("SELECT p FROM Product p WHERE " +
           "(p.category.name = :category OR :category = '') " +
           "AND (:minPrice IS NULL OR p.discountedPrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.discountedPrice <= :maxPrice) " +
           "AND (:minDiscount IS NULL OR p.discountPersent >= :minDiscount) " +
           "AND (:color IS NULL OR LOWER(p.color) = LOWER(:color)) " +
           "AND (:stock = 'in_stock' AND p.quantity > 0 " +
           "     OR :stock = 'out_of_stock' AND p.quantity < 1 " +
           "     OR :stock IS NULL OR :stock = '') " +
           "ORDER BY " +
           "CASE WHEN :sort = 'price_low'  THEN p.discountedPrice END ASC, " +
           "CASE WHEN :sort = 'price_high' THEN p.discountedPrice END DESC, " +
           "p.createdAt DESC")
    Page<Product> filterProducts(
            @Param("category")    String category,
            @Param("minPrice")    Integer minPrice,
            @Param("maxPrice")    Integer maxPrice,
            @Param("minDiscount") Integer minDiscount,
            @Param("sort")        String sort,
            @Param("color")       String color,       // ← new
            @Param("stock")       String stock,       // ← new
            Pageable pageable                         // ← new
    );

    List<Product> findTop10ByOrderByCreatedAtDesc();
    
 // In ProductRepository — handles List<String> colors with IN clause
    @Query("SELECT p FROM Product p WHERE " +
           "(p.category.name = :category OR :category = '') " +
           "AND (:minPrice IS NULL OR p.discountedPrice >= :minPrice) " +
           "AND (:maxPrice IS NULL OR p.discountedPrice <= :maxPrice) " +
           "AND (:minDiscount IS NULL OR p.discountPersent >= :minDiscount) " +
           "AND (p.color IN :colors) " +
           "AND (:stock = 'in_stock' AND p.quantity > 0 " +
           "     OR :stock = 'out_of_stock' AND p.quantity < 1 " +
           "     OR :stock IS NULL OR :stock = '') " +
           "ORDER BY " +
           "CASE WHEN :sort = 'price_low'  THEN p.discountedPrice END ASC, " +
           "CASE WHEN :sort = 'price_high' THEN p.discountedPrice END DESC, " +
           "p.createdAt DESC")
    Page<Product> filterProductsByColors(
            @Param("category") String category,
            @Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            @Param("minDiscount") Integer minDiscount,
            @Param("sort") String sort,
            @Param("colors") List<String> colors,
            @Param("stock") String stock,
            Pageable pageable
    );
}

