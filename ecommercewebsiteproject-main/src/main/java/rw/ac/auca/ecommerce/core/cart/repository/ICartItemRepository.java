package rw.ac.auca.ecommerce.core.cart.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.ac.auca.ecommerce.core.cart.model.CartItem;

import java.util.List;
import java.util.UUID;

public interface ICartItemRepository extends JpaRepository<CartItem, UUID> {
    List<CartItem> findByCartId(UUID cartId);
}