package com.canteen.canteenbackend.model;

/**
 * Lifecycle of an order as it moves through the canteen fulfilment process.
 *
 * PENDING    -> order placed by student, not yet acknowledged by staff
 * PREPARING  -> staff has accepted the order and is cooking / assembling it
 * READY      -> order is ready for pickup
 * COMPLETED  -> order has been collected by the customer
 * CANCELLED  -> order was cancelled (by staff or customer) before completion
 */
public enum OrderStatus {
    PENDING,
    PREPARING,
    READY,
    COMPLETED,
    CANCELLED
}
