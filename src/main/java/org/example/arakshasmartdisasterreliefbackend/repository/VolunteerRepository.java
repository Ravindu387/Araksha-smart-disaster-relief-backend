package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long>,
        JpaSpecificationExecutor<Volunteer> {

    long countByStatus(String status);

    long countByStatusIgnoreCase(String status);

    List<Volunteer> findTop5ByOrderByTasksDesc();

    java.util.Optional<Volunteer> findByName(String name);
}