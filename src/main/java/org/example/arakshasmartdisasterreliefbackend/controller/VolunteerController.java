package org.example.arakshasmartdisasterreliefbackend.controller;

import org.example.arakshasmartdisasterreliefbackend.dto.request.VolunteerRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.VolunteerResponse;
import org.example.arakshasmartdisasterreliefbackend.service.VolunteerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/volunteers")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class VolunteerController {

    private final VolunteerService volunteerService;

    // ── Create Volunteer ──────────────────────────────────────────────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VolunteerResponse createVolunteer(
            @Valid @RequestBody VolunteerRequest request) {

        return volunteerService.createVolunteer(request);
    }

    // ── Get All Volunteers ────────────────────────────────────────────────────
    @GetMapping
    public List<VolunteerResponse> getAllVolunteers() {

        return volunteerService.getAllVolunteers();
    }

    // ── Advanced Search / Filter / Paginate ──────────────────────────────────
    /**
     * GET /api/volunteers/search
     *
     * Query params (all optional):
     *   keyword  – searches name, phone, location
     *   status   – Available | On Duty | Off Duty
     *   district – partial match on location
     *   skill    – partial match on any skill
     *   page     – 0-based page number (default 0)
     *   size     – page size (default 10)
     *   sort     – e.g. name,asc | tasks,desc | id,desc
     */
    @GetMapping("/search")
    public Page<VolunteerResponse> searchVolunteers(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String district,
            @RequestParam(required = false) String skill,
            @PageableDefault(size = 10, sort = "name") Pageable pageable) {

        return volunteerService.searchVolunteers(keyword, status, district, skill, pageable);
    }

    // ── Get Volunteer By ID ───────────────────────────────────────────────────
    @GetMapping("/{id}")
    public VolunteerResponse getVolunteerById(
            @PathVariable Long id) {

        return volunteerService.getVolunteerById(id);
    }

    // ── Update Volunteer ──────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public VolunteerResponse updateVolunteer(
            @PathVariable Long id,
            @Valid @RequestBody VolunteerRequest request) {

        return volunteerService.updateVolunteer(id, request);
    }

    // ── Delete Volunteer ──────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVolunteer(
            @PathVariable Long id) {

        volunteerService.deleteVolunteer(id);
    }

}