package org.example.arakshasmartdisasterreliefbackend.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.request.EmergencyRequestRequest;
import org.example.arakshasmartdisasterreliefbackend.dto.response.EmergencyRequestResponse;
import org.example.arakshasmartdisasterreliefbackend.service.EmergencyRequestService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/emergency-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class EmergencyRequestController {

    private final EmergencyRequestService emergencyRequestService;

    // ── Create ────────────────────────────────────────────────────────────────
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public EmergencyRequestResponse createEmergencyRequest(
            @Valid @RequestBody EmergencyRequestRequest request) {

        return emergencyRequestService.createEmergencyRequest(request);
    }

    // ── Get All ───────────────────────────────────────────────────────────────
    @GetMapping
    public List<EmergencyRequestResponse> getAllEmergencyRequests() {

        return emergencyRequestService.getAllEmergencyRequests();
    }

    // ── Advanced Search / Filter / Paginate ──────────────────────────────────
    /**
     * GET /api/emergency-requests/search
     *
     * Query params (all optional):
     *   keyword      – searches requestId, citizenName, location
     *   status       – Pending | Assigned | In Progress | Resolved | Completed
     *   priority     – Critical | High | Medium | Low
     *   disasterType – emergencyType exact match
     *   district     – partial match on location
     *   dateFrom     – ISO date (yyyy-MM-dd), inclusive start
     *   dateTo       – ISO date (yyyy-MM-dd), inclusive end
     *   page         – 0-based page number (default 0)
     *   size         – page size (default 6)
     *   sort         – e.g. requestTime,desc | priority,asc
     */
    @GetMapping("/search")
    public Page<EmergencyRequestResponse> searchEmergencyRequests(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String disasterType,
            @RequestParam(required = false) String district,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateFrom,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dateTo,
            @PageableDefault(size = 6, sort = "requestTime") Pageable pageable) {

        return emergencyRequestService.searchEmergencyRequests(
                keyword, status, priority, disasterType, district, dateFrom, dateTo, pageable);
    }

    // ── Get By ID ─────────────────────────────────────────────────────────────
    @GetMapping("/{id}")
    public EmergencyRequestResponse getEmergencyRequestById(@PathVariable Long id) {

        return emergencyRequestService.getEmergencyRequestById(id);
    }

    // ── Update ────────────────────────────────────────────────────────────────
    @PutMapping("/{id}")
    public EmergencyRequestResponse updateEmergencyRequest(
            @PathVariable Long id,
            @Valid @RequestBody EmergencyRequestRequest request) {

        return emergencyRequestService.updateEmergencyRequest(id, request);
    }

    // ── Delete ────────────────────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteEmergencyRequest(@PathVariable Long id) {

        emergencyRequestService.deleteEmergencyRequest(id);
    }
}