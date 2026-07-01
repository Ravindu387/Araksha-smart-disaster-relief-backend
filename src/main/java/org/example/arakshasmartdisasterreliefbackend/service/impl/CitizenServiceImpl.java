package org.example.arakshasmartdisasterreliefbackend.service.impl;

import org.example.arakshasmartdisasterreliefbackend.entity.Citizen;
import org.example.arakshasmartdisasterreliefbackend.repository.CitizenRepository;
import org.example.arakshasmartdisasterreliefbackend.service.CitizenService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CitizenServiceImpl implements CitizenService {

    private final CitizenRepository citizenRepository;

    public CitizenServiceImpl(CitizenRepository citizenRepository) {
        this.citizenRepository = citizenRepository;
    }

    @Override
    public Citizen saveCitizen(Citizen citizen) {

        if (citizenRepository.existsByEmail(citizen.getEmail())) {
            throw new RuntimeException("Email already exists.");
        }

        return citizenRepository.save(citizen);
    }

    @Override
    public List<Citizen> getAllCitizens() {
        return citizenRepository.findAll();
    }

    @Override
    public Optional<Citizen> getCitizenById(Long id) {
        return citizenRepository.findById(id);
    }

    @Override
    public Optional<Citizen> getCitizenByEmail(String email) {
        return citizenRepository.findByEmail(email);
    }

    @Override
    public Citizen updateCitizen(Long id, Citizen citizen) {

        Citizen existing = citizenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Citizen not found"));

        existing.setFullName(citizen.getFullName());
        existing.setEmail(citizen.getEmail());
        existing.setPhoneNumber(citizen.getPhoneNumber());
        existing.setAddress(citizen.getAddress());
        existing.setPassword(citizen.getPassword());

        return citizenRepository.save(existing);
    }

    @Override
    public void deleteCitizen(Long id) {

        citizenRepository.deleteById(id);

    }

}