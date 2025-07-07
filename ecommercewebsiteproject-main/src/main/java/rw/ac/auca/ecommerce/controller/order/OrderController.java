package rw.ac.auca.ecommerce.controller.order;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.ecommerce.core.customer.model.Customer;
import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
import rw.ac.auca.ecommerce.core.order.model.Order;
import rw.ac.auca.ecommerce.core.order.model.OrderItem;
import rw.ac.auca.ecommerce.core.order.model.OrderStatus;
import rw.ac.auca.ecommerce.core.order.service.IOrderService;
import rw.ac.auca.ecommerce.core.product.model.Product;
import rw.ac.auca.ecommerce.core.product.service.IProductService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;
    private final IProductService productService;
    private final ICustomerService customerService;  // Inject customer service

    @GetMapping("/create")
    public String showOrderForm(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        model.addAttribute("order", new Order()); // optional binding
        return "order/create";
    }

    @PostMapping("/submit")
    public String submitOrder(@RequestParam UUID productId,
                              @RequestParam Integer quantity,
                              @RequestParam String shippingAddress,
                              @RequestParam String phoneNumber,
                              Model model) {

        Product product = productService.findProductByIdAndState(productId, true);
        if (product == null || quantity == null || quantity < 1) {
            return "redirect:/order/create?error=invalid";
        }

        // Get the first active customer (no authentication)
        Customer customer = customerService.findCustomersByState(true).stream().findFirst().orElse(null);
        if (customer == null) {
            // Handle case where no customer exists (return error or create default)
            return "redirect:/order/create?error=customerNotFound";
        }

        Order order = new Order();
        order.setOrderDate(LocalDateTime.now());
        order.setShippingAddress(shippingAddress);
        order.setPhoneNumber(phoneNumber);
        order.setStatus(OrderStatus.PENDING);
        order.setCustomer(customer); // assign the customer here!

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPriceAtOrder(BigDecimal.valueOf(product.getPrice()));
        item.setOrder(order);

        BigDecimal total = BigDecimal.valueOf(product.getPrice()).multiply(BigDecimal.valueOf(quantity));
        order.setItems(List.of(item));
        order.setTotalAmount(total);

        orderService.save(order);

        return "redirect:/order/list?success=true";
    }

    @GetMapping("/list")
    public String listOrders(Model model, @RequestParam(required = false) String success) {
        model.addAttribute("orders", orderService.getAllOrders());
        if ("true".equals(success)) {
            model.addAttribute("successMessage", "Order placed successfully!");
        }
        return "order/list";
    }

    @PostMapping("/cancel/{orderId}")
    public String cancelOrder(@PathVariable UUID orderId) {
        orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        return "redirect:/order/list";
    }

    @PostMapping("/delete/{orderId}")
    public String deleteOrder(@PathVariable UUID orderId) {
        orderService.deleteOrder(orderId);
        return "redirect:/order/list";
    }
}
