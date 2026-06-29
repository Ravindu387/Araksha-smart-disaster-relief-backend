package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.VolunteerHub;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VolunteerHubRepository extends JpaRepository<VolunteerHub, Long> {


    Optional<VolunteerHub> findByVolunteerCode(String volunteerCode);


    Optional<VolunteerHub> findByEmail(String email);


    Optional<VolunteerHub> findByPhone(String phone);
}
