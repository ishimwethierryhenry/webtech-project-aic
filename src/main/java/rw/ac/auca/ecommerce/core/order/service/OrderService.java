package rw.ac.auca.ecommerce.core.order.service;

import rw.ac.auca.ecommerce.controller.api.OrderRequest;
import rw.ac.auca.ecommerce.core.order.model.Order;
import rw.ac.auca.ecommerce.core.order.model.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {
    Order createOrder(Order order);
    Order updateOrder(Order order);
    Order updateOrderStatus(UUID orderId, OrderStatus status);
    Order getOrderById(UUID id);
    List<Order> getOrdersByCustomer(UUID customerId);
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getAllOrders();
    void deleteOrder(UUID orderId);
    Order createOrder(OrderRequest request);
}