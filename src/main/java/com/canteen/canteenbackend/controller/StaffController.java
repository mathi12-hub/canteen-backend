package com.canteen.canteenbackend.controller;

import com.canteen.canteenbackend.dto.OrderResponse;
import com.canteen.canteenbackend.dto.StatusUpdateRequest;
import com.canteen.canteenbackend.model.Order;
import com.canteen.canteenbackend.model.OrderStatus;
import com.canteen.canteenbackend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Staff/kitchen-facing endpoints for managing order fulfilment.
 * Base path: /api/staff/orders
 *
 * In a production system these would sit behind authentication (e.g. Spring
 * Security + role-based access). Kept open here to keep the skill demo focused
 * on the ordering domain; see README for notes on adding auth.
 */
@RestController
@RequestMapping("/api/staff/orders")
@RequiredArgsConstructor
public class StaffController {

    private final OrderService orderService;

    /** GET /api/staff/orders            -> all orders, newest first
     *  GET /api/staff/orders?status=PENDING -> orders filtered by status, oldest first (FIFO queue)
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(@RequestParam(required = false) OrderStatus status) {
        List<Order> orders = (status != null)
                ? orderService.getOrdersByStatus(status)
                : orderService.getAllOrders();

        List<OrderResponse> response = orders.stream()
                .map(OrderResponse::fromEntity)
                .collect(Collectors.toList());
        return ResponseEntity.ok(response);
    }

    /** Move an order forward through the fulfilment pipeline: PENDING -> PREPARING -> READY -> COMPLETED. */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateStatus(@PathVariable Long id,
                                                        @Valid @RequestBody StatusUpdateRequest request) {
        Order updated = orderService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(OrderResponse.fromEntity(updated));
    }
}
