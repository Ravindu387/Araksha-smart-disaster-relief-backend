package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.SchedulerJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SchedulerJobRepository extends JpaRepository<SchedulerJob, Long> {
    Optional<SchedulerJob> findByJobKey(String jobKey);
}
