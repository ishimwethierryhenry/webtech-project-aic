package rw.ac.auca.ecommerce.controller.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.ecommerce.core.cart.model.Cart;
import rw.ac.auca.ecommerce.core.cart.service.ICartService;
import rw.ac.auca.ecommerce.core.product.service.IProductService;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;
    private final IProductService productService;

    @GetMapping("/{customerId}")
    public String viewCart(@PathVariable UUID customerId, Model model) {
        Cart cart = cartService.getOrCreateCart(customerId);
        model.addAttribute("cart", cart);
        return "cart/view";
    }

    @PostMapping("/add/{customerId}/{productId}")
    public String addToCart(
            @PathVariable UUID customerId,
            @PathVariable UUID productId,
            @RequestParam(defaultValue = "1") int quantity
    ) {
        cartService.addToCart(customerId, productId, quantity);
        return "redirect:/cart/" + customerId;
    }

    @PostMapping("/update/{customerId}/{itemId}")
    public String updateCartItem(
            @PathVariable UUID customerId,
            @PathVariable UUID itemId,
            @RequestParam int quantity
    ) {
        cartService.updateCartItem(customerId, itemId, quantity);
        return "redirect:/cart/" + customerId;
    }

    @PostMapping("/remove/{customerId}/{itemId}")
    public String removeFromCart(
            @PathVariable UUID customerId,
            @PathVariable UUID itemId
    ) {
        cartService.removeFromCart(customerId, itemId);
        return "redirect:/cart/" + customerId;
    }
}
