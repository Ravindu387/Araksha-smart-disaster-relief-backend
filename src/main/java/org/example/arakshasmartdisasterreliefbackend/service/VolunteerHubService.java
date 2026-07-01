package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerHubRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.VolunteerHubResponse;

import java.util.List;

public interface VolunteerHubService {
    VolunteerHubResponse save(VolunteerHubRequest request);


    VolunteerHubResponse getById(Long id);


    List<VolunteerHubResponse> getAll();


    VolunteerHubResponse getByEmail(String email);


    void delete(Long id);
}
