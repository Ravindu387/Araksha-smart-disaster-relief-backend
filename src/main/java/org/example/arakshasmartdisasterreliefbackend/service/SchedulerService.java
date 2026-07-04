package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.entity.SchedulerJob;
import org.example.arakshasmartdisasterreliefbackend.entity.SchedulerLog;

import java.util.List;

public interface SchedulerService {
    List<SchedulerJob> getAllJobs();
    List<SchedulerLog> getAllLogs();
    void runJob(String jobKey);
    void updateJobExecution(String jobKey, boolean success, String message, long durationMs);
    SchedulerJob toggleJobStatus(String jobKey, String status);
    SchedulerJob updateJob(String jobKey, String cronExpression, String status);
}
