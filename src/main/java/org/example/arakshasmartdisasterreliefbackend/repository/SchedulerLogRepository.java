package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.SchedulerLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchedulerLogRepository extends JpaRepository<SchedulerLog, Long> {
    List<SchedulerLog> findAllByOrderByExecutionTimeDesc();
}
