package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerResponse;
import org.example.arakshasmartdisasterreliefbackend.entity.Volunteer;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerRepository;
import org.example.arakshasmartdisasterreliefbackend.service.VolunteerService;

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

        Volunteer updatedVolunteer = repository.save(volunteer);

        return mapToResponse(updatedVolunteer);
    }

    @Override
    public void deleteVolunteer(Long id) {

        Volunteer volunteer = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Volunteer not found with ID : " + id));

        repository.delete(volunteer);
    }

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
                .build();
    }
}