package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface EmergencyRequestRepository extends JpaRepository<EmergencyRequest, Long>,
        JpaSpecificationExecutor<EmergencyRequest> {

    long countByStatus(String status);

    Optional<EmergencyRequest> findByRequestId(String requestId);

    @Query("SELECT COUNT(e) FROM EmergencyRequest e WHERE e.status NOT IN ('Completed', 'Resolved')")
    long countActiveRequests();

    @Query("SELECT COUNT(e) FROM EmergencyRequest e WHERE e.priority = :priority AND e.status NOT IN ('Completed', 'Resolved')")
    long countActiveRequestsByPriority(@Param("priority") String priority);

    @Query("SELECT COUNT(e) FROM EmergencyRequest e WHERE e.status IN ('Completed', 'Resolved')")
    long countResolvedRequests();

}