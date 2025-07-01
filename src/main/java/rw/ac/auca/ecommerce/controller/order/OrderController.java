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

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;
    private final ICustomerService customerService;
    private final IProductService productService;

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("customers", customerService.findCustomersByState(Boolean.TRUE));
        model.addAttribute("products", productService.findProductsByState(Boolean.TRUE));
        model.addAttribute("statuses", OrderStatus.values());

        List<OrderItem> items = new ArrayList<>();
        items.add(new OrderItem());
        model.addAttribute("items", items);

        return "order/create";
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute Order order,
                              @RequestParam("customerId") UUID customerId,
                              Model model) {
        Customer customer = customerService.findCustomerByIdAndState(customerId, Boolean.TRUE);
        order.setCustomer(customer);

        for (OrderItem item : order.getItems()) {
            Product product = productService.findProductByIdAndState(item.getProduct().getId(), Boolean.TRUE);
            item.setProduct(product);
        }

        orderService.createOrder(order);
        model.addAttribute("message", "Order created successfully");
        return "redirect:/order/search/all";
    }

    @GetMapping("/search/all")
    public String getAllOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "order/list";
    }

    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable UUID id, Model model) {
        model.addAttribute("order", orderService.getOrderById(id));
        return "order/details";
    }

    @PostMapping("/update-status")
    public String updateOrderStatus(@RequestParam UUID orderId,
                                    @RequestParam OrderStatus status,
                                    Model model) {
        orderService.updateOrderStatus(orderId, status);
        model.addAttribute("message", "Order status updated successfully");
        return "redirect:/order/" + orderId;
    }

    @PostMapping("/add-item")
    public String addOrderItem(@ModelAttribute Order order, Model model) {
        order.getItems().add(new OrderItem());
        model.addAttribute("order", order);
        model.addAttribute("customers", customerService.findCustomersByState(Boolean.TRUE));
        model.addAttribute("products", productService.findProductsByState(Boolean.TRUE));
        model.addAttribute("statuses", OrderStatus.values());
        return "order/create";
    }
}
