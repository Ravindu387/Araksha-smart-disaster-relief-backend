package org.example.arakshasmartdisasterreliefbackend.controller;

import org.example.arakshasmartdisasterreliefbackend.dto.response.ReportsPageResponse;
import org.example.arakshasmartdisasterreliefbackend.dto.response.VolunteerPerformanceDto;
import org.example.arakshasmartdisasterreliefbackend.service.ReportsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/reports")
@CrossOrigin(origins = "${cors.allowed-origins}")
public class ReportsController {

    private final ReportsService reportsService;

    public ReportsController(ReportsService reportsService) {
        this.reportsService = reportsService;
    }


    @GetMapping
    public ResponseEntity<ReportsPageResponse> getReports(
            @RequestParam(defaultValue = "LAST_30_DAYS") String period
    ) {
        ReportsPageResponse response = reportsService.getReportsByPeriod(period);
        return ResponseEntity.ok(response);
    }


    @GetMapping("/volunteers")
    public ResponseEntity<List<VolunteerPerformanceDto>> getTopVolunteers() {
        List<VolunteerPerformanceDto> volunteers = reportsService.getTopVolunteers();
        return ResponseEntity.ok(volunteers);
    }
}
