package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.EmergencyRequestRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyRequestResponse;
import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequest;
import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyNeed;
import org.example.arakshasmartdisasterreliefbackend.entity.Notification;
import org.example.arakshasmartdisasterreliefbackend.repository.EmergencyRequestRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.NotificationRepository;
import org.example.arakshasmartdisasterreliefbackend.service.EmergencyRequestService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmergencyRequestServiceImpl implements EmergencyRequestService {

    private final EmergencyRequestRepository repository;
    private final NotificationRepository notificationRepository;

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

        // Auto-create notification
        String severity = "Critical".equals(saved.getPriority()) ? "critical"
                : "High".equals(saved.getPriority()) ? "high" : "info";
        String badge = "Critical".equals(saved.getPriority()) ? "Critical"
                : "High".equals(saved.getPriority()) ? "High" : "Info";
        Notification n = new Notification();
        n.setCategory("alerts");
        n.setSeverity(severity);
        n.setTitle("New Emergency: " + saved.getEmergencyType() + " — " + saved.getLocation());
        n.setBadge(badge);
        n.setDescription(saved.getCitizenName() + " reported a " + saved.getEmergencyType()
                + " emergency at " + saved.getLocation() + ". Request ID: " + saved.getRequestId()
                + ". Priority: " + saved.getPriority() + ".");
        n.setTime("Just now");
        n.setRead(false);
        notificationRepository.save(n);

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
        List<String> resources = new java.util.ArrayList<>();
        if (emergencyRequest.getNeeds() != null) {
            for (EmergencyNeed need : emergencyRequest.getNeeds()) {
                if (need.getNeed() != null) {
                    resources.add(need.getNeed().getName());
                }
            }
        }

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
                .resources(resources)
                .build();
    }
}