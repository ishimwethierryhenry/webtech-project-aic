package rw.ac.auca.ecommerce.controller.customer;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.ecommerce.core.cart.model.Cart;
import rw.ac.auca.ecommerce.core.cart.service.ICartService;
import rw.ac.auca.ecommerce.core.customer.model.Customer;
import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
import rw.ac.auca.ecommerce.core.order.model.Order;
import rw.ac.auca.ecommerce.core.order.service.IOrderService;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/customer/")
public class CustomerController {

    private final ICustomerService customerService;
    private final ICartService cartService;
    private final IOrderService orderService;

    @GetMapping({"/", "/search/all"})
    public String getAllCustomers(Model model) {
        model.addAttribute("customers", customerService.findCustomersByState(true));
        return "combinedView";
    }

    @GetMapping("/register")
    public String getCustomerRegistrationPage(Model model) {
        model.addAttribute("customer", new Customer());
        return "customer/customerRegistrationPage";
    }

    @PostMapping("/register")
    public String registerCustomer(@ModelAttribute("customer") Customer theCustomer, Model model) {
        if (Objects.nonNull(theCustomer)) {
            customerService.registerCustomer(theCustomer);
            model.addAttribute("message", "Data Saved Successfully");
        } else {
            model.addAttribute("error", "Data Not Saved");
        }
        return "customer/customerRegistrationPage";
    }

    // Delete customer by specific ID
    @PostMapping("/delete/{id}")
    public String deleteCustomerById(@PathVariable UUID id) {
        Customer customer = customerService.findCustomerByIdAndState(id, true);
        if (customer != null) {
            customerService.deleteCustomer(customer);
        }
        return "redirect:/customer/search/all";
    }


    @GetMapping("/update/{id}")
    public String editCustomerById(@PathVariable UUID id, Model model) {
        Customer customer = customerService.findCustomerByIdAndState(id, true);
        if (customer != null) {
            model.addAttribute("customer", customer);
            return "customer/customerUpdatePage";
        } else {
            model.addAttribute("error", "Customer not found");
            return "customer/customerList";  // Or your combinedView if you want
        }
    }

    // Handle POST update submission
    @PostMapping("/updateCustomer")
    public String updateCustomer(@ModelAttribute("customer") Customer theCustomer, Model model) {
        if (Objects.nonNull(theCustomer)) {
            customerService.updateCustomer(theCustomer);
        }
        return "redirect:/customer/search/all";
    }

    // Dashboard for first active customer (optional, you can update similarly)
    @GetMapping("/account")
    public String customerDashboard(Model model) {
        Customer customer = customerService.findCustomersByState(true).stream().findFirst().orElse(null);
        if (customer != null) {
            UUID customerId = customer.getId();
            List<Order> orders = orderService.getOrdersByCustomer(customerId);
            Cart cart = cartService.getOrCreateCart(customerId);

            model.addAttribute("customer", customer);
            model.addAttribute("orders", orders);
            model.addAttribute("cart", cart);
        } else {
            model.addAttribute("error", "No customer found to show dashboard.");
        }
        return "customer/account";
    }
}
