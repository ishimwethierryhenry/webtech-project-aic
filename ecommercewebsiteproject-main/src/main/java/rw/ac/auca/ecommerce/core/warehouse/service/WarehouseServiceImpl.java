package rw.ac.auca.ecommerce.core.warehouse.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import rw.ac.auca.ecommerce.core.warehouse.model.Warehouse;
import rw.ac.auca.ecommerce.core.warehouse.repository.WarehouseRepository;
import rw.ac.auca.ecommerce.core.warehouse.service.IWarehouseService;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements IWarehouseService {

    private final WarehouseRepository warehouseRepository;

    @Override
    public List<Warehouse> getAllWarehouses() {
        return warehouseRepository.findAll();
    }

    @Override
    public Warehouse getWarehouseById(UUID id) {
        return warehouseRepository.findById(id).orElse(null);
    }

    @Override
    public Warehouse createWarehouse(Warehouse warehouse) {
        return warehouseRepository.save(warehouse);
    }
}
