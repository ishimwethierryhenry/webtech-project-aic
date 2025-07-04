package rw.ac.auca.ecommerce.core.product.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import rw.ac.auca.ecommerce.core.product.model.ProductCategory;
import rw.ac.auca.ecommerce.core.product.repository.IProductCategoryRepository;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductCategoryServiceImpl implements IProductCategoryService {

    private final IProductCategoryRepository categoryRepository;

    @Override
    public ProductCategory createCategory(ProductCategory category) {
        return categoryRepository.save(category);
    }

    @Override
    public ProductCategory updateCategory(ProductCategory category) {
        ProductCategory existing = getCategoryById(category.getId());
        existing.setName(category.getName());
        existing.setDescription(category.getDescription());
        return categoryRepository.save(existing);
    }

    @Override
    public ProductCategory deleteCategory(UUID categoryId) {
        ProductCategory category = getCategoryById(categoryId);
        category.setActive(false);
        return categoryRepository.save(category);
    }

    @Override
    public ProductCategory getCategoryById(UUID id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ObjectNotFoundException(ProductCategory.class, "Category not found"));
    }

    @Override
    public List<ProductCategory> getAllCategories() {
        return categoryRepository.findAll();
    }

    @Override
    public List<ProductCategory> getActiveCategories(Boolean active) {
        return categoryRepository.findAllByActive(active);
    }
}