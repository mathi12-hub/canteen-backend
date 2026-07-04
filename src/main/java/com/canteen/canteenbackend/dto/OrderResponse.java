package com.canteen.canteenbackend.dto;

import com.canteen.canteenbackend.model.Order;
import com.canteen.canteenbackend.model.OrderItem;
import com.canteen.canteenbackend.model.OrderStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
public class OrderResponse {

    private Long id;
    private String customerName;
    private String customerPhone;
    private OrderStatus status;
    private BigDecimal totalAmount;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<LineItem> items;

    @Data
    @Builder
    public static class LineItem {
        private Long menuItemId;
        private String name;
        private int quantity;
        private BigDecimal priceAtOrder;
        private BigDecimal lineTotal;
    }

    public static OrderResponse fromEntity(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .customerName(order.getCustomerName())
                .customerPhone(order.getCustomerPhone())
                .status(order.getStatus())
                .totalAmount(order.getTotalAmount())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .items(order.getItems().stream().map(OrderResponse::toLineItem).collect(Collectors.toList()))
                .build();
    }

    private static LineItem toLineItem(OrderItem oi) {
        return LineItem.builder()
                .menuItemId(oi.getMenuItem().getId())
                .name(oi.getMenuItem().getName())
                .quantity(oi.getQuantity())
                .priceAtOrder(oi.getPriceAtOrder())
                .lineTotal(oi.lineTotal())
                .build();
    }
}
