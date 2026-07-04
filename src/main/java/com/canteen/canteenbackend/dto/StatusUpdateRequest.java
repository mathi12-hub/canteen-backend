package com.canteen.canteenbackend.dto;

import com.canteen.canteenbackend.model.OrderStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotNull(message = "status is required")
    private OrderStatus status;
}
