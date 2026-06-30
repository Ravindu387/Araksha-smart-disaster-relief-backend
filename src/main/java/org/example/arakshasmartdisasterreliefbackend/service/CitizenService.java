package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.entity.Citizen;

import java.util.List;
import java.util.Optional;

public interface CitizenService {

    Citizen saveCitizen(Citizen citizen);

    List<Citizen> getAllCitizens();

    Optional<Citizen> getCitizenById(Long id);

    Citizen updateCitizen(Long id, Citizen citizen);

    void deleteCitizen(Long id);

}