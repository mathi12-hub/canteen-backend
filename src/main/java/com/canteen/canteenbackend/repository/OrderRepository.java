package com.canteen.canteenbackend.repository;

import com.canteen.canteenbackend.model.Order;
import com.canteen.canteenbackend.model.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * All finder methods below fetch `items` (and their menu item) eagerly via
     * JOIN FETCH. The controllers map Order -> OrderResponse (which reads
     * order.getItems()) *after* the service's @Transactional method has
     * returned, so with open-in-view=false the Hibernate session is already
     * closed by then — accessing a lazy collection at that point throws
     * LazyInitializationException. Fetching it up front avoids that.
     */
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.menuItem WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") Long id);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.menuItem WHERE o.status = :status ORDER BY o.createdAt ASC")
    List<Order> findByStatusOrderByCreatedAtAsc(@Param("status") OrderStatus status);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.menuItem WHERE o.customerPhone = :phone ORDER BY o.createdAt DESC")
    List<Order> findByCustomerPhoneOrderByCreatedAtDesc(@Param("phone") String customerPhone);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items i LEFT JOIN FETCH i.menuItem ORDER BY o.createdAt DESC")
    List<Order> findAllByOrderByCreatedAtDesc();
}