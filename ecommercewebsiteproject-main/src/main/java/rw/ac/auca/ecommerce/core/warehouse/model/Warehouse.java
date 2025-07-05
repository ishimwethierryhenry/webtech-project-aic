

package rw.ac.auca.ecommerce.core.warehouse.model;

import jakarta.persistence.*;
import lombok.*;
import rw.ac.auca.ecommerce.core.product.model.Product;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Warehouse {

    @Id
    @GeneratedValue
    private UUID id;

    private String name;

    private String location;

    @ElementCollection
    @CollectionTable(name = "warehouse_stock", joinColumns = @JoinColumn(name = "warehouse_id"))
    @MapKeyJoinColumn(name = "product_id")
    @Column(name = "quantity")
    private Map<Product, Integer> productStock = new HashMap<>();
}
