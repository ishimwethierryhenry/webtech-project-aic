package rw.ac.auca.ecommerce.controller.api;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

// OrderRequest.java
@Data
public class OrderRequest {
    private UUID customerId;
    private String shippingAddress;
    private List<OrderItemRequest> items;


    @Data
    public static class OrderItemRequest {
        private UUID productId;
        private int quantity;
        private BigDecimal price;

    }
}
