package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.ShelterRequestDTO;
import org.example.arakshasmartdisasterreliefbackend.entity.Shelter;

import java.util.List;

public interface ShelterService {
    Shelter registerShelter(ShelterRequestDTO dto);
    List<Shelter> getAllShelters();
    List<Shelter> searchShelters(String keyword);
    List<Shelter> getSheltersByStatus(String status);
    void deleteShelter(Integer id);
    Shelter updateShelter(Integer id, ShelterRequestDTO dto);
}