package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerResponse;

import java.util.List;

public interface VolunteerService {

    VolunteerResponse createVolunteer(VolunteerRequest request);

    List<VolunteerResponse> getAllVolunteers();

    VolunteerResponse getVolunteerById(Long id);

    VolunteerResponse updateVolunteer(Long id, VolunteerRequest request);

    void deleteVolunteer(Long id);
}