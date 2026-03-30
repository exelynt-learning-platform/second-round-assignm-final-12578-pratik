package com.multigenesystask.requests;

import java.util.HashSet;
import java.util.Set;

import com.multigenesystask.entity.Size;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateProductRequest {

    @NotBlank(message = "Title is required")
    @jakarta.validation.constraints.Size(min = 2, max = 100, message = "Title must be between 2 and 100 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @jakarta.validation.constraints.Size(min = 10, max = 1000, message = "Description must be between 10 and 1000 characters")
    private String description;

    @NotNull(message = "Price is required")
    @Min(value = 1, message = "Price must be greater than 0")
    private Integer price;

    @NotNull(message = "Discounted price is required")
    @Min(value = 0, message = "Discounted price cannot be negative")
    private Integer discountedPrice;

    @NotNull(message = "Discount percent is required")
    @Min(value = 0, message = "Discount percent cannot be negative")
    @jakarta.validation.constraints.Max(value = 100, message = "Discount percent cannot exceed 100")
    private Integer discountPersent;

    @NotNull(message = "Quantity is required")
    @Min(value = 0, message = "Quantity cannot be negative")
    private Integer quantity;

    @NotBlank(message = "Brand is required")
    @jakarta.validation.constraints.Size(min = 1, max = 50, message = "Brand must be between 1 and 50 characters")
    private String brand;

    @NotBlank(message = "Color is required")
    @jakarta.validation.constraints.Size(min = 1, max = 30, message = "Color must be between 1 and 30 characters")
    private String color;

    @NotEmpty(message = "At least one size must be provided")
    @Valid  // ← triggers validation on each Size object inside the set
    private Set<Size> size = new HashSet<>();

    @NotBlank(message = "Image URL is required")
    @jakarta.validation.constraints.Size(max = 500, message = "Image URL must not exceed 500 characters")
    private String imageUrl;

    @NotBlank(message = "Top level category is required")
    @jakarta.validation.constraints.Size(min = 2, max = 50, message = "Top level category must be between 2 and 50 characters")
    private String topLevelCategory;

    @NotBlank(message = "Second level category is required")
    @jakarta.validation.constraints.Size(min = 2, max = 50, message = "Second level category must be between 2 and 50 characters")
    private String secondLevelCategory;

    @NotBlank(message = "Third level category is required")
    @jakarta.validation.constraints.Size(min = 2, max = 50, message = "Third level category must be between 2 and 50 characters")
    private String thirdLevelCategory;

}