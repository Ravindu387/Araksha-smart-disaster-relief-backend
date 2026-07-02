package org.example.arakshasmartdisasterreliefbackend.scheduler;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.arakshasmartdisasterreliefbackend.entity.SchedulerJob;
import org.example.arakshasmartdisasterreliefbackend.repository.SchedulerJobRepository;
import org.example.arakshasmartdisasterreliefbackend.service.SchedulerService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.support.CronExpression;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutomatedJobs {

    private final SchedulerService schedulerService;
    private final SchedulerJobRepository jobRepository;

    @PostConstruct
    public void initSchedulerJobs() {
        seedJob("volunteer-reminder", "Volunteer Reminders", "0 0 8 * * *");
        seedJob("shelter-monitoring", "Shelter Capacity Monitoring", "0 0 * * * *");
        seedJob("request-escalation", "Emergency Request Escalation", "0 */30 * * * *");
        seedJob("inventory-monitoring", "Resource Inventory Monitoring", "0 0 6 * * *");
        seedJob("expired-shelter-check", "Expired Temporary Shelter Check", "0 0 0 * * *");
        seedJob("daily-summary", "Daily System Summary", "0 59 23 * * *");
    }

    private void seedJob(String key, String name, String cronExp) {
        if (jobRepository.findByJobKey(key).isEmpty()) {
            LocalDateTime nextRun = LocalDateTime.now();
            try {
                nextRun = CronExpression.parse(cronExp).next(LocalDateTime.now());
            } catch (Exception e) {
                log.error("Failed to parse cron expression for job key: " + key, e);
            }

            SchedulerJob job = SchedulerJob.builder()
                    .jobKey(key)
                    .name(name)
                    .cronExpression(cronExp)
                    .status("ACTIVE")
                    .nextRun(nextRun)
                    .build();
            jobRepository.save(job);
            log.info("Seeded scheduler job: {}", name);
        }
    }

    // 1. Volunteer Reminder Automation (Every day at 8:00 AM)
    @Scheduled(cron = "0 0 8 * * *")
    public void runVolunteerReminderJob() {
        runJobSafe("volunteer-reminder");
    }

    // 2. Shelter Capacity Monitoring (Every hour)
    @Scheduled(cron = "0 0 * * * *")
    public void runShelterOccupancyMonitoringJob() {
        runJobSafe("shelter-monitoring");
    }

    // 3. Emergency Request Escalation (Every 30 minutes)
    @Scheduled(cron = "0 */30 * * * *")
    public void runEmergencyRequestEscalationJob() {
        runJobSafe("request-escalation");
    }

    // 4. Resource Inventory Monitoring (Every morning at 6:00 AM)
    @Scheduled(cron = "0 0 6 * * *")
    public void runResourceInventoryMonitoringJob() {
        runJobSafe("inventory-monitoring");
    }

    // 5. Expired Temporary Shelter Check (Every night at 12:00 AM)
    @Scheduled(cron = "0 0 0 * * *")
    public void runExpiredTemporaryShelterCheckJob() {
        runJobSafe("expired-shelter-check");
    }

    // 6. Daily System Summary (Every day at 11:59 PM)
    @Scheduled(cron = "0 59 23 * * *")
    public void runDailySystemSummaryJob() {
        runJobSafe("daily-summary");
    }

    private void runJobSafe(String key) {
        try {
            // Check if job is active before executing
            jobRepository.findByJobKey(key).ifPresent(job -> {
                if ("ACTIVE".equalsIgnoreCase(job.getStatus())) {
                    schedulerService.runJob(key);
                }
            });
        } catch (Exception e) {
            log.error("Error executing scheduled job: " + key, e);
        }
    }
}
