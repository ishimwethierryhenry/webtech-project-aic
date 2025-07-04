package rw.ac.auca.ecommerce.core.order.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.ac.auca.ecommerce.core.order.model.OrderItem;

import java.util.List;
import java.util.UUID;

public interface IOrderItemRepository extends JpaRepository<OrderItem, UUID> {
    List<OrderItem> findByOrderId(UUID orderId);
}