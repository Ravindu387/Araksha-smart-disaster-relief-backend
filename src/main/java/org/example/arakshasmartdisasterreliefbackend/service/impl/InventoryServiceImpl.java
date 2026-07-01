package org.example.arakshasmartdisasterreliefbackend.service.impl;



import org.example.arakshasmartdisasterreliefbackend.entity.Inventory;
import org.example.arakshasmartdisasterreliefbackend.entity.Notification;
import org.example.arakshasmartdisasterreliefbackend.repository.InventoryRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.NotificationRepository;
import org.example.arakshasmartdisasterreliefbackend.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    private InventoryRepository inventoryRepository;

    @Autowired
    private NotificationRepository notificationRepository;

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
        Inventory saved = inventoryRepository.save(inventory);

        // Auto-create notification
        Notification n = new Notification();
        n.setCategory("inventory");
        n.setSeverity("info");
        n.setTitle("New Inventory Item Added: " + saved.getName());
        n.setBadge("Info");
        n.setDescription(saved.getName() + " added to inventory. Quantity: " + saved.getCount() + " " + saved.getUnit() + ".");
        n.setTime("Just now");
        n.setRead(false);
        notificationRepository.save(n);

        return saved;
    }

    @Override
    public Inventory updateInventory(Long id, Inventory inventory) {

        Inventory existing = inventoryRepository.findById(id).orElse(null);

        if (existing != null) {

            int oldCount = existing.getCount();
            existing.setName(inventory.getName());
            existing.setCategory(inventory.getCategory());
            existing.setCount(inventory.getCount());
            existing.setTotal(inventory.getTotal());
            existing.setUnit(inventory.getUnit());
            existing.setAllocated(inventory.getAllocated());
            existing.setMinStock(inventory.getMinStock());

            Inventory updated = inventoryRepository.save(existing);

            // Auto-create notification for stock change
            String severity = updated.getCount() <= updated.getMinStock() ? "critical" : "info";
            String badge = updated.getCount() <= updated.getMinStock() ? "Critical" : "Info";
            Notification n = new Notification();
            n.setCategory("inventory");
            n.setSeverity(severity);
            n.setTitle("Inventory Updated: " + updated.getName());
            n.setBadge(badge);
            n.setDescription(updated.getName() + " stock changed from " + oldCount + " to " + updated.getCount() + " " + updated.getUnit() + "."
                    + (updated.getCount() <= updated.getMinStock() ? " WARNING: Below minimum threshold of " + updated.getMinStock() + "!" : ""));
            n.setTime("Just now");
            n.setRead(false);
            notificationRepository.save(n);

            return updated;
        }

        return null;
    }

    @Override
    public void deleteInventory(Long id) {
        inventoryRepository.deleteById(id);
    }
}