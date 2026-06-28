package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.ShelterRequestDTO;
import org.example.arakshasmartdisasterreliefbackend.entity.Shelter;
import org.example.arakshasmartdisasterreliefbackend.repository.ShelterRepository;
import org.example.arakshasmartdisasterreliefbackend.service.ShelterService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelterServiceImpl implements ShelterService {
    private final ShelterRepository shelterRepository;
    @Override
    public Shelter registerShelter(ShelterRequestDTO dto) {
        Shelter shelter = new Shelter();

        shelter.setName(dto.getShelterName());
        shelter.setAddress(dto.getAddress());

        shelter.setTotalCapacity(dto.getTotalCapacity());
        shelter.setOccupiedBeds(dto.getOccupiedBeds());


        double percentage =
                (dto.getOccupiedBeds() * 100.0) / dto.getTotalCapacity();

        if (percentage >= 100) {
            shelter.setStatus("Full");
        } else if (percentage >= 80) {
            shelter.setStatus("Limited");
        } else {
            shelter.setStatus("Available");
        }

        shelter.setLatitude(dto.getLatitude());
        shelter.setLongitude(dto.getLongitude());

        shelter.setWifi(dto.getWifi());
        shelter.setElectricity(
                dto.getPower());
        shelter.setWater(dto.getWater());

        shelter.setLastUpdated(LocalDateTime.now());

        return shelterRepository.save(shelter);
    }

    @Override
    public List<Shelter> getAllShelters() {
        return shelterRepository.findAll();
    }

    @Override
    public List<Shelter> searchShelters(String keyword) {
        return shelterRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Shelter> getSheltersByStatus(String status) {
        return shelterRepository.findByStatus(status);
    }

    @Override
    public void deleteShelter(Long id) {
        shelterRepository.deleteById(id);
    }
}
