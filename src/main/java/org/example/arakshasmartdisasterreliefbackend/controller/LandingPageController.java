package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.response.LandingStatsDTO;
import org.example.arakshasmartdisasterreliefbackend.repository.EmergencyRequestRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.ShelterRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/landing")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class LandingPageController {

    private final EmergencyRequestRepository emergencyRequestRepository;
    private final VolunteerRepository volunteerRepository;
    private final ShelterRepository shelterRepository;

    @GetMapping("/stats")
    public LandingStatsDTO getLandingStats() {
        long activeRequests = emergencyRequestRepository.countActiveRequests();
        long criticalIncidents = emergencyRequestRepository.countActiveRequestsByPriority("Critical");
        long inProgressIncidents = emergencyRequestRepository.countByStatus("In Progress");
        long resolvedIncidents = emergencyRequestRepository.countResolvedRequests();
        long activeVolunteers = volunteerRepository.count();
        long sheltersCount = shelterRepository.count();

        return LandingStatsDTO.builder()
                .activeEmergencyRequests(activeRequests)
                .criticalIncidents(criticalIncidents)
                .inProgressIncidents(inProgressIncidents)
                .resolvedIncidents(resolvedIncidents)
                .volunteersActive(activeVolunteers)
                .reliefSheltersCount(sheltersCount)
                .build();
    }
}
