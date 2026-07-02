package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.VolunteerResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface VolunteerService {

    VolunteerResponse createVolunteer(VolunteerRequest request);

    List<VolunteerResponse> getAllVolunteers();

    VolunteerResponse getVolunteerById(Long id);

    VolunteerResponse updateVolunteer(Long id, VolunteerRequest request);

    void deleteVolunteer(Long id);

    /**
     * Server-side search with optional filters and pagination.
     *
     * @param keyword  searches name, phone, location
     * @param status   filter by status
     * @param district filter by location (partial)
     * @param skill    filter by any skill (partial)
     * @param pageable page, size, sort
     * @return paginated result
     */
    Page<VolunteerResponse> searchVolunteers(
            String keyword,
            String status,
            String district,
            String skill,
            Pageable pageable);
}