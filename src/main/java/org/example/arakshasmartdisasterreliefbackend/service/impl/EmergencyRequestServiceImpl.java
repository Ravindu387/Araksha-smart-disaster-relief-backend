package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arakshasmartdisasterreliefbackend.dto.request.EmergencyRequestRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyRequestResponse;
import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequest;
import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyNeed;
import org.example.arakshasmartdisasterreliefbackend.entity.Notification;
import org.example.arakshasmartdisasterreliefbackend.repository.EmergencyRequestRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.NotificationRepository;
import org.example.arakshasmartdisasterreliefbackend.service.EmergencyRequestService;
import org.example.arakshasmartdisasterreliefbackend.specification.EmergencyRequestSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmergencyRequestServiceImpl implements EmergencyRequestService {

    private final EmergencyRequestRepository repository;
    private final NotificationRepository notificationRepository;
    private final org.example.arakshasmartdisasterreliefbackend.service.SmsService smsService;
    private final org.example.arakshasmartdisasterreliefbackend.service.EmailService emailService;
    private final org.example.arakshasmartdisasterreliefbackend.repository.VolunteerRepository volunteerRepository;
    private final org.example.arakshasmartdisasterreliefbackend.service.GeocodingService geocodingService;

    @Override
    public EmergencyRequestResponse createEmergencyRequest(EmergencyRequestRequest request) {

        Double lat = request.getLatitude();
        Double lng = request.getLongitude();
        if (lat == null || lng == null) {
            try {
                org.example.arakshasmartdisasterreliefbackend.dto.LatLngDTO coords = geocodingService.geocode(request.getLocation());
                lat = coords.getLatitude();
                lng = coords.getLongitude();
            } catch (Exception e) {
                lat = 6.9271;
                lng = 79.8612;
            }
        }

        EmergencyRequest emergencyRequest = EmergencyRequest.builder()
                .requestId(request.getRequestId())
                .citizenName(request.getCitizenName())
                .emergencyType(request.getEmergencyType())
                .priority(request.getPriority())
                .status(request.getStatus())
                .location(request.getLocation())
                .assignedVolunteer(request.getAssignedVolunteer())
                .requestTime(LocalDateTime.now())
                .disasterImageUrl(request.getDisasterImageUrl())
                .documentUrl(request.getDocumentUrl())
                .latitude(lat)
                .longitude(lng)
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

        // Send SMS & Email alerts
        try {
            smsService.sendEmergencyCreatedSms("+94770000000", saved.getRequestId(), saved.getEmergencyType(), saved.getLocation());
            emailService.sendEmergencyConfirmationEmail("citizen@example.com", saved.getCitizenName(), saved.getRequestId(), saved.getEmergencyType(), saved.getLocation());
        } catch (Exception e) {
            log.error("Failed to send automatic emergency creation alerts: {}", e.getMessage());
        }

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

        String oldVol = emergencyRequest.getAssignedVolunteer();
        String newVol = request.getAssignedVolunteer();

        Double lat = request.getLatitude();
        Double lng = request.getLongitude();
        if (lat == null || lng == null) {
            try {
                org.example.arakshasmartdisasterreliefbackend.dto.LatLngDTO coords = geocodingService.geocode(request.getLocation());
                lat = coords.getLatitude();
                lng = coords.getLongitude();
            } catch (Exception e) {
                lat = emergencyRequest.getLatitude();
                lng = emergencyRequest.getLongitude();
            }
        }

        emergencyRequest.setRequestId(request.getRequestId());
        emergencyRequest.setCitizenName(request.getCitizenName());
        emergencyRequest.setEmergencyType(request.getEmergencyType());
        emergencyRequest.setPriority(request.getPriority());
        emergencyRequest.setStatus(request.getStatus());
        emergencyRequest.setLocation(request.getLocation());
        emergencyRequest.setAssignedVolunteer(request.getAssignedVolunteer());
        emergencyRequest.setDisasterImageUrl(request.getDisasterImageUrl());
        emergencyRequest.setDocumentUrl(request.getDocumentUrl());
        emergencyRequest.setLatitude(lat);
        emergencyRequest.setLongitude(lng);

        EmergencyRequest updated = repository.save(emergencyRequest);

        // Send SMS/Email alerts if volunteer is assigned/reassigned
        if (newVol != null && !newVol.trim().isEmpty() && (oldVol == null || !oldVol.equals(newVol))) {
            try {
                volunteerRepository.findByName(newVol).ifPresent(v -> {
                    smsService.sendVolunteerAssignmentSms(v.getPhone(), v.getName(), updated.getRequestId(), updated.getLocation());
                    emailService.sendVolunteerAssignmentEmail("volunteer@example.com", v.getName(), updated.getRequestId(), updated.getLocation(), 
                            "Please proceed immediately to the designated location to assist response efforts. Stay safe.");
                });
            } catch (Exception e) {
                log.error("Failed to send volunteer assignment notification: {}", e.getMessage());
            }
        }

        return mapToResponse(updated);
    }

    @Override
    public void deleteEmergencyRequest(Long id) {

        EmergencyRequest emergencyRequest = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Emergency Request not found"));

        repository.delete(emergencyRequest);
    }

    // ── Search with server-side pagination ────────────────────────────────────
    @Override
    public Page<EmergencyRequestResponse> searchEmergencyRequests(
            String keyword,
            String status,
            String priority,
            String disasterType,
            String district,
            LocalDate dateFrom,
            LocalDate dateTo,
            Pageable pageable) {

        return repository
                .findAll(
                        EmergencyRequestSpecification.build(
                                keyword, status, priority, disasterType, district, dateFrom, dateTo),
                        pageable)
                .map(this::mapToResponse);
    }

    // ── Mapping helper ────────────────────────────────────────────────────────
    private EmergencyRequestResponse mapToResponse(EmergencyRequest emergencyRequest) {
        List<String> resources = new ArrayList<>();
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
                .disasterImageUrl(emergencyRequest.getDisasterImageUrl())
                .documentUrl(emergencyRequest.getDocumentUrl())
                .latitude(emergencyRequest.getLatitude())
                .longitude(emergencyRequest.getLongitude())
                .build();
    }
}