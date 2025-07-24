package rw.ac.auca.ecommerce.core.product.service;

import lombok.RequiredArgsConstructor;
import org.hibernate.ObjectNotFoundException;
import org.springframework.stereotype.Service;
import rw.ac.auca.ecommerce.core.product.model.Product;
import rw.ac.auca.ecommerce.core.product.repository.IProductRepository;
import rw.ac.auca.ecommerce.core.util.product.EStockState;

import java.util.List;
import java.util.UUID;

/**
 * The class ProductServiceImpl.
 *
 * @author Jeremie Ukundwa Tuyisenge
 * @version 1.0
 */
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements IProductService{

    private final IProductRepository productRepository;

    @Override
    public Product createProduct(Product theProduct) {
        return productRepository.save(theProduct);
    }
    @Override
    public Product updateProduct(Product theProduct) {
        Product found = findProductByIdAndState(theProduct.getId(), Boolean.TRUE);
        if (found != null) {
            found.setProductName(theProduct.getProductName());
            found.setDescription(theProduct.getDescription());
            found.setPrice(theProduct.getPrice());
            found.setManufacturedDate(theProduct.getManufacturedDate());
            found.setExpirationDate(theProduct.getExpirationDate());
            found.setStockState(theProduct.getStockState());
            return productRepository.save(found);
        }
        throw new ObjectNotFoundException(Product.class, "Product not found");
    }

    @Override
    public Product deleteProduct(Product theProduct) {
        Product found = findProductByIdAndState(theProduct.getId(), Boolean.TRUE);
        if (found != null) {
            found.setActive(Boolean.FALSE);
            return productRepository.save(found);
        }
        throw new ObjectNotFoundException(Product.class, "Product not found");
    }
    @Override
    public List<Product> findByProductNameContainingAndState(String name, Boolean active) {
        return productRepository.findByProductNameContainingIgnoreCaseAndActive(name, active);
    }

    @Override
    public List<Product> findByPriceBetweenAndState(Double minPrice, Double maxPrice, Boolean active) {
        return productRepository.findByPriceBetweenAndActive(minPrice, maxPrice, active);
    }
    @Override
    public List<Product> searchByNameOrCategory(String query) {
        return productRepository.findByProductNameContainingIgnoreCaseOrCategory_NameContainingIgnoreCase(query, query);
    }




    @Override
    public Product findProductByIdAndState(UUID id, Boolean active) {
        Product theProduct = productRepository.findByIdAndActive(id , active)
                .orElseThrow(() -> new ObjectNotFoundException(Product.class , "Product not Found"));
        return theProduct;
    }

    @Override
    public List<Product> findProductsByState(Boolean active) {
        return productRepository.findAllByActive(active);
    }

    @Override
    public List<Product> findProductsByStockStateAndState(EStockState stockState, Boolean active) {
        return productRepository.findAllByStockStateAndActive(stockState,active);
    }

    @Override
    public List<Product> findProductsByStockStatesAndState(List<EStockState> stockStates, Boolean active) {
        return productRepository.findALlByStockStateInAndActive(stockStates,active);
    }
    @Override
    public List<Product> getAllProducts() {
        return productRepository.findAll(); // or your repo method
    }
    @Override
    public List<Product> findAll() {
        return productRepository.findAll(); // assuming you have a repository
    }

}
