package com.multigenesystask.entity;

import java.time.LocalDateTime;



import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Product {

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "discount_persent", nullable = false, columnDefinition = "int default 0")
    private Integer discountPersent;

    @Column(name = "price", nullable = false, columnDefinition = "int default 0")
    private Integer price;

    @Column(name = "discounted_price", nullable = false, columnDefinition = "int default 0")
    private Integer discountedPrice;

    @Column(name = "quantity", nullable = false, columnDefinition = "int default 0")
    private Integer quantity;

    @Column(name = "brand")
    private String brand;

    @Column(name = "color")
    private String color;



    @ElementCollection
    @CollectionTable(
            name = "product_sizes",               // ✅ Dedicated join table
            joinColumns = @JoinColumn(name = "product_id")
        )
    
    private Set<Size> sizes=new HashSet<>();

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Rating>ratings=new ArrayList<>();
    
    @OneToMany(mappedBy = "product",cascade = CascadeType.ALL,orphanRemoval = true)
    private List<Review>reviews=new ArrayList<>();

    @Column(name = "num_ratings")
    private int numRatings;
    

    @ManyToOne()
    @JoinColumn(name="category_id")
    private Category category;
    
    private LocalDateTime createdAt;

    
    
}
