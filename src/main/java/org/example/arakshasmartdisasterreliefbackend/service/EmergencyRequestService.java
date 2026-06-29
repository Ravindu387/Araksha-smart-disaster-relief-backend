package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.EmergencyRequestRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyRequestResponse;

import java.util.List;

public interface EmergencyRequestService {

    EmergencyRequestResponse createEmergencyRequest(EmergencyRequestRequest request);

    List<EmergencyRequestResponse> getAllEmergencyRequests();

    EmergencyRequestResponse getEmergencyRequestById(Long id);

    EmergencyRequestResponse updateEmergencyRequest(Long id, EmergencyRequestRequest request);

    void deleteEmergencyRequest(Long id);

}