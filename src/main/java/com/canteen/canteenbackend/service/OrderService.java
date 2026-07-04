package com.canteen.canteenbackend.service;

import com.canteen.canteenbackend.dto.OrderRequest;
import com.canteen.canteenbackend.exception.InvalidOrderException;
import com.canteen.canteenbackend.exception.ResourceNotFoundException;
import com.canteen.canteenbackend.model.MenuItem;
import com.canteen.canteenbackend.model.Order;
import com.canteen.canteenbackend.model.OrderItem;
import com.canteen.canteenbackend.model.OrderStatus;
import com.canteen.canteenbackend.repository.MenuItemRepository;
import com.canteen.canteenbackend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    /**
     * Valid forward transitions for order status. Staff can only move an
     * order along this path (or cancel it), preventing e.g. READY -> PENDING.
     */
    private static final Map<OrderStatus, List<OrderStatus>> ALLOWED_TRANSITIONS = Map.of(
            OrderStatus.PENDING, List.of(OrderStatus.PREPARING, OrderStatus.CANCELLED),
            OrderStatus.PREPARING, List.of(OrderStatus.READY, OrderStatus.CANCELLED),
            OrderStatus.READY, List.of(OrderStatus.COMPLETED, OrderStatus.CANCELLED),
            OrderStatus.COMPLETED, List.of(),
            OrderStatus.CANCELLED, List.of()
    );

    @Transactional
    public Order placeOrder(OrderRequest request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new InvalidOrderException("Order must contain at least one item");
        }

        Order order = Order.builder()
                .customerName(request.getCustomerName())
                .customerPhone(request.getCustomerPhone())
                .notes(request.getNotes())
                .status(OrderStatus.PENDING)
                .build();

        BigDecimal total = BigDecimal.ZERO;

        for (OrderRequest.Item lineRequest : request.getItems()) {
            MenuItem menuItem = menuItemRepository.findById(lineRequest.getMenuItemId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Menu item not found with id: " + lineRequest.getMenuItemId()));

            if (!Boolean.TRUE.equals(menuItem.getAvailable())) {
                throw new InvalidOrderException("Menu item '" + menuItem.getName() + "' is currently unavailable");
            }

            OrderItem orderItem = OrderItem.builder()
                    .menuItem(menuItem)
                    .quantity(lineRequest.getQuantity())
                    .priceAtOrder(menuItem.getPrice())
                    .build();

            order.addItem(orderItem);
            total = total.add(menuItem.getPrice().multiply(BigDecimal.valueOf(lineRequest.getQuantity())));
        }

        order.setTotalAmount(total);
        return orderRepository.save(order);
    }

    @Transactional(readOnly = true)
    public Order getById(Long id) {
        return orderRepository.findByIdWithItems(id)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAllByOrderByCreatedAtDesc();
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomerPhone(String phone) {
        return orderRepository.findByCustomerPhoneOrderByCreatedAtDesc(phone);
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtAsc(status);
    }

    @Transactional
    public Order updateStatus(Long id, OrderStatus newStatus) {
        Order order = getById(id);
        OrderStatus current = order.getStatus();

        if (current == newStatus) {
            return order; // idempotent no-op
        }

        List<OrderStatus> allowed = ALLOWED_TRANSITIONS.getOrDefault(current, List.of());
        if (!allowed.contains(newStatus)) {
            throw new InvalidOrderException(
                    "Cannot transition order from " + current + " to " + newStatus);
        }

        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    @Transactional
    public Order cancelOrder(Long id) {
        return updateStatus(id, OrderStatus.CANCELLED);
    }
}
