//package rw.ac.auca.ecommerce.controller;
//
//import org.springframework.stereotype.Controller;
//import org.springframework.web.bind.annotation.GetMapping;
//import org.springframework.web.bind.annotation.RequestParam;
//
//import java.util.UUID;
//
//@Controller
//public class HomeController {
//
//    // Homepage route
//    @GetMapping("/")
//    public String homePage() {
//        return "home";
//    }
//
//    // Redirect to view cart
//    @GetMapping("/cart/VIEW")
//    public String viewCartRedirect(@RequestParam("customerId") UUID customerId) {
//        return "redirect:/cart/" + customerId;
//    }
//
//
//    @GetMapping("/order/checkout/GO")
//    public String checkoutRedirect(@RequestParam("customerId") UUID customerId) {
//        return "redirect:/order/checkout/" + customerId;
//    }
//}
package rw.ac.auca.ecommerce.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // You can add any model attributes if needed here
        return "home";  // Thymeleaf template "home.html"
    }
}

