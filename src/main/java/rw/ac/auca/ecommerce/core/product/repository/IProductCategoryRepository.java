package rw.ac.auca.ecommerce.core.product.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rw.ac.auca.ecommerce.core.product.model.ProductCategory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IProductCategoryRepository extends JpaRepository<ProductCategory, UUID> {
    Optional<ProductCategory> findByName(String name);
    List<ProductCategory> findAllByActive(Boolean active);
}