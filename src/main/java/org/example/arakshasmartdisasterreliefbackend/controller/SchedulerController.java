package org.example.arakshasmartdisasterreliefbackend.controller;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.entity.SchedulerJob;
import org.example.arakshasmartdisasterreliefbackend.entity.SchedulerLog;
import org.example.arakshasmartdisasterreliefbackend.service.SchedulerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/scheduler")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class SchedulerController {

    private final SchedulerService schedulerService;

    @GetMapping("/jobs")
    public ResponseEntity<List<SchedulerJob>> getSchedulerJobs() {
        return ResponseEntity.ok(schedulerService.getAllJobs());
    }

    @GetMapping("/logs")
    public ResponseEntity<List<SchedulerLog>> getSchedulerLogs() {
        return ResponseEntity.ok(schedulerService.getAllLogs());
    }

    @PostMapping("/run/{jobKey}")
    public ResponseEntity<String> triggerJob(@PathVariable String jobKey) {
        try {
            // Run job synchronously so the frontend can receive confirmation immediately.
            schedulerService.runJob(jobKey);
            return ResponseEntity.ok("Successfully executed job: " + jobKey);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Job execution failed: " + e.getMessage());
        }
    }
}
