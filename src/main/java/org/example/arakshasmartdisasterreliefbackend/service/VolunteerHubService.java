package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerHubRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.VolunteerHubResponse;

import java.util.List;

public interface VolunteerHubService {
    VolunteerHubResponse save(VolunteerHubRequest request);


    VolunteerHubResponse getById(Long id);


    List<VolunteerHubResponse> getAll();


    VolunteerHubResponse getByEmail(String email);


    VolunteerHubResponse updateLocation(Long id, Double latitude, Double longitude);


    VolunteerHubResponse updateStatus(Long id, String status);


    void delete(Long id);
}
