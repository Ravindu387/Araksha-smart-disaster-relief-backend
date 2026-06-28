package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long> {

}