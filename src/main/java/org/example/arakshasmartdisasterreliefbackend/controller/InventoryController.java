package org.example.arakshasmartdisasterreliefbackend.controller;

import org.example.arakshasmartdisasterreliefbackend.entity.Inventory;
import org.example.arakshasmartdisasterreliefbackend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    // ── Get All ───────────────────────────────────────────────────────────────
    @GetMapping
    public List<Inventory> getAllInventory() {
        return inventoryService.getAllInventory();
    }

    // ── Advanced Search / Filter / Paginate ──────────────────────────────────
    /**
     * GET /api/inventory/search
     *
     * Query params (all optional):
     *   keyword     – partial match on name
     *   category    – Food | Water | Medicine | Supplies | Shelter | Equipment | Hygiene
     *   stockStatus – available | low | out
     *   page        – 0-based page number (default 0)
     *   size        – page size (default 10)
     *   sort        – e.g. name,asc | count,desc
     */
    @GetMapping("/search")
    public Page<Inventory> searchInventory(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String stockStatus,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        return inventoryService.searchInventory(keyword, category, stockStatus, pageable);
    }

    // ── Get By ID ─────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public Inventory getInventoryById(@PathVariable Long id) {
        return inventoryService.getInventoryById(id);
    }

    // ── Create ────────────────────────────────────────────────────────────────
    @PostMapping
    public Inventory saveInventory(@RequestBody Inventory inventory) {
        return inventoryService.saveInventory(inventory);
    }

    // ── Update ────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public Inventory updateInventory(@PathVariable Long id,
                                     @RequestBody Inventory inventory) {

        return inventoryService.updateInventory(id, inventory);
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public org.springframework.http.ResponseEntity<Void> deleteInventory(@PathVariable Long id) {

        inventoryService.deleteInventory(id);

        return org.springframework.http.ResponseEntity.ok().build();
    }
}
