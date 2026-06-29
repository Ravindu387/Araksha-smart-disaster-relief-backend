package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.EmergencyRequestRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyRequestResponse;
import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequest;
import org.example.arakshasmartdisasterreliefbackend.repository.EmergencyRequestRepository;
import org.example.arakshasmartdisasterreliefbackend.service.EmergencyRequestService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmergencyRequestServiceImpl implements EmergencyRequestService {

    private final EmergencyRequestRepository repository;

    @Override
    public EmergencyRequestResponse createEmergencyRequest(EmergencyRequestRequest request) {

        EmergencyRequest emergencyRequest = EmergencyRequest.builder()
                .requestId(request.getRequestId())
                .citizenName(request.getCitizenName())
                .emergencyType(request.getEmergencyType())
                .priority(request.getPriority())
                .status(request.getStatus())
                .location(request.getLocation())
                .assignedVolunteer(request.getAssignedVolunteer())
                .requestTime(LocalDateTime.now())
                .build();

        EmergencyRequest saved = repository.save(emergencyRequest);

        return mapToResponse(saved);
    }

    @Override
    public List<EmergencyRequestResponse> getAllEmergencyRequests() {

        return repository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    public EmergencyRequestResponse getEmergencyRequestById(Long id) {

        EmergencyRequest emergencyRequest = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency Request not found"));

        return mapToResponse(emergencyRequest);
    }

    @Override
    public EmergencyRequestResponse updateEmergencyRequest(Long id, EmergencyRequestRequest request) {

        EmergencyRequest emergencyRequest = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency Request not found"));

        emergencyRequest.setRequestId(request.getRequestId());
        emergencyRequest.setCitizenName(request.getCitizenName());
        emergencyRequest.setEmergencyType(request.getEmergencyType());
        emergencyRequest.setPriority(request.getPriority());
        emergencyRequest.setStatus(request.getStatus());
        emergencyRequest.setLocation(request.getLocation());
        emergencyRequest.setAssignedVolunteer(request.getAssignedVolunteer());

        EmergencyRequest updated = repository.save(emergencyRequest);

        return mapToResponse(updated);
    }

    @Override
    public void deleteEmergencyRequest(Long id) {

        EmergencyRequest emergencyRequest = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency Request not found"));

        repository.delete(emergencyRequest);
    }

    private EmergencyRequestResponse mapToResponse(EmergencyRequest emergencyRequest) {

        return EmergencyRequestResponse.builder()
                .id(emergencyRequest.getId())
                .requestId(emergencyRequest.getRequestId())
                .citizenName(emergencyRequest.getCitizenName())
                .emergencyType(emergencyRequest.getEmergencyType())
                .priority(emergencyRequest.getPriority())
                .status(emergencyRequest.getStatus())
                .location(emergencyRequest.getLocation())
                .assignedVolunteer(emergencyRequest.getAssignedVolunteer())
                .requestTime(emergencyRequest.getRequestTime())
                .build();
    }
}