package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.ShelterRequestDTO;
import org.example.arakshasmartdisasterreliefbackend.entity.Shelter;
import org.example.arakshasmartdisasterreliefbackend.service.impl.ShelterServiceImpl;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelters")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ShelterController {
    private final ShelterServiceImpl shelterService;

    @PostMapping
    public Shelter save(@RequestBody ShelterRequestDTO dto) {
        return shelterService.registerShelter(dto);
    }

    @GetMapping
    public List<Shelter> getAll() {
        return shelterService.getAllShelters();
    }

    @GetMapping("/search")
    public List<Shelter> search(@RequestParam String keyword) {
        return shelterService.searchShelters(keyword);
    }

    @GetMapping("/status/{status}")
    public List<Shelter> filter(@PathVariable String status) {
        return shelterService.getSheltersByStatus(status);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        shelterService.deleteShelter(id);
    }

    @PutMapping("/{id}")
    public Shelter update(@PathVariable Long id, @RequestBody ShelterRequestDTO dto) {
        return shelterService.updateShelter(id, dto);
    }
}