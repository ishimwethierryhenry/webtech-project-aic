//package rw.ac.auca.ecommerce.core.cart.service;
//
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Service;
//import rw.ac.auca.ecommerce.core.cart.model.Cart;
//import rw.ac.auca.ecommerce.core.cart.model.CartItem;
//import rw.ac.auca.ecommerce.core.cart.repository.ICartItemRepository;
//import rw.ac.auca.ecommerce.core.cart.repository.ICartRepository;
//import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
//import rw.ac.auca.ecommerce.core.product.model.Product;
//import rw.ac.auca.ecommerce.core.product.service.IProductService;
//
//import java.math.BigDecimal;
//import java.util.UUID;
//
//// CartServiceImpl.java
//@Service
//@RequiredArgsConstructor
//public class CartServiceImpl implements ICartService {
//    private final ICartRepository cartRepository;
//    private final ICustomerService customerService;
//    private final IProductService productService;
//    private final ICartItemRepository cartItemRepository;
//
//    @Override
//    @Transactional
//    public Cart getOrCreateCart(UUID customerId) {
//        return cartRepository.findByCustomerId(customerId)
//                .orElseGet(() -> {
//                    Cart newCart = new Cart();
//                    newCart.setCustomer(customerService.findCustomerByIdAndState(customerId, Boolean.TRUE));
//                    return cartRepository.save(newCart);
//                });
//    }
//
//    @Override
//    @Transactional
//    public Cart addToCart(UUID customerId, UUID productId, int quantity) {
//        Cart cart = getOrCreateCart(customerId);
//        Product product = productService.findProductByIdAndState(productId, Boolean.TRUE);
//
//        cart.getItems().stream()
//                .filter(item -> item.getProduct().getId().equals(productId))
//                .findFirst()
//                .ifPresentOrElse(
//                        item -> item.setQuantity(item.getQuantity() + quantity),
//                        () -> {
//                            CartItem newItem = new CartItem();
//                            newItem.setCart(cart);
//                            newItem.setProduct(product);
//                            newItem.setQuantity(quantity);
//                            cart.getItems().add(newItem);
//                        }
//                );
//
//        updateCartTotal(cart);
//        return cartRepository.save(cart);
//    }
//
//    @Override
//    public Cart updateCartItem(UUID cartId, UUID itemId, int quantity) {
//        return null;
//    }
//
//    @Override
//    public void removeFromCart(UUID cartId, UUID itemId) {
//
//    }
//
//    @Override
//    public void clearCart(UUID cartId) {
//
//    }
//
//    private void updateCartTotal(Cart cart) {
//        BigDecimal total = cart.getItems().stream()
//                .map(item -> {
//                    BigDecimal price = BigDecimal.valueOf(
//                            productService.findProductByIdAndState(item.getProduct().getId(), Boolean.TRUE).getPrice()
//                    );
//                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
//                })
//                .reduce(BigDecimal.ZERO, BigDecimal::add);
//
//        cart.setTotalAmount(total);
//    }
//
//
//    // Implement other methods similarly
//}

package rw.ac.auca.ecommerce.core.cart.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import rw.ac.auca.ecommerce.core.cart.model.Cart;
import rw.ac.auca.ecommerce.core.cart.model.CartItem;
import rw.ac.auca.ecommerce.core.cart.repository.ICartItemRepository;
import rw.ac.auca.ecommerce.core.cart.repository.ICartRepository;
import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
import rw.ac.auca.ecommerce.core.product.model.Product;
import rw.ac.auca.ecommerce.core.product.service.IProductService;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartServiceImpl implements ICartService {
    private final ICartRepository cartRepository;
    private final ICustomerService customerService;
    private final IProductService productService;
    private final ICartItemRepository cartItemRepository;

    @Override
    @Transactional
    public Cart getOrCreateCart(UUID customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setCustomer(customerService.findCustomerByIdAndState(customerId, Boolean.TRUE));
                    return cartRepository.save(newCart);
                });
    }

    @Override
    @Transactional
    public Cart addToCart(UUID customerId, UUID productId, int quantity) {
        try {
            Cart cart = getOrCreateCart(customerId);
            Product product = productService.findProductByIdAndState(productId, Boolean.TRUE);

            // Find existing cart item for this product
            CartItem existingItem = cart.getItems().stream()
                    .filter(item -> item.getProduct().getId().equals(productId))
                    .findFirst()
                    .orElse(null);

            if (existingItem != null) {
                // Update quantity of existing item
                existingItem.setQuantity(existingItem.getQuantity() + quantity);
                // Update unit price in case product price changed
                existingItem.setUnitPrice(BigDecimal.valueOf(product.getPrice()));
            } else {
                // Create new cart item
                CartItem newItem = new CartItem();
                newItem.setCart(cart);
                newItem.setProduct(product);
                newItem.setQuantity(quantity);
                newItem.setUnitPrice(BigDecimal.valueOf(product.getPrice())); // Store current price
                cart.getItems().add(newItem);
            }

            // Clean up invalid items and update total
            cleanupAndUpdateCartTotal(cart);
            return cartRepository.save(cart);

        } catch (Exception e) {
            log.error("Error adding product {} to cart for customer {}: {}", productId, customerId, e.getMessage());
            throw new RuntimeException("Failed to add product to cart: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Cart updateCartItem(UUID cartId, UUID itemId, int quantity) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));

            CartItem item = cart.getItems().stream()
                    .filter(ci -> ci.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Cart item not found"));

            if (quantity <= 0) {
                cart.getItems().remove(item);
                cartItemRepository.delete(item);
            } else {
                item.setQuantity(quantity);
            }

            cleanupAndUpdateCartTotal(cart);
            return cartRepository.save(cart);

        } catch (Exception e) {
            log.error("Error updating cart item {}: {}", itemId, e.getMessage());
            throw new RuntimeException("Failed to update cart item: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void removeFromCart(UUID cartId, UUID itemId) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));

            CartItem itemToRemove = cart.getItems().stream()
                    .filter(item -> item.getId().equals(itemId))
                    .findFirst()
                    .orElseThrow(() -> new RuntimeException("Cart item not found"));

            cart.getItems().remove(itemToRemove);
            cartItemRepository.delete(itemToRemove);

            cleanupAndUpdateCartTotal(cart);
            cartRepository.save(cart);

        } catch (Exception e) {
            log.error("Error removing cart item {}: {}", itemId, e.getMessage());
            throw new RuntimeException("Failed to remove cart item: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public void clearCart(UUID cartId) {
        try {
            Cart cart = cartRepository.findById(cartId)
                    .orElseThrow(() -> new RuntimeException("Cart not found"));

            // Delete all cart items
            cartItemRepository.deleteAll(cart.getItems());
            cart.getItems().clear();
            cart.setTotalAmount(BigDecimal.ZERO);

            cartRepository.save(cart);

        } catch (Exception e) {
            log.error("Error clearing cart {}: {}", cartId, e.getMessage());
            throw new RuntimeException("Failed to clear cart: " + e.getMessage(), e);
        }
    }

    /**
     * Clean up invalid cart items and update cart total
     * This method removes items for products that no longer exist or are inactive
     */
    private void cleanupAndUpdateCartTotal(Cart cart) {
        List<CartItem> itemsToRemove = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        // Check each cart item
        for (CartItem item : cart.getItems()) {
            try {
                // Verify product still exists and is active
                productService.findProductByIdAndState(item.getProduct().getId(), Boolean.TRUE);

                // Calculate total for valid items
                BigDecimal itemTotal = item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity()));
                total = total.add(itemTotal);

            } catch (Exception e) {
                // Product no longer exists or is inactive
                log.warn("Removing invalid cart item for product ID: {} - Reason: {}",
                        item.getProduct().getId(), e.getMessage());
                itemsToRemove.add(item);
            }
        }

        // Remove invalid items
        if (!itemsToRemove.isEmpty()) {
            cart.getItems().removeAll(itemsToRemove);
            cartItemRepository.deleteAll(itemsToRemove);
            log.info("Removed {} invalid items from cart", itemsToRemove.size());
        }

        // Update cart total
        cart.setTotalAmount(total);
    }

    /**
     * Alternative method for cases where you don't want to remove invalid items
     * Just calculates total based on stored unit prices
     */
    private void updateCartTotalSimple(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(total);
    }
}