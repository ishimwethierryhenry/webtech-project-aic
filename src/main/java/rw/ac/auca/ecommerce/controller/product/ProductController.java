package rw.ac.auca.ecommerce.controller.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.ecommerce.core.product.model.Product;
import rw.ac.auca.ecommerce.core.product.service.IProductService;
import rw.ac.auca.ecommerce.core.util.product.EStockState;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Controller
@RequestMapping("/product/")
public class ProductController {
    private final IProductService productService;

    @GetMapping({"/", "/search/all"})
    public String getAllProducts(Model model) {
        model.addAttribute("products", productService.findProductsByState(Boolean.TRUE));
        return "combinedView"; // Now using combined view
    }

    @GetMapping("/register")
    public String getProductRegistrationPage(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("stockStates", EStockState.values());
        return "product/productRegistrationPage";
    }

    @PostMapping("/register")
    public String registerProduct(@ModelAttribute("product") Product theProduct, Model model) {
        if(Objects.nonNull(theProduct)) {
            // Set default values
            theProduct.setManufacturedDate(LocalDate.now());
            theProduct.setActive(Boolean.TRUE);

            productService.createProduct(theProduct);
            model.addAttribute("message","Product Saved Successfully");
        } else {
            model.addAttribute("error","Product Not Saved");
        }
        model.addAttribute("stockStates", EStockState.values());
        return "product/productRegistrationPage";
    }

    @PostMapping("/delete")
    public String deleteProduct(@RequestParam("id") String id, Model model) {
        if(Objects.nonNull(id)) {
            Product theProduct = new Product();
            theProduct.setId(UUID.fromString(id));
            productService.deleteProduct(theProduct);
        }
        return "redirect:/product/";
    }

    @PostMapping("/update")
    public String updateProduct(@RequestParam("id") String id, Model model) {
        if(Objects.nonNull(id)) {
            Product theProduct = productService.findProductByIdAndState(UUID.fromString(id), Boolean.TRUE);
            if(Objects.nonNull(theProduct)) {
                model.addAttribute("product", theProduct);
                model.addAttribute("stockStates", EStockState.values());
                return "product/productUpdatePage";
            }
        }
        model.addAttribute("error", "Wrong Information");
        return "redirect:/product/";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute("product") Product theProduct, Model model) {
        if(Objects.nonNull(theProduct)) {
            productService.updateProduct(theProduct);
        }
        return "redirect:/product/search/all";
    }
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam(value = "name", required = false) String name,
            @RequestParam(value = "minPrice", required = false) Double minPrice,
            @RequestParam(value = "maxPrice", required = false) Double maxPrice,
            @RequestParam(value = "stockState", required = false) EStockState stockState,
            Model model) {

        List<Product> products;

        if (name != null && !name.isEmpty()) {
            products = productService.findByProductNameContainingAndState(name, Boolean.TRUE);
        } else if (minPrice != null || maxPrice != null) {
            products = productService.findByPriceBetweenAndState(
                    minPrice != null ? minPrice : 0.0,
                    maxPrice != null ? maxPrice : Double.MAX_VALUE,
                    Boolean.TRUE
            );
        } else if (stockState != null) {
            products = productService.findProductsByStockStateAndState(stockState, Boolean.TRUE);
        } else {
            products = productService.findProductsByState(Boolean.TRUE);
        }

        model.addAttribute("products", products);
        model.addAttribute("stockStates", EStockState.values());
        return "product/productList";
    }
}