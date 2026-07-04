package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;

import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerHubRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.VolunteerHubResponse;
import org.example.arakshasmartdisasterreliefbackend.entity.VolunteerHub;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerHubRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.UserRepository;
import org.example.arakshasmartdisasterreliefbackend.service.VolunteerHubService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VolunteerHubServiceImpl implements VolunteerHubService {

    private final VolunteerHubRepository repository;
    private final UserRepository userRepository;

    @Override
    public VolunteerHubResponse save(VolunteerHubRequest request){


        VolunteerHub volunteer =
                VolunteerHub.builder()
                        .volunteerCode(request.getVolunteerCode())
                        .name(request.getName())
                        .email(request.getEmail())
                        .phone(request.getPhone())
                        .status(request.getStatus())
                        .available(request.getAvailable())
                        .currentLatitude(request.getCurrentLatitude())
                        .currentLongitude(request.getCurrentLongitude())
                        .build();


        VolunteerHub saved =
                repository.save(volunteer);

        return map(saved);
    }

    @Override
    public VolunteerHubResponse getById(Long id){

        return repository.findById(id)
                .map(this::map)
                .orElseThrow();

    }

    @Override
    public List<VolunteerHubResponse> getAll(){

        return repository.findAll()
                .stream()
                .map(this::map)
                .toList();

    }

    @Override
    public void delete(Long id){

        repository.deleteById(id);

    }

    @Override
    public VolunteerHubResponse getByEmail(String email) {
        return repository.findByEmail(email)
                .map(this::map)
                .orElseGet(() -> {
                    org.example.arakshasmartdisasterreliefbackend.entity.User user = userRepository.findByEmail(email)
                            .orElseThrow(() -> new RuntimeException("Volunteer not found with email: " + email));
                    
                    VolunteerHub volunteer = VolunteerHub.builder()
                            .volunteerCode("VOL-" + String.format("%04d", (int)(Math.random() * 10000)))
                            .name(user.getFirstName() + " " + user.getLastName())
                            .email(email)
                            .phone("+94 77 " + String.format("%07d", (int)(Math.random() * 10000000)))
                            .status("Available")
                            .available(true)
                            .currentLatitude(6.9271)
                            .currentLongitude(79.8612)
                            .build();
                    VolunteerHub saved = repository.save(volunteer);
                    return map(saved);
                });
    }

    private VolunteerHubResponse map(VolunteerHub v){

        return VolunteerHubResponse.builder()
                .id(v.getId())
                .name(v.getName())
                .volunteerCode(v.getVolunteerCode())
                .status(v.getStatus())
                .available(v.getAvailable())
                .email(v.getEmail())
                .phone(v.getPhone())
                .build();

    }

}