package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.request.ShelterRequestDTO;
import org.example.arakshasmartdisasterreliefbackend.entity.Shelter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ShelterService {
    Shelter registerShelter(ShelterRequestDTO dto);
    List<Shelter> getAllShelters();
    List<Shelter> searchShelters(String keyword);
    List<Shelter> getSheltersByStatus(String status);
    void deleteShelter(Integer id);
    Shelter updateShelter(Integer id, ShelterRequestDTO dto);

    /**
     * Server-side search with optional filters and pagination.
     *
     * @param keyword     partial match on name or address
     * @param status      exact match on status
     * @param minCapacity capacity >= minCapacity
     * @param maxCapacity capacity <= maxCapacity
     * @param pageable    page, size, sort
     * @return paginated result
     */
    Page<Shelter> searchSheltersPage(
            String keyword,
            String status,
            Integer minCapacity,
            Integer maxCapacity,
            Pageable pageable);
}