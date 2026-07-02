package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.EmergencyRequestRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyRequestResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface EmergencyRequestService {

    EmergencyRequestResponse createEmergencyRequest(EmergencyRequestRequest request);

    List<EmergencyRequestResponse> getAllEmergencyRequests();

    EmergencyRequestResponse getEmergencyRequestById(Long id);

    EmergencyRequestResponse updateEmergencyRequest(Long id, EmergencyRequestRequest request);

    void deleteEmergencyRequest(Long id);

    /**
     * Server-side search with optional filters and pagination.
     *
     * @param keyword      searches requestId, citizenName, location
     * @param status       filter by status
     * @param priority     filter by priority
     * @param disasterType filter by emergencyType
     * @param district     filter by location (partial)
     * @param dateFrom     requestTime >= dateFrom
     * @param dateTo       requestTime <= dateTo (end of day)
     * @param pageable     page, size, sort
     * @return paginated result
     */
    Page<EmergencyRequestResponse> searchEmergencyRequests(
            String keyword,
            String status,
            String priority,
            String disasterType,
            String district,
            LocalDate dateFrom,
            LocalDate dateTo,
            Pageable pageable);

}