package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.VolunteerResponse;
import org.example.arakshasmartdisasterreliefbackend.entity.Volunteer;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerRepository;
import org.example.arakshasmartdisasterreliefbackend.service.VolunteerService;
import org.example.arakshasmartdisasterreliefbackend.specification.VolunteerSpecification;

@Service
@RequiredArgsConstructor
public class VolunteerServiceImpl implements VolunteerService {

    private final VolunteerRepository repository;

    @Override
    public VolunteerResponse createVolunteer(VolunteerRequest request) {

        Volunteer volunteer = Volunteer.builder()
                .name(request.getName())
                .location(request.getLocation())
                .skills(request.getSkills())
                .status(request.getStatus())
                .rating(request.getRating())
                .tasks(request.getTasks())
                .phone(request.getPhone())
                .profilePhotoUrl(request.getProfilePhotoUrl())
                .idVerificationDocUrl(request.getIdVerificationDocUrl())
                .build();

        Volunteer savedVolunteer = repository.save(volunteer);

        return mapToResponse(savedVolunteer);
    }

    @Override
    public List<VolunteerResponse> getAllVolunteers() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public VolunteerResponse getVolunteerById(Long id) {

        Volunteer volunteer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer not found with ID : " + id));

        return mapToResponse(volunteer);
    }

    @Override
    public VolunteerResponse updateVolunteer(Long id, VolunteerRequest request) {

        Volunteer volunteer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer not found with ID : " + id));

        volunteer.setName(request.getName());
        volunteer.setLocation(request.getLocation());
        volunteer.setSkills(request.getSkills());
        volunteer.setStatus(request.getStatus());
        volunteer.setRating(request.getRating());
        volunteer.setTasks(request.getTasks());
        volunteer.setPhone(request.getPhone());
        volunteer.setProfilePhotoUrl(request.getProfilePhotoUrl());
        volunteer.setIdVerificationDocUrl(request.getIdVerificationDocUrl());

        Volunteer updatedVolunteer = repository.save(volunteer);

        return mapToResponse(updatedVolunteer);
    }

    @Override
    public void deleteVolunteer(Long id) {

        Volunteer volunteer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer not found with ID : " + id));

        repository.delete(volunteer);
    }

    // ── Search with server-side pagination ────────────────────────────────────
    @Override
    public Page<VolunteerResponse> searchVolunteers(
            String keyword,
            String status,
            String district,
            String skill,
            Pageable pageable) {

        return repository
                .findAll(VolunteerSpecification.build(keyword, status, district, skill), pageable)
                .map(this::mapToResponse);
    }

    // ── Mapping helper ────────────────────────────────────────────────────────
    private VolunteerResponse mapToResponse(Volunteer volunteer) {

        return VolunteerResponse.builder()
                .id(volunteer.getId())
                .name(volunteer.getName())
                .location(volunteer.getLocation())
                .skills(volunteer.getSkills())
                .status(volunteer.getStatus())
                .rating(volunteer.getRating())
                .tasks(volunteer.getTasks())
                .phone(volunteer.getPhone())
                .profilePhotoUrl(volunteer.getProfilePhotoUrl())
                .idVerificationDocUrl(volunteer.getIdVerificationDocUrl())
                .build();
    }
}