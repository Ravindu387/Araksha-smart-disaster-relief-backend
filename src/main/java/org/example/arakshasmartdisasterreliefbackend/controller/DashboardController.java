package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.dto.response.*;
import org.example.arakshasmartdisasterreliefbackend.service.DashboardService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")

public class DashboardController {

    private final DashboardService service;

    @GetMapping("/stats/{volunteerId}")
    public DashboardStatsResponse stats(
            @PathVariable Long volunteerId){

        return service.getStats(volunteerId);

    }
    @GetMapping("/tasks/{volunteerId}")
    public List<TaskResponse> tasks(
            @PathVariable Long volunteerId){

        return service.getTasks(volunteerId);

    }
    @GetMapping("/requests")
    public List<EmergencyResponseVolunteers> requests(){

        return service.getRequests();

    }
    @GetMapping("/performance/{volunteerId}")
    public PerformanceResponse performance(
            @PathVariable Long volunteerId){

        return service.getPerformance(volunteerId);

    }
    @GetMapping("/completed/{volunteerId}")
    public List<CompletedTaskResponse> completed(
            @PathVariable Long volunteerId){

        return service.getCompletedTasks(volunteerId);

    }

}
