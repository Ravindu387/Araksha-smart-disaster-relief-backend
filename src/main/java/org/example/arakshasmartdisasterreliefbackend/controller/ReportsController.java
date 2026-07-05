package org.example.arakshasmartdisasterreliefbackend.controller;

import org.example.arakshasmartdisasterreliefbackend.dto.response.ReportsPageResponse;
import org.example.arakshasmartdisasterreliefbackend.dto.response.VolunteerPerformanceDto;
import org.example.arakshasmartdisasterreliefbackend.service.ReportsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Reports & Analytics.
 *
 * Base URL: /api/v1/reports
 *
 * This controller is permitted by SecurityConfig without authentication.
 * CORS headers are configured via properties (e.g. allowed-origins).
 *
 * Endpoints:
 *   GET /api/v1/reports?period=LAST_30_DAYS  → ReportsPageResponse (full page data)
 *   GET /api/v1/reports?period=Q2_2025       → ReportsPageResponse
 *   GET /api/v1/reports?period=YTD_2025      → ReportsPageResponse
 *   GET /api/v1/reports/volunteers           → List<VolunteerPerformanceDto>
 */
@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }

    /**
     * GET /api/v1/reports?period=LAST_30_DAYS
     *
     * Returns the complete reports page data for the given time period.
     *
     * The "period" query parameter must be one of:
     *   - LAST_30_DAYS  (default if not provided)
     *   - Q2_2025
     *   - YTD_2025
     *
     * Angular calls this endpoint every time the user clicks a tab button.
     *
     * Example Postman call:
     *   GET http://localhost:8080/api/v1/reports?period=Q2_2025
     *
     * @param period  the time period filter (defaults to LAST_30_DAYS)
     * @return        200 OK with ReportsPageResponse JSON body
     */
    @GetMapping
    public ResponseEntity<ReportsPageResponse> getReports(
            @RequestParam(defaultValue = "LAST_30_DAYS") String period
    ) {
        ReportsPageResponse response = reportsService.getReportsByPeriod(period);
        return ResponseEntity.ok(response);
    }

    /**
     * GET /api/v1/reports/volunteers
     *
     * Returns the top 5 volunteers ranked by tasks completed.
     * This is a separate endpoint in case Angular needs to refresh
     * the volunteer leaderboard independently of the main data.
     *
     * Example Postman call:
     *   GET http://localhost:8080/api/v1/reports/volunteers
     *
     * @return  200 OK with list of up to 5 VolunteerPerformanceDto objects
     */
    @GetMapping("/volunteers")
    public ResponseEntity<List<VolunteerPerformanceDto>> getTopVolunteers() {
        List<VolunteerPerformanceDto> volunteers = reportsService.getTopVolunteers();
        return ResponseEntity.ok(volunteers);
    }
}
