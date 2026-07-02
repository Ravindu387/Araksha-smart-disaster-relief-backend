package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.MutualAidItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MutualAidRepository extends JpaRepository<MutualAidItem, Long> {
    List<MutualAidItem> findByType(String type);
}
