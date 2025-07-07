package rw.ac.auca.ecommerce.controller;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.ecommerce.controller.api.OrderRequest;
import rw.ac.auca.ecommerce.core.cart.service.ICartService;
import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
import rw.ac.auca.ecommerce.core.order.model.Order;
import rw.ac.auca.ecommerce.core.order.service.IOrderService;
import rw.ac.auca.ecommerce.service.SessionService;

import java.util.UUID;

@Controller
@RequestMapping("/order/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final ICartService cartService;
    private final IOrderService orderService;
    private final ICustomerService customerService;
    private final SessionService sessionService;


    @GetMapping
    public String showCheckoutForm(HttpSession session, Model model) {
        UUID customerId = sessionService.getCurrentCustomerId(session);
        if (customerId == null) {
            return "redirect:/login";
        }

        var customer = customerService.findCustomerByIdAndState(customerId, Boolean.TRUE);
        var cart = cartService.getOrCreateCart(customerId);

        model.addAttribute("customer", customer);
        model.addAttribute("cart", cart);
        model.addAttribute("orderRequest", new OrderRequest());
        return "order/checkout";
    }

    // Process the submitted checkout form
    @PostMapping
    public String processCheckout(
            @ModelAttribute OrderRequest orderRequest,
            HttpSession session
    ) {
        UUID customerId = sessionService.getCurrentCustomerId(session);
        if (customerId == null) {
            return "redirect:/login";
        }

        // Set customerId in orderRequest if not already set



        Order order = orderService.createOrderFromCart(customerId, orderRequest);
        cartService.clearCart(customerId);
        return "redirect:/order/" + order.getId();
    }
}
