package rw.ac.auca.ecommerce.core.product.service;

import rw.ac.auca.ecommerce.core.product.model.ProductCategory;

import java.util.List;
import java.util.UUID;

public interface IProductCategoryService {
    ProductCategory createCategory(ProductCategory category);
    ProductCategory updateCategory(ProductCategory category);
    ProductCategory deleteCategory(UUID categoryId);
    ProductCategory getCategoryById(UUID id);
    List<ProductCategory> getAllCategories();
    List<ProductCategory> getActiveCategories(Boolean active);
}