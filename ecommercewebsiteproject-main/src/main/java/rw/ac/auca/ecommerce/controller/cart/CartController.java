package rw.ac.auca.ecommerce.controller.cart;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import rw.ac.auca.ecommerce.controller.api.OrderRequest;
import rw.ac.auca.ecommerce.core.cart.model.Cart;
import rw.ac.auca.ecommerce.core.cart.service.ICartService;
import rw.ac.auca.ecommerce.core.customer.model.Customer;
import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
import rw.ac.auca.ecommerce.core.order.model.Order;
import rw.ac.auca.ecommerce.core.order.service.IOrderService;

import java.util.UUID;

@Controller
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final ICartService cartService;
    private final IOrderService orderService;
    private final ICustomerService customerService;

    @GetMapping
    public String viewCart(Model model, @ModelAttribute("successMessage") String successMessage) {
        UUID customerId = getSampleCustomerId();
        Cart cart = cartService.getOrCreateCart(customerId);
        model.addAttribute("cart", cart);
        if (!successMessage.isEmpty()) {
            model.addAttribute("successMessage", successMessage);
        }
        return "cart/view";
    }

    @PostMapping("/add/{productId}")
    public String addToCart(@PathVariable UUID productId,
                            @RequestParam(defaultValue = "1") int quantity,
                            RedirectAttributes redirectAttributes) {
        UUID customerId = getSampleCustomerId();
        cartService.addToCart(customerId, productId, quantity);
        redirectAttributes.addFlashAttribute("successMessage", "Added to cart successfully!");
        return "redirect:/cart";
    }

    @PostMapping("/update/{itemId}")
    public String updateCartItem(@PathVariable UUID itemId,
                                 @RequestParam int quantity) {
        UUID customerId = getSampleCustomerId();
        cartService.updateCartItem(customerId, itemId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/remove/{itemId}")
    public String removeFromCart(@PathVariable UUID itemId) {
        UUID customerId = getSampleCustomerId();
        cartService.removeFromCart(customerId, itemId);
        return "redirect:/cart";
    }

//    @PostMapping("/checkout")
//    public String checkoutCart(@RequestParam("shippingAddress") String shippingAddress) {
//        UUID customerId = getSampleCustomerId();
//        OrderRequest orderRequest = new OrderRequest();
//        orderRequest.setShippingAddress(shippingAddress);
//        orderRequest.setCustomerId(customerId);
//
//        Order order = orderService.createOrderFromCart(customerId, orderRequest);
//        cartService.clearCart(customerId);
//        return "redirect:/order/" + order.getId();
//    }

    // 🔁 Helper method: select a default customer (for demo/public use)
    private UUID getSampleCustomerId() {
        return customerService.findCustomersByState(true)
                .stream()
                .findFirst()
                .map(Customer::getId)
                .orElse(UUID.randomUUID()); // fallback in case there’s no customer
    }
}
