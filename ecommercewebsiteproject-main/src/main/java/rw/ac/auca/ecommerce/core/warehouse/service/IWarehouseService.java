package rw.ac.auca.ecommerce.core.warehouse.service;

import rw.ac.auca.ecommerce.core.warehouse.model.Warehouse;

import java.util.List;
import java.util.UUID;

public interface IWarehouseService {
    List<Warehouse> getAllWarehouses();
    Warehouse getWarehouseById(UUID id);
    Warehouse createWarehouse(Warehouse warehouse);
}
