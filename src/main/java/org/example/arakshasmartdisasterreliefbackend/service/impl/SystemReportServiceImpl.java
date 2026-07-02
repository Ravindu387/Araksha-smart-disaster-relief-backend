package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.entity.Inventory;
import org.example.arakshasmartdisasterreliefbackend.entity.ScheduledReport;
import org.example.arakshasmartdisasterreliefbackend.entity.Shelter;
import org.example.arakshasmartdisasterreliefbackend.entity.Volunteer;
import org.example.arakshasmartdisasterreliefbackend.repository.*;
import org.example.arakshasmartdisasterreliefbackend.service.SystemReportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SystemReportServiceImpl implements SystemReportService {

    private final EmergencyRequestRepository requestRepository;
    private final VolunteerRepository volunteerRepository;
    private final ShelterRepository shelterRepository;
    private final InventoryRepository inventoryRepository;
    private final ScheduledReportRepository reportRepository;
    private final org.example.arakshasmartdisasterreliefbackend.service.EmailService emailService;

    @Override
    public ScheduledReport generateAndSaveDailySummary() {
        // 1. Total emergencies (emergency requests count)
        int totalEmergencies = (int) requestRepository.count();

        // 2. Active volunteers (Available or On Duty)
        List<Volunteer> allVolunteers = volunteerRepository.findAll();
        int activeVolunteers = (int) allVolunteers.stream()
                .filter(v -> "Available".equalsIgnoreCase(v.getStatus()) || "On Duty".equalsIgnoreCase(v.getStatus()))
                .count();

        // 3. Shelter occupancy (occupied / capacity percentage)
        List<Shelter> allShelters = shelterRepository.findAll();
        int totalCapacity = allShelters.stream().mapToInt(Shelter::getCapacity).sum();
        int totalOccupied = allShelters.stream().mapToInt(Shelter::getOccupied).sum();
        double occupancyPercentage = totalCapacity > 0 ? ((double) totalOccupied / totalCapacity) * 100.0 : 0.0;

        // 4. Low stock resources (count < minStock)
        List<Inventory> allInventory = inventoryRepository.findAll();
        int lowStockResources = (int) allInventory.stream()
                .filter(i -> i.getCount() != null && i.getMinStock() != null && i.getCount() < i.getMinStock())
                .count();

        // 5. Pending requests (status == Pending)
        int pendingRequests = (int) requestRepository.findAll().stream()
                .filter(r -> "Pending".equalsIgnoreCase(r.getStatus()))
                .count();

        ScheduledReport report = ScheduledReport.builder()
                .generatedTime(LocalDateTime.now())
                .totalEmergencies(totalEmergencies)
                .activeVolunteers(activeVolunteers)
                .shelterOccupancy(occupancyPercentage)
                .lowStockResources(lowStockResources)
                .pendingRequests(pendingRequests)
                .build();

        ScheduledReport saved = reportRepository.save(report);

        try {
            emailService.sendDailySummaryReportEmail("admin@araksha.gov.lk", totalEmergencies, activeVolunteers, occupancyPercentage, lowStockResources, pendingRequests);
        } catch (Exception e) {
            // log or ignore
        }

        return saved;
    }
}
