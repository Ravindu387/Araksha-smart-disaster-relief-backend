package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyNeed;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmergencyNeedRepository extends JpaRepository<EmergencyNeed,Long> {



    List<EmergencyNeed> findByEmergencyRequestId(Long requestId);
}
