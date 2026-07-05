package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.response.AdminDashboardStatsDTO;
import org.example.arakshasmartdisasterreliefbackend.service.AdminDashboardService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "${cors.allowed-origins}")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    @GetMapping("/stats")
    public AdminDashboardStatsDTO getDashboardStats() {
        return adminDashboardService.getDashboardStats();
    }
}