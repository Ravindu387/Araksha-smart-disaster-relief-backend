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

    private final ShelterRepository repository;

    @Override
    public ShelterRequestDTO save(ShelterRequestDTO dto) {

        Shelter shelter = Shelter.builder()
                .name(dto.getName())
                .address(dto.getAddress())
                .capacity(dto.getCapacity())
                .occupied(dto.getOccupied())
                .status(calculateStatus(dto.getCapacity(), dto.getOccupied()))
                .amenities(dto.getAmenities())
                .latitude(dto.getLatitude())
                .longitude(dto.getLongitude())
                .build();

        repository.save(shelter);

        return toDto(repository.findById(shelter.getId()).orElse(shelter));
    }

    @Override
    public List<ShelterRequestDTO> getAll() {
        return repository.findAll().stream().map(this::toDto).toList();
    }

    @Override
    public ShelterRequestDTO update(Integer id, ShelterRequestDTO dto) {

        Shelter shelter = repository.findById(id).orElseThrow();

        shelter.setName(dto.getName());
        shelter.setAddress(dto.getAddress());
        shelter.setCapacity(dto.getCapacity());
        shelter.setOccupied(dto.getOccupied());
        shelter.setAmenities(dto.getAmenities());
        shelter.setLatitude(dto.getLatitude());
        shelter.setLongitude(dto.getLongitude());
        shelter.setStatus(calculateStatus(dto.getCapacity(), dto.getOccupied()));

        repository.save(shelter);

        return toDto(repository.findById(id).orElse(shelter));
    }

    @Override
    public void delete(Integer id) {
        repository.deleteById(id);
    }

    @Override
    public ShelterRequestDTO getById(Integer id) {
        Shelter shelter = repository.findById(id).orElseThrow();
        return toDto(shelter);
    }

    private ShelterRequestDTO toDto(Shelter shelter) {

        ShelterRequestDTO dto = new ShelterRequestDTO();

        dto.setId(shelter.getId());
        dto.setName(shelter.getName());
        dto.setAddress(shelter.getAddress());
        dto.setCapacity(shelter.getCapacity());
        dto.setOccupied(shelter.getOccupied());
        dto.setStatus(shelter.getStatus());
        dto.setAmenities(shelter.getAmenities());
        dto.setLatitude(shelter.getLatitude());
        dto.setLongitude(shelter.getLongitude());
        dto.setLastUpdated(shelter.getLastUpdated());

        return dto;
    }

    private String calculateStatus(int capacity, int occupied) {

        double percentage = ((double) occupied / capacity) * 100;

        if (percentage >= 100)
            return "Full";

        if (percentage >= 80)
            return "Limited";

        return "Available";
    }
}