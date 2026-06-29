package org.example.arakshasmartdisasterreliefbackend.service;

import org.example.arakshasmartdisasterreliefbackend.dto.response.ReportsPageResponse;
import org.example.arakshasmartdisasterreliefbackend.dto.response.VolunteerPerformanceDto;

import java.util.List;

/**
 * Service interface for the Reports & Analytics module.
 *
 * The controller calls these methods.
 * The actual database logic lives in ReportsServiceImpl.
 */
public interface ReportsService {

    /**
     * Get the complete reports page data for a given time period.
     *
     * @param period  one of: "LAST_30_DAYS", "Q2_2025", "YTD_2025"
     * @return        full ReportsPageResponse with stats, disasters, trends, volunteers
     */
    ReportsPageResponse getReportsByPeriod(String period);

    /**
     * Get the top 5 volunteers ranked by tasks completed.
     * Used by GET /api/v1/reports/volunteers
     *
     * @return list of up to 5 VolunteerPerformanceDto objects
     */
    List<VolunteerPerformanceDto> getTopVolunteers();
}
