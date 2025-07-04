package rw.ac.auca.ecommerce.controller;

import org.springframework.ui.Model;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.ecommerce.controller.api.OrderRequest;
import rw.ac.auca.ecommerce.core.cart.model.Cart;
import rw.ac.auca.ecommerce.core.cart.service.ICartService;
import rw.ac.auca.ecommerce.core.customer.model.Customer;
import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
import rw.ac.auca.ecommerce.core.order.model.Order;
import rw.ac.auca.ecommerce.core.order.service.IOrderService;

import java.util.UUID;

// CheckoutController.java
@Controller
@RequestMapping("/order/checkout")
@RequiredArgsConstructor
public class CheckoutController {
    private final ICartService cartService;
    private final IOrderService orderService;
    private final ICustomerService customerService;

    @GetMapping("/{customerId}")
    public String showCheckoutForm(
            @PathVariable UUID customerId,
            Model model
    ) {
        Customer customer = customerService.findCustomerByIdAndState(customerId, Boolean.TRUE);
        Cart cart = cartService.getOrCreateCart(customerId);

        model.addAttribute("customer", customer);
        model.addAttribute("cart", cart);
        model.addAttribute("orderRequest", new OrderRequest());
        return "order/checkout";
    }

    @PostMapping("/{customerId}")
    public String processCheckout(
            @PathVariable UUID customerId,
            @ModelAttribute OrderRequest orderRequest,
            Model model
    ) {
        Order order = orderService.createOrderFromCart(customerId, orderRequest);
        cartService.clearCart(customerId);
        return "redirect:/order/" + order.getId();
    }
}