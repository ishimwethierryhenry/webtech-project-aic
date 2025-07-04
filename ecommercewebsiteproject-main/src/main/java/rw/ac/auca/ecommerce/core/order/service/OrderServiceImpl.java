package rw.ac.auca.ecommerce.core.order.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.ac.auca.ecommerce.core.cart.model.Cart;
import rw.ac.auca.ecommerce.core.cart.service.ICartService;
import rw.ac.auca.ecommerce.core.customer.model.Customer;
import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
import rw.ac.auca.ecommerce.core.order.model.Order;
import rw.ac.auca.ecommerce.core.order.model.OrderItem;
import rw.ac.auca.ecommerce.core.order.model.OrderStatus;
import rw.ac.auca.ecommerce.core.order.repository.IOrderRepository;
import rw.ac.auca.ecommerce.core.product.model.Product;
import rw.ac.auca.ecommerce.core.product.service.IProductService;
import rw.ac.auca.ecommerce.core.util.product.EStockState;
import rw.ac.auca.ecommerce.core.order.rest.dto.OrderRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements IOrderService {

    private final IOrderRepository orderRepository;
    private final ICustomerService customerService;
    private final IProductService productService;
    private final ICartService cartService; // ✅ Added missing dependency

    @Override
    public Order createOrder(Order order) {
        // You can implement direct order saving here if needed
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

    // This is for controller.api.OrderRequest (optional usage)
    @Override
    public Order createOrder(rw.ac.auca.ecommerce.controller.api.OrderRequest request) {
        return null;
    }

    @Override
    public Order createOrderFromCart(UUID customerId, rw.ac.auca.ecommerce.controller.api.OrderRequest orderRequest) {
        return null;
    }


    @Transactional
    @Override
    public Order createOrder(OrderRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Order request cannot be null");
        }

        Customer customer = customerService.findCustomerByIdAndState(request.getCustomerId(), Boolean.TRUE);

        Order order = new Order();
        order.setCustomer(customer);
        order.setShippingAddress(request.getShippingAddress());
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);

        List<OrderItem> items = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (OrderRequest.OrderItemRequest itemRequest : request.getItems()) {
            if (itemRequest.getQuantity() <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }

            Product product = productService.findProductByIdAndState(itemRequest.getProductId(), Boolean.TRUE);

            if (product.getStockState() != EStockState.AVAILABLE) {
                throw new IllegalStateException("Product " + product.getProductName() + " is not available");
            }

            OrderItem item = new OrderItem();
            item.setProduct(product);
            item.setQuantity(itemRequest.getQuantity());
            item.setPriceAtOrder(itemRequest.getPrice());
            item.setOrder(order);

            items.add(item);

            BigDecimal itemTotal = itemRequest.getPrice().multiply(BigDecimal.valueOf(itemRequest.getQuantity()));
            total = total.add(itemTotal);
        }

        order.setItems(items);
        order.setTotalAmount(total);

        return orderRepository.save(order);
    }


    @Transactional
    @Override
    public Order createOrderFromCart(UUID customerId, OrderRequest orderRequest) {
        Customer customer = customerService.findCustomerByIdAndState(customerId, Boolean.TRUE);
        Cart cart = cartService.getOrCreateCart(customerId);

        if (cart.getItems().isEmpty()) {
            throw new IllegalStateException("Cart is empty. Cannot create order.");
        }

        Order order = new Order();
        order.setCustomer(customer);
        order.setOrderDate(LocalDate.now().atStartOfDay());
        order.setStatus(OrderStatus.PENDING);
        order.setTotalAmount(cart.getTotalAmount());
        order.setShippingAddress(orderRequest.getShippingAddress());
        order.setPhoneNumber(orderRequest.getPhoneNumber());

        List<OrderItem> orderItems = cart.getItems().stream()
                .map(cartItem -> {
                    OrderItem orderItem = new OrderItem();
                    orderItem.setOrder(order);
                    orderItem.setProduct(cartItem.getProduct());
                    orderItem.setQuantity(cartItem.getQuantity());
                    orderItem.setPriceAtOrder(BigDecimal.valueOf(cartItem.getProduct().getPrice()));
                    return orderItem;
                })
                .collect(Collectors.toList());

        order.setItems(orderItems);
        return orderRepository.save(order);
    }
}
