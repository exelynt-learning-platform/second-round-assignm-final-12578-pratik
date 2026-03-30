package com.multigenesystask.entity;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Embeddable
public class Size {

    @NotBlank(message = "Size name is required")
    @Pattern(regexp = "XS|S|M|L|XL|XXL", message = "Size must be one of: XS, S, M, L, XL, XXL")
    private String name;

    @NotNull(message = "Size quantity is required")
    @Min(value = 0, message = "Size quantity cannot be negative")
    private Integer quantity;
}
