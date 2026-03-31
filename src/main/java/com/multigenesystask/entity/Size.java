package com.multigenesystask.entity;

import java.util.Objects;

import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Size {

    @NotBlank(message = "Size name is required")
    @Pattern(regexp = "XS|S|M|L|XL|XXL", message = "Size must be one of: XS, S, M, L, XL, XXL")
    private String name;

    @NotNull(message = "Size quantity is required")
    @Min(value = 0, message = "Size quantity cannot be negative")
    private Integer quantity;
    
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Size)) return false;
        Size size = (Size) o;
        return quantity == size.quantity && Objects.equals(name, size.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, quantity);
    }
}
