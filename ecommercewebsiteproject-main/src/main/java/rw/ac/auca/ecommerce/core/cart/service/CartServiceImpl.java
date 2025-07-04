package rw.ac.auca.ecommerce.core.cart.service;


import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rw.ac.auca.ecommerce.core.cart.model.Cart;
import rw.ac.auca.ecommerce.core.cart.model.CartItem;
import rw.ac.auca.ecommerce.core.cart.repository.ICartItemRepository;
import rw.ac.auca.ecommerce.core.cart.repository.ICartRepository;
import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
import rw.ac.auca.ecommerce.core.product.model.Product;
import rw.ac.auca.ecommerce.core.product.service.IProductService;

import java.math.BigDecimal;
import java.util.UUID;

// CartServiceImpl.java
@Service
@RequiredArgsConstructor
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
        Cart cart = getOrCreateCart(customerId);
        Product product = productService.findProductByIdAndState(productId, Boolean.TRUE);

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresentOrElse(
                        item -> item.setQuantity(item.getQuantity() + quantity),
                        () -> {
                            CartItem newItem = new CartItem();
                            newItem.setCart(cart);
                            newItem.setProduct(product);
                            newItem.setQuantity(quantity);
                            cart.getItems().add(newItem);
                        }
                );

        updateCartTotal(cart);
        return cartRepository.save(cart);
    }

    @Override
    public Cart updateCartItem(UUID cartId, UUID itemId, int quantity) {
        return null;
    }

    @Override
    public void removeFromCart(UUID cartId, UUID itemId) {

    }

    @Override
    public void clearCart(UUID cartId) {

    }

    private void updateCartTotal(Cart cart) {
        BigDecimal total = cart.getItems().stream()
                .map(item -> {
                    BigDecimal price = BigDecimal.valueOf(
                            productService.findProductByIdAndState(item.getProduct().getId(), Boolean.TRUE).getPrice()
                    );
                    return price.multiply(BigDecimal.valueOf(item.getQuantity()));
                })
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        cart.setTotalAmount(total);
    }


    // Implement other methods similarly
}