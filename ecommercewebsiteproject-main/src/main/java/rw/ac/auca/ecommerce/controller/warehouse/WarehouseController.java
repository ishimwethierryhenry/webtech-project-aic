package rw.ac.auca.ecommerce.controller.warehouse;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import rw.ac.auca.ecommerce.core.order.model.OrderItem;
import rw.ac.auca.ecommerce.core.product.model.Product;
import rw.ac.auca.ecommerce.core.product.service.IProductService;
import rw.ac.auca.ecommerce.core.warehouse.model.Warehouse;
import rw.ac.auca.ecommerce.core.warehouse.service.IWarehouseService;

import java.util.*;

@Controller
@RequestMapping("/warehouse")
@RequiredArgsConstructor
public class WarehouseController {

    private final IWarehouseService warehouseService;
    private final IProductService productService;

    // ✅ Only this method for /list — includes stock sum logic
    @GetMapping("/list")
    public String listWarehouses(Model model) {
        List<Warehouse> warehouses = warehouseService.getAllWarehouses();

        Map<UUID, Integer> warehouseStockSums = new HashMap<>();
        for (Warehouse warehouse : warehouses) {
            int sum = warehouse.getProductStock() != null
                    ? warehouse.getProductStock().values().stream().mapToInt(Integer::intValue).sum()
                    : 0;
            warehouseStockSums.put(warehouse.getId(), sum);
        }

        model.addAttribute("warehouses", warehouses);
        model.addAttribute("stockSums", warehouseStockSums);
        return "warehouse/list";
    }

    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("warehouse", new Warehouse());
        return "warehouse/add";
    }

    @PostMapping("/add")
    public String addWarehouse(@ModelAttribute Warehouse warehouse) {
        warehouseService.createWarehouse(warehouse);
        return "redirect:/warehouse/list";
    }

    @GetMapping("/{id}")
    public String viewWarehouse(@PathVariable UUID id, Model model) {
        Warehouse warehouse = warehouseService.getWarehouseById(id);
        if (warehouse == null) {
            return "redirect:/warehouse/list?error=notfound";
        }
        model.addAttribute("warehouse", warehouse);
        return "warehouse/details";
    }

    @GetMapping("/assign-stock")
    public String showAssignStockForm(Model model) {
        model.addAttribute("warehouses", warehouseService.getAllWarehouses());
        model.addAttribute("products", productService.findProductsByState(true));
        return "warehouse/assignStock";
    }

    @PostMapping("/assign-stock")
    public String assignStockToWarehouse(
            @RequestParam UUID warehouseId,
            @RequestParam UUID productId,
            @RequestParam Integer quantity
    ) {
        Warehouse warehouse = warehouseService.getWarehouseById(warehouseId);
        Product product = productService.findProductByIdAndState(productId, true);

        if (warehouse != null && product != null && quantity > 0) {
            Map<Product, Integer> stock = warehouse.getProductStock();
            stock.put(product, stock.getOrDefault(product, 0) + quantity);
            warehouse.setProductStock(stock);
            warehouseService.createWarehouse(warehouse);
        }

        return "redirect:/warehouse/list";
    }

    // ✅ Utility to deduct stock when an order is placed
    public void deductStockFromWarehouse(List<OrderItem> orderItems) {
        for (OrderItem item : orderItems) {
            Product product = item.getProduct();
            int quantityOrdered = item.getQuantity();

            for (Warehouse warehouse : warehouseService.getAllWarehouses()) {
                Map<Product, Integer> stock = warehouse.getProductStock();
                if (stock.containsKey(product) && stock.get(product) >= quantityOrdered) {
                    stock.put(product, stock.get(product) - quantityOrdered);
                    warehouse.setProductStock(stock);
                    warehouseService.createWarehouse(warehouse);
                    break;
                }
            }
        }
    }
}
