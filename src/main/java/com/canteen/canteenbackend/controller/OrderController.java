package com.canteen.canteenbackend.controller;

import com.canteen.canteenbackend.dto.OrderRequest;
import com.canteen.canteenbackend.dto.OrderResponse;
import com.canteen.canteenbackend.model.Order;
import com.canteen.canteenbackend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Customer-facing order endpoints: place an order and track its live status.
 * Base path: /api/orders
 */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    /** Place a new order. */
    @PostMapping
    public ResponseEntity<OrderResponse> placeOrder(@Valid @RequestBody OrderRequest request) {
        Order order = orderService.placeOrder(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.fromEntity(order));
    }

    /** Get a single order — used by the frontend to poll live status (pending -> preparing -> ready). */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(OrderResponse.fromEntity(orderService.getById(id)));
    }

    /** Get order history for a customer by phone number. */
    @GetMapping("/customer/{phone}")
    public ResponseEntity<List<OrderResponse>> getOrdersByCustomer(@PathVariable String phone) {
        List<OrderResponse> orders = orderService.getOrdersByCustomerPhone(phone)
                .stream().map(OrderResponse::fromEntity).collect(Collectors.toList());
        return ResponseEntity.ok(orders);
    }

    /** Customer-initiated cancellation (only allowed while PENDING/PREPARING, enforced in service layer). */
    @PatchMapping("/{id}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(@PathVariable Long id) {
        return ResponseEntity.ok(OrderResponse.fromEntity(orderService.cancelOrder(id)));
    }
}
