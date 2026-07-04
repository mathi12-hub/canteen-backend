package com.canteen.canteenbackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * Join entity between {@link Order} and {@link MenuItem}. Modelling the
 * many-to-many relationship explicitly (rather than with @ManyToMany) lets us
 * store per-line data: quantity ordered and the item's price at the moment
 * the order was placed (so later menu price changes don't rewrite history).
 */
@Entity
@Table(name = "order_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "price_at_order", nullable = false, precision = 8, scale = 2)
    private BigDecimal priceAtOrder;

    public BigDecimal lineTotal() {
        return priceAtOrder.multiply(BigDecimal.valueOf(quantity));
    }
}
