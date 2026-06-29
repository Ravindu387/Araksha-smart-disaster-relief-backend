package org.example.arakshasmartdisasterreliefbackend.service.impl;



import org.example.arakshasmartdisasterreliefbackend.entity.Inventory;
import org.example.arakshasmartdisasterreliefbackend.repository.InventoryRepository;
import org.example.arakshasmartdisasterreliefbackend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Override
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    @Override
    public Inventory getInventoryById(Long id) {
        return inventoryRepository.findById(id).orElse(null);
    }

    @Override
    public Inventory saveInventory(Inventory inventory) {
        return inventoryRepository.save(inventory);
    }

    @Override
    public Inventory updateInventory(Long id, Inventory inventory) {

        Inventory existing = inventoryRepository.findById(id).orElse(null);

        if (existing != null) {

            existing.setName(inventory.getName());
            existing.setCategory(inventory.getCategory());
            existing.setCount(inventory.getCount());
            existing.setTotal(inventory.getTotal());
            existing.setUnit(inventory.getUnit());
            existing.setAllocated(inventory.getAllocated());
            existing.setMinStock(inventory.getMinStock());

            return inventoryRepository.save(existing);
        }

        return null;
    }

    @Override
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }
}
