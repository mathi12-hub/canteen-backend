package com.canteen.canteenbackend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotBlank(message = "Customer phone is required")
    private String customerPhone;

    private String notes;

    @NotEmpty(message = "Order must contain at least one item")
    @Valid
    private List<Item> items;

    @Data
    public static class Item {

        @NotNull(message = "menuItemId is required")
        private Long menuItemId;

        @Min(value = 1, message = "Quantity must be at least 1")
        private int quantity;
    }
}
