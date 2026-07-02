package org.example.arakshasmartdisasterreliefbackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.arakshasmartdisasterreliefbackend.entity.*;
import org.example.arakshasmartdisasterreliefbackend.repository.*;
import org.example.arakshasmartdisasterreliefbackend.service.SchedulerService;
import org.example.arakshasmartdisasterreliefbackend.service.SystemReportService;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SchedulerServiceImpl implements SchedulerService {

    private final SchedulerJobRepository jobRepository;
    private final SchedulerLogRepository logRepository;
    private final TaskRepository taskRepository;
    private final ShelterRepository shelterRepository;
    private final EmergencyRequestRepository requestRepository;
    private final InventoryRepository inventoryRepository;
    private final NotificationRepository notificationRepository;
    private final SystemReportService reportService;

    @Override
    public List<SchedulerJob> getAllJobs() {
        return jobRepository.findAll();
    }

    @Override
    public List<SchedulerLog> getAllLogs() {
        return logRepository.findAllByOrderByExecutionTimeDesc();
    }

    @Override
    @Transactional
    public void runJob(String jobKey) {
        long startTime = System.currentTimeMillis();
        boolean success = false;
        String message = "";
        try {
            if ("volunteer-reminder".equals(jobKey)) {
                executeVolunteerReminder();
                message = "Executed volunteer reminders.";
            } else if ("shelter-monitoring".equals(jobKey)) {
                executeShelterOccupancyMonitoring();
                message = "Checked shelter capacities.";
            } else if ("request-escalation".equals(jobKey)) {
                executeEmergencyRequestEscalation();
                message = "Escalated unresolved high-priority requests.";
            } else if ("inventory-monitoring".equals(jobKey)) {
                executeResourceInventoryMonitoring();
                message = "Checked inventory levels and generated low stock alerts.";
            } else if ("expired-shelter-check".equals(jobKey)) {
                executeExpiredTemporaryShelterCheck();
                message = "Checked and deactivated expired temporary shelters.";
            } else if ("daily-summary".equals(jobKey)) {
                executeDailySystemSummary();
                message = "Generated daily summary report.";
            } else {
                throw new IllegalArgumentException("Unknown job key: " + jobKey);
            }
            success = true;
        } catch (Exception e) {
            success = false;
            message = "Failed: " + e.getMessage();
            throw new RuntimeException("Job execution failed: " + jobKey, e);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            updateJobExecution(jobKey, success, message, duration);
        }
    }

    @Override
    @Transactional
    public void updateJobExecution(String jobKey, boolean success, String message, long durationMs) {
        Optional<SchedulerJob> jobOpt = jobRepository.findByJobKey(jobKey);
        if (jobOpt.isPresent()) {
            SchedulerJob job = jobOpt.get();
            job.setLastRun(LocalDateTime.now());
            job.setTotalRuns(job.getTotalRuns() + 1);
            if (success) {
                job.setLastRunStatus("SUCCESS");
            } else {
                job.setLastRunStatus("FAILED");
                job.setFailedRuns(job.getFailedRuns() + 1);
            }

            try {
                CronExpression cron = CronExpression.parse(job.getCronExpression());
                job.setNextRun(cron.next(LocalDateTime.now()));
            } catch (Exception e) {
                // Ignore cron parsing errors for next run calculation
            }
            jobRepository.save(job);
        }

        // Save log entry
        SchedulerLog log = SchedulerLog.builder()
                .jobName(jobKey)
                .executionTime(LocalDateTime.now())
                .durationMs(durationMs)
                .status(success ? "SUCCESS" : "FAILED")
                .message(message)
                .build();
        logRepository.save(log);
    }

    private void executeVolunteerReminder() {
        // Query tasks where status = ASSIGNED or PENDING
        List<Task> pendingTasks = taskRepository.findAll().stream()
                .filter(t -> "Assigned".equalsIgnoreCase(t.getStatus()) || "Pending".equalsIgnoreCase(t.getStatus()))
                .toList();

        for (Task task : pendingTasks) {
            if (task.getVolunteer() != null && task.getEmergencyRequest() != null) {
                Notification notification = new Notification();
                notification.setCategory("assignments");
                notification.setSeverity("info");
                notification.setTitle("Volunteer Assignment Reminder");
                notification.setBadge("Reminder");
                notification.setDescription("Reminder for " + task.getVolunteer().getName() + " assigned to request " + task.getEmergencyRequest().getRequestId() + ". Please confirm participation.");
                notification.setTime("Just now");
                notification.setRead(false);
                notificationRepository.save(notification);
            }
        }
    }

    private void executeShelterOccupancyMonitoring() {
        List<Shelter> shelters = shelterRepository.findAll();
        for (Shelter shelter : shelters) {
            if (shelter.getCapacity() != null && shelter.getOccupied() != null && shelter.getCapacity() > 0) {
                double pct = ((double) shelter.getOccupied() / shelter.getCapacity()) * 100.0;
                if (pct >= 90.0) {
                    Notification notification = new Notification();
                    notification.setCategory("shelters");
                    notification.setSeverity("high");
                    notification.setTitle("Shelter Near Capacity Alert");
                    notification.setBadge("High");
                    notification.setDescription("Shelter '" + shelter.getName() + "' is near capacity at " + (int) pct + "% (" + shelter.getOccupied() + "/" + shelter.getCapacity() + " beds occupied).");
                    notification.setTime("Just now");
                    notification.setRead(false);
                    notificationRepository.save(notification);
                }
            }
        }
    }

    private void executeEmergencyRequestEscalation() {
        List<EmergencyRequest> requests = requestRepository.findAll().stream()
                .filter(r -> "High".equalsIgnoreCase(r.getPriority()) || "Critical".equalsIgnoreCase(r.getPriority()))
                .filter(r -> "Pending".equalsIgnoreCase(r.getStatus()) || "Assigned".equalsIgnoreCase(r.getStatus()))
                .filter(r -> r.getRequestTime() != null && r.getRequestTime().isBefore(LocalDateTime.now().minusHours(2)))
                .toList();

        for (EmergencyRequest req : requests) {
            req.setStatus("ESCALATED");
            requestRepository.save(req);

            Notification notification = new Notification();
            notification.setCategory("alerts");
            notification.setSeverity("critical");
            notification.setTitle("Emergency Request Escalated");
            notification.setBadge("Critical");
            notification.setDescription("Unresolved request " + req.getRequestId() + " (Priority: " + req.getPriority() + ") has been automatically ESCALATED after 2 hours.");
            notification.setTime("Just now");
            notification.setRead(false);
            notificationRepository.save(notification);
        }
    }

    private void executeResourceInventoryMonitoring() {
        List<Inventory> inventoryList = inventoryRepository.findAll();
        for (Inventory item : inventoryList) {
            if (item.getCount() != null && item.getMinStock() != null && item.getCount() < item.getMinStock()) {
                Notification notification = new Notification();
                notification.setCategory("inventory");
                notification.setSeverity("critical");
                notification.setTitle("Low Stock Alert: " + item.getName());
                notification.setBadge("Critical");
                notification.setDescription("Resource '" + item.getName() + "' count falls to " + item.getCount() + " " + item.getUnit() + " (Threshold: " + item.getMinStock() + ").");
                notification.setTime("Just now");
                notification.setRead(false);
                notificationRepository.save(notification);
            }
        }
    }

    private void executeExpiredTemporaryShelterCheck() {
        List<Shelter> temporaryShelters = shelterRepository.findAll().stream()
                .filter(s -> s.getClosingDate() != null && s.getClosingDate().isBefore(LocalDateTime.now()))
                .filter(s -> !"Inactive".equalsIgnoreCase(s.getStatus()))
                .toList();

        for (Shelter shelter : temporaryShelters) {
            shelter.setStatus("Inactive");
            shelterRepository.save(shelter);

            Notification notification = new Notification();
            notification.setCategory("shelters");
            notification.setSeverity("info");
            notification.setTitle("Temporary Shelter Deactivated");
            notification.setBadge("Closed");
            notification.setDescription("Temporary shelter '" + shelter.getName() + "' automatically closed as its closing date has passed.");
            notification.setTime("Just now");
            notification.setRead(false);
            notificationRepository.save(notification);
        }
    }

    private void executeDailySystemSummary() {
        reportService.generateAndSaveDailySummary();
    }
}
