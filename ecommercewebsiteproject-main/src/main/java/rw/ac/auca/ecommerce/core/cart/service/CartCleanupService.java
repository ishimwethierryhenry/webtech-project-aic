package rw.ac.auca.ecommerce.core.cart.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import rw.ac.auca.ecommerce.core.cart.model.Cart;
import rw.ac.auca.ecommerce.core.cart.model.CartItem;
import rw.ac.auca.ecommerce.core.cart.repository.ICartItemRepository;
import rw.ac.auca.ecommerce.core.cart.repository.ICartRepository;
import rw.ac.auca.ecommerce.core.product.service.IProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Service to clean up cart items that reference deleted or inactive products
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CartCleanupService {

    private final ICartRepository cartRepository;
    private final ICartItemRepository cartItemRepository;
    private final IProductService productService;

    /**
     * Scheduled method to clean up invalid cart items
     * Runs every hour
     */
    @Scheduled(fixedRate = 3600000) // 1 hour = 3600000 milliseconds
    @Transactional
    public void cleanupInvalidCartItems() {
        log.info("Starting cart cleanup process...");

        try {
            List<Cart> allCarts = cartRepository.findAll();
            int totalItemsRemoved = 0;
            int cartsProcessed = 0;

            for (Cart cart : allCarts) {
                List<CartItem> itemsToRemove = new ArrayList<>();

                for (CartItem item : cart.getItems()) {
                    try {
                        // Check if product still exists and is active
                        productService.findProductByIdAndState(item.getProduct().getId(), Boolean.TRUE);
                    } catch (Exception e) {
                        // Product not found or inactive, mark for removal
                        itemsToRemove.add(item);
                        log.debug("Marking cart item {} for removal - product {} not found or inactive",
                                item.getId(), item.getProduct().getId());
                    }
                }

                // Remove invalid items
                if (!itemsToRemove.isEmpty()) {
                    cart.getItems().removeAll(itemsToRemove);
                    cartItemRepository.deleteAll(itemsToRemove);

                    // Recalculate cart total
                    BigDecimal newTotal = cart.getItems().stream()
                            .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    cart.setTotalAmount(newTotal);

                    cartRepository.save(cart);

                    totalItemsRemoved += itemsToRemove.size();
                    cartsProcessed++;

                    log.info("Removed {} invalid items from cart {}", itemsToRemove.size(), cart.getId());
                }
            }

            log.info("Cart cleanup completed. Processed {} carts, removed {} invalid items",
                    cartsProcessed, totalItemsRemoved);

        } catch (Exception e) {
            log.error("Error during cart cleanup process: {}", e.getMessage(), e);
        }
    }

    /**
     * Manual cleanup method that can be called from admin interface
     */
    @Transactional
    public CleanupResult manualCleanup() {
        log.info("Manual cart cleanup initiated...");

        List<Cart> allCarts = cartRepository.findAll();
        int totalItemsRemoved = 0;
        int cartsProcessed = 0;
        List<String> details = new ArrayList<>();

        for (Cart cart : allCarts) {
            List<CartItem> itemsToRemove = new ArrayList<>();

            for (CartItem item : cart.getItems()) {
                try {
                    productService.findProductByIdAndState(item.getProduct().getId(), Boolean.TRUE);
                } catch (Exception e) {
                    itemsToRemove.add(item);
                    details.add(String.format("Cart %s: Removed item for product %s",
                            cart.getId(), item.getProduct().getId()));
                }
            }

            if (!itemsToRemove.isEmpty()) {
                cart.getItems().removeAll(itemsToRemove);
                cartItemRepository.deleteAll(itemsToRemove);

                BigDecimal newTotal = cart.getItems().stream()
                        .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                cart.setTotalAmount(newTotal);

                cartRepository.save(cart);

                totalItemsRemoved += itemsToRemove.size();
                cartsProcessed++;
            }
        }

        CleanupResult result = new CleanupResult(cartsProcessed, totalItemsRemoved, details);
        log.info("Manual cleanup completed: {}", result);
        return result;
    }

    /**
     * Result class for cleanup operations
     */
    public static class CleanupResult {
        private final int cartsProcessed;
        private final int itemsRemoved;
        private final List<String> details;

        public CleanupResult(int cartsProcessed, int itemsRemoved, List<String> details) {
            this.cartsProcessed = cartsProcessed;
            this.itemsRemoved = itemsRemoved;
            this.details = details;
        }

        public int getCartsProcessed() { return cartsProcessed; }
        public int getItemsRemoved() { return itemsRemoved; }
        public List<String> getDetails() { return details; }

        @Override
        public String toString() {
            return String.format("CleanupResult{cartsProcessed=%d, itemsRemoved=%d}",
                    cartsProcessed, itemsRemoved);
        }
    }
}