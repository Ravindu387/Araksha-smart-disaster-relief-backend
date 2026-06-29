package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.response.*;

import java.util.List;

public interface DashboardService {
    DashboardStatsResponse getStats(Long volunteerId);


    List<TaskResponse> getTasks(Long volunteerId);


    List<EmergencyResponseVolunteers> getRequests();


    PerformanceResponse getPerformance(Long volunteerId);


    List<CompletedTaskResponse> getCompletedTasks(Long volunteerId);
}
