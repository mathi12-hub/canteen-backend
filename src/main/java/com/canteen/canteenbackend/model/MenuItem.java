package com.canteen.canteenbackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

/**
 * A single sellable item on the canteen menu, e.g. "Veg Puff", "Masala Dosa".
 */
@Entity
@Table(name = "menu_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120, unique = true)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(nullable = false, precision = 8, scale = 2)
    private BigDecimal price;

    @Column(length = 60)
    private String category; // e.g. Beverages, Snacks, Meals, Desserts

    @Column(nullable = false)
    @Builder.Default
    private Boolean available = true;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @JsonIgnore
    @OneToMany(mappedBy = "menuItem", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private Set<OrderItem> orderItems = new HashSet<>();
}
