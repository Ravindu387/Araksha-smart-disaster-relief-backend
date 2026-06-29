package org.example.arakshasmartdisasterreliefbackend.service;


import org.example.arakshasmartdisasterreliefbackend.entity.Inventory;

import java.util.List;

public interface InventoryService {

    List<Inventory> getAllInventory();

    Inventory getInventoryById(Long id);

    Inventory saveInventory(Inventory inventory);

    Inventory updateInventory(Long id, Inventory inventory);

    void deleteInventory(Long id);

}


