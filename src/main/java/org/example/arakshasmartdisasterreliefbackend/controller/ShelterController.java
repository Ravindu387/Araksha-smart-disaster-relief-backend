package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.ShelterRequestDTO;
import org.example.arakshasmartdisasterreliefbackend.entity.Shelter;
import org.example.arakshasmartdisasterreliefbackend.service.impl.ShelterServiceImpl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shelters")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ShelterController {
    private final ShelterServiceImpl shelterService;

    // ── Create ────────────────────────────────────────────────────────────────
    @PostMapping
    public Shelter save(@RequestBody ShelterRequestDTO dto) {
        return shelterService.registerShelter(dto);
    }

    // ── Get All ───────────────────────────────────────────────────────────────
    @GetMapping
    public List<Shelter> getAll() {
        return shelterService.getAllShelters();
    }

    // ── Advanced Search / Filter / Paginate ──────────────────────────────────
    /**
     * GET /api/shelters/search
     *
     * Query params (all optional):
     *   keyword      – partial match on name or address
     *   status       – Available | Limited | Full
     *   minCapacity  – minimum capacity (inclusive)
     *   maxCapacity  – maximum capacity (inclusive)
     *   page         – 0-based page number (default 0)
     *   size         – page size (default 10)
     *   sort         – e.g. capacity,desc | name,asc
     *
     * NOTE: this endpoint supersedes the old /search?keyword and /status/{status}
     * which are kept below for backward-compatibility.
     */
    @GetMapping("/search")
    public Page<Shelter> search(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Integer minCapacity,
            @RequestParam(required = false) Integer maxCapacity,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        return shelterService.searchSheltersPage(keyword, status, minCapacity, maxCapacity, pageable);
    }

    // ── Legacy endpoints (kept for backward-compatibility) ────────────────────
    @GetMapping("/status/{status}")
    public List<Shelter> filter(@PathVariable String status) {
        return shelterService.getSheltersByStatus(status);
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Integer id) {
        shelterService.deleteShelter(id);
    }

    // ── Update ────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public Shelter update(@PathVariable Integer id, @RequestBody ShelterRequestDTO dto) {
        return shelterService.updateShelter(id, dto);
    }
}