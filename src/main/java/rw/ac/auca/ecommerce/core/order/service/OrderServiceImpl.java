package rw.ac.auca.ecommerce.core.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.ac.auca.ecommerce.core.customer.model.Customer;
import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
import rw.ac.auca.ecommerce.core.order.model.Order;
import rw.ac.auca.ecommerce.core.order.model.OrderItem;
import rw.ac.auca.ecommerce.core.order.model.OrderStatus;
import rw.ac.auca.ecommerce.core.order.repository.IOrderRepository;
import rw.ac.auca.ecommerce.core.order.rest.dto.OrderRequest;
import rw.ac.auca.ecommerce.core.product.model.Product;
import rw.ac.auca.ecommerce.core.product.service.IProductService;
import rw.ac.auca.ecommerce.core.util.product.EStockState;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final IOrderRepository orderRepository;
    private final ICustomerService customerService;
    private final IProductService productService;

    @Override
    public Order createOrder(Order order) {
        return null;
    }

    @Override
    public Order updateOrder(Order order) {
        return null;
    }

    @Override
    public Order updateOrderStatus(UUID orderId, OrderStatus status) {
        return null;
    }

    @Override
    public Order getOrderById(UUID id) {
        return null;
    }

    @Override
    public List<Order> getOrdersByCustomer(UUID customerId) {
        return List.of();
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return List.of();
    }

    @Override
    public List<Order> getAllOrders() {
        return List.of();
    }

    @Override
    public void deleteOrder(UUID orderId) {

    }

    @Override
    public Order createOrder(rw.ac.auca.ecommerce.controller.api.OrderRequest request) {
        return null;
    }

    @Transactional
    @Override
    public Order createOrder(OrderRequest request) {
        // Validate request
        if (request == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }

        // Find customer
        Customer customer = customerService.findCustomerByIdAndState(
                request.getCustomerId(), Boolean.TRUE
        );

        // Create order
        Order order = new Order();
        order.setCustomer(customer);
        order.setShippingAddress(request.getShippingAddress());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        // Create and validate order items
        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            // Validate item request
            if (itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }

            Product product = productService.findProductByIdAndState(
                    itemRequest.getProductId(), Boolean.TRUE
            );

            // Check product availability
            if (product.getStockState() != EStockState.AVAILABLE) {
                throw new IllegalStateException(
                        "Product " + product.getProductName() + " is not available"
                );
            }

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPriceAtOrder(itemRequest.getPrice());
            item.setOrder(order);

            items.add(item);

            // Calculate item total
            BigDecimal itemTotal = itemRequest.getPrice().multiply(
                    BigDecimal.valueOf(itemRequest.getQuantity())
            );
            total = total.add(itemTotal);
        }

        // Set items and total
        order.setItems(items);
        order.setTotalAmount(total);

        // Save order
        return orderRepository.save(order);
    }

    // Add other service methods as needed
}