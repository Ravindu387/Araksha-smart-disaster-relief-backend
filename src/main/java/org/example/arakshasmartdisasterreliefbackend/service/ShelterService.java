package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.ShelterRequestDTO;

import java.util.List;

public interface ShelterService {

    ShelterRequestDTO save(ShelterRequestDTO dto);

    List<ShelterRequestDTO> getAll();

    void delete(Integer id);

    ShelterRequestDTO update(Integer id, ShelterRequestDTO dto);

    ShelterRequestDTO getById(Integer id);
}