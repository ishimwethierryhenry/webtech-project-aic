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
import rw.ac.auca.ecommerce.core.warehouse.model.Warehouse;
import rw.ac.auca.ecommerce.core.warehouse.service.IWarehouseService;

import java.math.BigDecimal;
import java.util.*;

@Controller
@RequestMapping("/order")
@RequiredArgsConstructor
public class OrderController {

    private final IOrderService orderService;
    private final ICustomerService customerService;
    private final IProductService productService;
    private final IWarehouseService warehouseService;

    // ======= ORDER MANAGEMENT =======

    @GetMapping("/manage")
    public String orderManagement(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        return "order/management";
    }

    @GetMapping("/{id}")
    public String getOrderDetails(@PathVariable UUID id, Model model) {
        Order order = orderService.getOrderById(id);
        if (order == null) return "redirect:/order/manage?error=notfound";
        model.addAttribute("order", order);
        return "order/details";
    }

    @PostMapping("/update-status")
    public String updateOrderStatus(@RequestParam UUID orderId, @RequestParam OrderStatus status) {
        orderService.updateOrderStatus(orderId, status);
        return "redirect:/order/" + orderId;
    }

    @PostMapping("/cancel/{id}")
    public String cancelOrder(@PathVariable UUID id) {
        orderService.updateOrderStatus(id, OrderStatus.CANCELLED);
        return "redirect:/order/manage";
    }

    // ======= CREATE ORDER FROM MULTIPLE ITEMS =======

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("order", new Order());
        model.addAttribute("products", productService.findProductsByState(true));
        model.addAttribute("statuses", OrderStatus.values());
        model.addAttribute("items", List.of(new OrderItem()));
        return "order/create";
    }

    @PostMapping("/create")
    public String createOrder(@ModelAttribute Order order, Model model) {
        Customer customer = customerService.findCustomersByState(true).stream().findFirst().orElse(null);
        if (customer != null) order.setCustomer(customer);

        for (OrderItem item : order.getItems()) {
            Product product = productService.findProductByIdAndState(item.getProduct().getId(), true);
            item.setProduct(product);
        }

        orderService.createOrder(order);
        deductStockFromWarehouse(order.getItems());

        model.addAttribute("message", "Order created successfully");
        return "redirect:/order/manage";
    }

    @PostMapping("/add-item")
    public String addOrderItem(@ModelAttribute Order order, Model model) {
        order.getItems().add(new OrderItem());
        model.addAttribute("order", order);
        model.addAttribute("products", productService.findProductsByState(true));
        model.addAttribute("statuses", OrderStatus.values());
        return "order/create";
    }

    // ======= CREATE ORDER FROM SINGLE PRODUCT =======

    @GetMapping("/make/{productId}")
    public String makeOrderFromSingleProduct(@PathVariable UUID productId, Model model) {
        Product product = productService.findProductByIdAndState(productId, true);
        if (product == null) return "redirect:/product/browse?error=notfound";

        Customer customer = customerService.findCustomersByState(true).stream().findFirst().orElse(null);

        Order order = new Order();
        order.setCustomer(customer);
        order.setStatus(OrderStatus.PENDING);

        OrderItem item = new OrderItem();
        item.setProduct(product);
        item.setQuantity(1);
        item.setPriceAtOrder(BigDecimal.valueOf(product.getPrice()));
        item.setOrder(order);

        order.setItems(List.of(item));

        model.addAttribute("order", order);
        model.addAttribute("statuses", OrderStatus.values());
        return "order/create";
    }

    // ======= ADMIN VIEW ALL ORDERS =======

    @GetMapping("/search/all")
    public String getAllOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "order/list";
    }

    // ======= STOCK DEDUCTION HELPER =======

    private void deductStockFromWarehouse(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            int quantityOrdered = item.getQuantity();

            for (Warehouse warehouse : warehouseService.getAllWarehouses()) {
                Map<Product, Integer> stock = warehouse.getProductStock();
                if (stock != null && stock.containsKey(product) && stock.get(product) >= quantityOrdered) {
                    stock.put(product, stock.get(product) - quantityOrdered);
                    warehouse.setProductStock(stock);
                    warehouseService.createWarehouse(warehouse); // If this method saves the updated stock
                    break;
                }
            }
        }
    }
}
