package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.Shelter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ShelterRepository extends JpaRepository<Shelter, Integer> {

    List<Shelter> findByStatus(String status);

    List<Shelter> findByNameContainingIgnoreCase(String keyword);

    @Query("""
        SELECT AVG(
            (CAST(s.occupied AS double) /
             CAST(s.capacity AS double)) * 100
        )
        FROM Shelter s
        WHERE s.capacity > 0
        """)
    Double calculateAverageOccupancy();

}