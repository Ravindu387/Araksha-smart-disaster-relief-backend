package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequest;
import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequestVolunteers;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmergencyRequestVolunteersRepository extends JpaRepository<EmergencyRequestVolunteers,Long> {


    Optional<EmergencyRequestVolunteers> findByRequestCode(String requestCode);



    List<EmergencyRequestVolunteers> findByStatus(String status);



    List<EmergencyRequestVolunteers> findByPriority(String priority);
}
