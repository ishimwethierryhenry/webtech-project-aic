//package rw.ac.auca.ecommerce.controller.customer;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.stereotype.Controller;
//import org.springframework.ui.Model;
//import org.springframework.web.bind.annotation.*;
//import rw.ac.auca.ecommerce.core.cart.model.Cart;
//import rw.ac.auca.ecommerce.core.cart.service.ICartService;
//import rw.ac.auca.ecommerce.core.customer.model.Customer;
//import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
//import rw.ac.auca.ecommerce.core.order.model.Order;
//import rw.ac.auca.ecommerce.core.order.service.IOrderService;
//
//import java.util.List;
//import java.util.Objects;
//import java.util.UUID;
//
///**
// * The class CustomerController.
// *
// * @author Jeremie Ukundwa Tuyisenge
// * @version 1.0
// */
//@RequiredArgsConstructor
//@Controller
//@RequestMapping("/customer/")
//public class CustomerController {
//    private final ICustomerService customerService;
//
//
//    @GetMapping({"/", "/search/all"})
//    public String getAllCustomers(Model model) {
//        model.addAttribute("customers", customerService.findCustomersByState(Boolean.TRUE));
//        return "combinedView"; // Now using combined view
//    }
//
//    @GetMapping("/register")
//    public String getCustomerRegistrationPage(Model model){
//        model.addAttribute("customer" , new Customer());
//        return "customer/customerRegistrationPage";
//    }
//
//    @PostMapping("/register")
//    public String registerCustomer(@ModelAttribute("customer") Customer theCustomer , Model model){
//        if(Objects.nonNull(theCustomer)){
//            customerService.registerCustomer(theCustomer);
//            model.addAttribute("message","Data Saved Successful");
//        }else{
//            model.addAttribute("error","Data Not Saved Successful");
//        }
//
//        return "customer/customerRegistrationPage";
//    }
//
//    @PostMapping("/delete")
//    public String deleteCustomer(@RequestParam("id") String id, Model model){
//        if(Objects.nonNull(id)){
//            Customer theCustomer = new Customer();
//            theCustomer.setId(UUID.fromString(id));
//            customerService.deleteCustomer(theCustomer);
//        }
//        return "redirect:/customer/";
//    }
//
//    @PostMapping("/update")
//    public String updateCustomer(@RequestParam("id") String id, Model model){
//        if(Objects.nonNull(id)){
//            Customer theCustomer = customerService
//                    .findCustomerByIdAndState(UUID.fromString(id) , Boolean.TRUE);
//            if(Objects.nonNull(theCustomer)){
//                model.addAttribute("customer" , theCustomer);
//                return "customer/customerUpdatePage";
//            }
//        }
//        model.addAttribute("error" , "Wrong Information");
//        return "customer/customerList";
//    }
//
//    @PostMapping("/updateCustomer")
//    public String updateCustomer(@ModelAttribute("customer") Customer theCustomer,  Model model){
//        if(Objects.nonNull(theCustomer)){
//            System.out.println("The customer: "+theCustomer);
//             customerService.updateCustomer(theCustomer);
//        }
//        return "redirect:/customer/search/all";
//    }
//    // CustomerAccountController.java
//    @Controller
//    @RequestMapping("/customer/account")
//    @RequiredArgsConstructor
//    public class CustomerAccountController {
//        private final ICustomerService customerService;
//        private final IOrderService orderService;
//        private final ICartService cartService;
//
//        @GetMapping("/{customerId}")
//        public String customerDashboard(
//                @PathVariable UUID customerId,
//                Model model
//        ) {
//            Customer customer = customerService.findCustomerByIdAndState(customerId, Boolean.TRUE);
//            List<Order> orders = orderService.getOrdersByCustomer(customerId);
//            Cart cart = cartService.getOrCreateCart(customerId);
//
//            model.addAttribute("customer", customer);
//            model.addAttribute("orders", orders);
//            model.addAttribute("cart", cart);
//            return "customer/account";
//        }
//    }
//}
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

    @PostMapping("/deleteFirst")
    public String deleteFirstCustomer(Model model) {
        customerService.findCustomersByState(true).stream().findFirst().ifPresent(customerService::deleteCustomer);
        return "redirect:/customer/";
    }

    @GetMapping("/editFirst")
    public String editFirstCustomer(Model model) {
        Customer customer = customerService.findCustomersByState(true).stream().findFirst().orElse(null);
        if (customer != null) {
            model.addAttribute("customer", customer);
            return "customer/customerUpdatePage";
        } else {
            model.addAttribute("error", "No customers found");
            return "customer/customerList";
        }
    }

    @PostMapping("/updateCustomer")
    public String updateCustomer(@ModelAttribute("customer") Customer theCustomer, Model model) {
        if (Objects.nonNull(theCustomer)) {
            customerService.updateCustomer(theCustomer);
        }
        return "redirect:/customer/search/all";
    }

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

