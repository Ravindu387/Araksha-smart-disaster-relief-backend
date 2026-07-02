package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.entity.ScheduledReport;

public interface SystemReportService {
    ScheduledReport generateAndSaveDailySummary();
}
