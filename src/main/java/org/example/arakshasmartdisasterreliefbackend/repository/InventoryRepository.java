package org.example.arakshasmartdisasterreliefbackend.repository;


import org.example.arakshasmartdisasterreliefbackend.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InventoryRepository extends JpaRepository<Inventory, Long>,
        JpaSpecificationExecutor<Inventory> {

}
