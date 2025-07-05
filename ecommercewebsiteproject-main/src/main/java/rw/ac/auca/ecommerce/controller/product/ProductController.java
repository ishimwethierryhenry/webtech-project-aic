package rw.ac.auca.ecommerce.controller.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.ecommerce.core.product.model.Product;
import rw.ac.auca.ecommerce.core.product.service.IProductCategoryService;
import rw.ac.auca.ecommerce.core.product.service.IProductService;
import rw.ac.auca.ecommerce.core.util.product.EStockState;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
@RequestMapping("/product")
public class ProductController {

    private final IProductService productService;
    private final IProductCategoryService categoryService;

    @GetMapping("/search/all")
    public String searchAllProducts(Model model) {
        List<Product> products = productService.findProductsByState(true);
        model.addAttribute("products", products);
        model.addAttribute("stockStates", EStockState.values());
        model.addAttribute("categories", categoryService.getActiveCategories(true));
        return "product/productList";
    }

    @GetMapping("/browse")
    public String browseProducts(Model model) {
        List<Product> products = productService.findProductsByState(true);
        model.addAttribute("products", products);
        return "product/browse";
    }

    @GetMapping("/register")
    public String showProductRegistration(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("stockStates", EStockState.values());
        model.addAttribute("categories", categoryService.getActiveCategories(true));
        return "product/productRegistrationPage";
    }

    @PostMapping("/register")
    public String registerProduct(@ModelAttribute("product") Product theProduct, Model model) {
        if (Objects.nonNull(theProduct)) {
            theProduct.setManufacturedDate(LocalDate.now());
            theProduct.setActive(true);
            productService.createProduct(theProduct);
            model.addAttribute("message", "Product Saved Successfully");
        } else {
            model.addAttribute("error", "Product Not Saved");
        }
        model.addAttribute("stockStates", EStockState.values());
        model.addAttribute("categories", categoryService.getActiveCategories(true));
        return "product/productRegistrationPage";
    }

    @GetMapping("/update/{id}")
    public String showUpdateForm(@PathVariable UUID id, Model model) {
        Product theProduct = productService.findProductByIdAndState(id, true);
        model.addAttribute("product", theProduct);
        model.addAttribute("stockStates", EStockState.values());
        model.addAttribute("categories", categoryService.getActiveCategories(true));
        return "product/productUpdatePage";
    }

    @PostMapping("/updateProduct")
    public String updateProduct(@ModelAttribute("product") Product theProduct) {
        if (Objects.nonNull(theProduct)) {
            productService.updateProduct(theProduct);
        }
        return "redirect:/product/browse";
    }

    @PostMapping("/delete")
    public String deleteProduct(@RequestParam("id") String id) {
        if (Objects.nonNull(id)) {
            Product theProduct = new Product();
            theProduct.setId(UUID.fromString(id));
            productService.deleteProduct(theProduct);
        }
        return "redirect:/product/browse";
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
            products = productService.findByProductNameContainingAndState(name, true);
        } else if (minPrice != null || maxPrice != null) {
            products = productService.findByPriceBetweenAndState(
                    minPrice != null ? minPrice : 0.0,
                    maxPrice != null ? maxPrice : Double.MAX_VALUE,
                    true);
        } else if (stockState != null) {
            products = productService.findProductsByStockStateAndState(stockState, true);
        } else {
            products = productService.findProductsByState(true);
        }

        model.addAttribute("products", products);
        model.addAttribute("stockStates", EStockState.values());
        model.addAttribute("categories", categoryService.getActiveCategories(true));
        return "product/productList";
    }
}
