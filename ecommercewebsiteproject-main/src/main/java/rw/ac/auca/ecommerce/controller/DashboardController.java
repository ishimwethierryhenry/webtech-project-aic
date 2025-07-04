package rw.ac.auca.ecommerce.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import rw.ac.auca.ecommerce.core.customer.service.ICustomerService;
import rw.ac.auca.ecommerce.core.product.service.IProductService;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final ICustomerService customerService;
    private final IProductService productService;

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        model.addAttribute("customers", customerService.findCustomersByState(Boolean.TRUE));
        model.addAttribute("products", productService.findProductsByState(Boolean.TRUE));
        return "combinedView";
    }
}