package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.response.AdminDashboardStatsDTO;
import org.example.arakshasmartdisasterreliefbackend.repository.EmergencyRequestRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.ShelterRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerRepository;
import org.example.arakshasmartdisasterreliefbackend.service.AdminDashboardService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdminDashboardServiceImpl implements AdminDashboardService {

    private final EmergencyRequestRepository emergencyRequestRepository;
    private final VolunteerRepository volunteerRepository;
    private final ShelterRepository shelterRepository;

    @Override
    public AdminDashboardStatsDTO getDashboardStats() {

        long totalEmergencyRequests = emergencyRequestRepository.count();

        long activeCases = emergencyRequestRepository.countByStatus("In Progress");

        long volunteersOnline = volunteerRepository.countByStatus("Available");

        Double occupancy = shelterRepository.calculateAverageOccupancy();

        if (occupancy == null) {
            occupancy = 0.0;
        }

        return AdminDashboardStatsDTO.builder()
                .emergencyRequests(totalEmergencyRequests)
                .activeCases(activeCases)
                .volunteersOnline(volunteersOnline)
                .shelterOccupancy(occupancy)
                .build();
    }
}