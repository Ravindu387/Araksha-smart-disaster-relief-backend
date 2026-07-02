package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.ShelterRequestDTO;
import org.example.arakshasmartdisasterreliefbackend.entity.Notification;
import org.example.arakshasmartdisasterreliefbackend.entity.Shelter;
import org.example.arakshasmartdisasterreliefbackend.repository.NotificationRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.ShelterRepository;
import org.example.arakshasmartdisasterreliefbackend.service.ShelterService;
import org.example.arakshasmartdisasterreliefbackend.specification.ShelterSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ShelterServiceImpl implements ShelterService {
    private final ShelterRepository shelterRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public Shelter registerShelter(ShelterRequestDTO dto) {
        Shelter shelter = new Shelter();

        shelter.setName(dto.getName());
        shelter.setAddress(dto.getAddress());

        shelter.setCapacity(dto.getCapacity());
        shelter.setOccupied(dto.getOccupied());


        double percentage =
                (dto.getOccupied() * 100.0) / dto.getCapacity();

        if (percentage >= 100) {
            shelter.setStatus("Full");
        } else if (percentage >= 80) {
            shelter.setStatus("Limited");
        } else {
            shelter.setStatus("Available");
        }

        shelter.setLatitude(dto.getLatitude());
        shelter.setLongitude(dto.getLongitude());

        shelter.setAmenities(dto.getAmenities());
        shelter.setShelterImageUrl(dto.getShelterImageUrl());

        shelter.setLastUpdated(LocalDateTime.now());

        Shelter saved = shelterRepository.save(shelter);

        // Auto-create notification
        Notification n = new Notification();
        n.setCategory("shelters");
        n.setSeverity("info");
        n.setTitle("New Shelter Registered: " + saved.getName());
        n.setBadge("Info");
        int freeBeds = saved.getCapacity() - saved.getOccupied();
        n.setDescription(saved.getName() + " registered at " + saved.getAddress() + ". Capacity: " + saved.getCapacity() + " beds (" + freeBeds + " free). Status: " + saved.getStatus() + ".");
        n.setTime("Just now");
        n.setRead(false);
        notificationRepository.save(n);

        return saved;
    }

    @Override
    public List<Shelter> getAllShelters() {
        return shelterRepository.findAll();
    }

    @Override
    public List<Shelter> searchShelters(String keyword) {
        return shelterRepository.findByNameContainingIgnoreCase(keyword);
    }

    @Override
    public List<Shelter> getSheltersByStatus(String status) {
        return shelterRepository.findByStatus(status);
    }

    @Override
    public void deleteShelter(Integer id) {
        shelterRepository.deleteById(id);
    }

    @Override
    public Shelter updateShelter(Integer id, ShelterRequestDTO dto) {
        Shelter shelter = shelterRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shelter not found"));

        shelter.setName(dto.getName());
        shelter.setAddress(dto.getAddress());
        shelter.setCapacity(dto.getCapacity());
        shelter.setOccupied(dto.getOccupied());

        double percentage = (dto.getOccupied() * 100.0) / dto.getCapacity();
        if (percentage >= 100) {
            shelter.setStatus("Full");
        } else if (percentage >= 80) {
            shelter.setStatus("Limited");
        } else {
            shelter.setStatus("Available");
        }

        shelter.setLatitude(dto.getLatitude());
        shelter.setLongitude(dto.getLongitude());
        shelter.setAmenities(dto.getAmenities());
        shelter.setShelterImageUrl(dto.getShelterImageUrl());
        shelter.setLastUpdated(LocalDateTime.now());

        Shelter updated = shelterRepository.save(shelter);

        // Auto-create notification
        String severity = "Full".equals(updated.getStatus()) ? "critical" : "Limited".equals(updated.getStatus()) ? "high" : "info";
        String badge = "Full".equals(updated.getStatus()) ? "Critical" : "Limited".equals(updated.getStatus()) ? "High" : "Info";
        Notification n = new Notification();
        n.setCategory("shelters");
        n.setSeverity(severity);
        n.setTitle("Shelter Updated: " + updated.getName());
        n.setBadge(badge);
        int freeBeds = updated.getCapacity() - updated.getOccupied();
        n.setDescription(updated.getName() + " updated. " + freeBeds + " of " + updated.getCapacity() + " beds free. Status: " + updated.getStatus() + ".");
        n.setTime("Just now");
        n.setRead(false);
        notificationRepository.save(n);

        return updated;
    }

    // ── Search with server-side pagination ────────────────────────────────────
    @Override
    public Page<Shelter> searchSheltersPage(
            String keyword,
            String status,
            Integer minCapacity,
            Integer maxCapacity,
            Pageable pageable) {

        return shelterRepository.findAll(
                ShelterSpecification.build(keyword, status, minCapacity, maxCapacity),
                pageable);
    }
}