package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VolunteerRepository extends JpaRepository<Volunteer, Long> {

    long countByStatus(String status);

    long countByStatusIgnoreCase(String status);

    List<Volunteer> findTop5ByOrderByTasksDesc();

}