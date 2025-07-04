package rw.ac.auca.ecommerce.core.cart.service;

import rw.ac.auca.ecommerce.core.cart.model.Cart;

import java.util.UUID;

public interface ICartService {
    Cart getOrCreateCart(UUID customerId);
    Cart addToCart(UUID customerId, UUID productId, int quantity);
    Cart updateCartItem(UUID cartId, UUID itemId, int quantity);
    void removeFromCart(UUID cartId, UUID itemId);
    void clearCart(UUID cartId);
}