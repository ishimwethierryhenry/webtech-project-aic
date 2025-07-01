package rw.ac.auca.ecommerce.controller.product;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.ecommerce.core.product.model.ProductCategory;
import rw.ac.auca.ecommerce.core.product.service.IProductCategoryService;

import java.util.UUID;

@Controller
@RequestMapping("/category")
@RequiredArgsConstructor
public class ProductCategoryController {

    private final IProductCategoryService categoryService;

    @GetMapping("/")
    public String getAllCategories(Model model) {
        model.addAttribute("categories", categoryService.getActiveCategories(true));
        return "category/categoryList";
    }

    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("category", new ProductCategory());
        return "category/categoryForm";
    }

    @PostMapping("/save")
    public String saveCategory(@ModelAttribute ProductCategory category, Model model) {
        try {
            if (category.getId() == null) {
                categoryService.createCategory(category);
                model.addAttribute("message", "Category created successfully!");
            } else {
                categoryService.updateCategory(category);
                model.addAttribute("message", "Category updated successfully!");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Error saving category: " + e.getMessage());
        }
        return "category/categoryForm";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));
        return "category/categoryForm";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable UUID id) {
        categoryService.deleteCategory(id);
        return "redirect:/category/";
    }
}