package org.example.arakshasmartdisasterreliefbackend.service;


import org.example.arakshasmartdisasterreliefbackend.entity.Inventory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InventoryService {

    List<Inventory> getAllInventory();

    Inventory getInventoryById(Long id);

    Inventory saveInventory(Inventory inventory);

    Inventory updateInventory(Long id, Inventory inventory);

    void deleteInventory(Long id);

    /**
     * Server-side search with optional filters and pagination.
     *
     * @param keyword     partial match on name
     * @param category    exact match on category
     * @param stockStatus "available" | "low" | "out"
     * @param pageable    page, size, sort
     * @return paginated result
     */
    Page<Inventory> searchInventory(
            String keyword,
            String category,
            String stockStatus,
            Pageable pageable);

}
