package org.example.arakshasmartdisasterreliefbackend.service.impl;

import org.example.arakshasmartdisasterreliefbackend.dto.response.*;
import org.example.arakshasmartdisasterreliefbackend.entity.Volunteer;
import org.example.arakshasmartdisasterreliefbackend.repository.IncidentRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerRepository;
import org.example.arakshasmartdisasterreliefbackend.service.ReportsService;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.*;

/**
 * Implementation of ReportsService.
 *
 * This class does all the database aggregation and maps results
 * into DTOs that the controller can return as JSON.
 *
 * Key responsibility: translate a "period" string like "LAST_30_DAYS"
 * into actual date ranges, then query the DB and build the response.
 */
@Service
public class ReportsServiceImpl implements ReportsService {

    private final IncidentRepository incidentRepo;
    private final VolunteerRepository volunteerRepo;

    // Month labels used for the line chart and bar chart X-axis
    private static final List<String> MONTH_LABELS = List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun");
    // The actual month numbers that correspond to the labels (January=1 … June=6)
    private static final int[] CHART_MONTHS = {1, 2, 3, 4, 5, 6};
    private static final int CHART_YEAR = 2025;

    public ReportsServiceImpl(IncidentRepository incidentRepo,
                              VolunteerRepository volunteerRepo) {
        this.incidentRepo = incidentRepo;
        this.volunteerRepo = volunteerRepo;
    }

    /**
     * Main entry point — builds the full page response for a given period.
     */
    @Override
    public ReportsPageResponse getReportsByPeriod(String period) {
        // Step 1: Calculate the date range for the selected period
        LocalDateTime[] range = getDateRange(period);
        LocalDateTime from = range[0];
        LocalDateTime to   = range[1];

        // Step 2: Build KPI stat cards
        List<ReportsSummaryResponse> stats = buildStats(from, to);

        // Step 3: Build disaster category breakdown
        List<DisasterCategoryDto> disasters = buildDisasterCategories(from, to);

        // Step 4: Build monthly trends data (line chart + bar chart)
        TrendsDataDto trends = buildTrends();

        // Step 5: Build volunteer leaderboard
        List<VolunteerPerformanceDto> volunteers = getTopVolunteers();

        // Step 6: Build the human-readable dashboard label
        String dashboardLabel = getDashboardLabel(period);

        return new ReportsPageResponse(period, dashboardLabel, stats, disasters, trends, volunteers);
    }

    /**
     * Returns the top 5 volunteers by tasks completed.
     */
    @Override
    public List<VolunteerPerformanceDto> getTopVolunteers() {
        List<Volunteer> top5 = volunteerRepo.findTop5ByOrderByTasksCompletedDesc();
        List<VolunteerPerformanceDto> result = new ArrayList<>();

        for (int i = 0; i < top5.size(); i++) {
            Volunteer v = top5.get(i);
            result.add(new VolunteerPerformanceDto(
                    i + 1,                        // rank (1-based)
                    v.getName(),
                    v.getAvgResponseMinutes(),
                    v.getTasksCompleted(),
                    v.getRating()
            ));
        }
        return result;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // PRIVATE HELPER METHODS
    // ─────────────────────────────────────────────────────────────────────────

    /**
     * Returns [from, to] LocalDateTime array for the given period string.
     *
     * LAST_30_DAYS → last 30 days from now
     * Q2_2025      → April 1 – June 30, 2025
     * YTD_2025     → January 1 – June 30, 2025 (year-to-date through June)
     */
    private LocalDateTime[] getDateRange(String period) {
        LocalDateTime now = LocalDateTime.now();
        return switch (period.toUpperCase()) {
            case "LAST_30_DAYS" -> new LocalDateTime[]{now.minusDays(30), now};
            case "Q2_2025"      -> new LocalDateTime[]{
                    LocalDateTime.of(2025, Month.APRIL, 1, 0, 0),
                    LocalDateTime.of(2025, Month.JUNE, 30, 23, 59, 59)
            };
            case "YTD_2025"     -> new LocalDateTime[]{
                    LocalDateTime.of(2025, Month.JANUARY, 1, 0, 0),
                    LocalDateTime.of(2025, Month.JUNE, 30, 23, 59, 59)
            };
            // Default fallback: last 30 days
            default -> new LocalDateTime[]{now.minusDays(30), now};
        };
    }

    /**
     * Human-readable label for the selected period.
     */
    private String getDashboardLabel(String period) {
        return switch (period.toUpperCase()) {
            case "LAST_30_DAYS" -> "Executive dashboard - Last 30 Days";
            case "Q2_2025"      -> "Executive dashboard - Q2 2025";
            case "YTD_2025"     -> "Executive dashboard - Year-To-Date 2025";
            default             -> "Executive dashboard";
        };
    }

    /**
     * Builds the 4 KPI stat cards by querying the DB for the given date range.
     *
     * For "change" percentage, we compare to the equivalent previous period.
     * For simplicity in this implementation, we use a hardcoded reference period
     * (the prior equivalent window) for the percentage badge.
     */
    private List<ReportsSummaryResponse> buildStats(LocalDateTime from, LocalDateTime to) {
        // ── Query the DB ──────────────────────────────────────────────────────
        long totalIncidents   = incidentRepo.countByDateRange(from, to);
        long resolvedCount    = incidentRepo.countByStatusAndDateRange("RESOLVED", from, to);
        long activeVolunteers = volunteerRepo.countByIsActive(true);
        Double avgResponseRaw = incidentRepo.avgResponseTimeByDateRange(from, to);
        double avgResponse    = (avgResponseRaw != null) ? Math.round(avgResponseRaw * 10.0) / 10.0 : 0.0;

        // ── Compare to previous period to compute the % change badge ──────────
        // Previous period = same duration, ending at "from"
        long durationDays = java.time.Duration.between(from, to).toDays();
        LocalDateTime prevFrom = from.minusDays(durationDays);
        LocalDateTime prevTo   = from;

        long prevIncidents  = incidentRepo.countByDateRange(prevFrom, prevTo);
        long prevResolved   = incidentRepo.countByStatusAndDateRange("RESOLVED", prevFrom, prevTo);
        Double prevAvgRaw   = incidentRepo.avgResponseTimeByDateRange(prevFrom, prevTo);
        double prevAvg      = (prevAvgRaw != null) ? prevAvgRaw : 0.0;

        // ── Format change percentages ──────────────────────────────────────────
        String incidentChange  = formatChange(prevIncidents, totalIncidents);
        String resolvedChange  = formatChange(prevResolved, resolvedCount);
        String responseChange  = (prevAvg > 0)
                ? formatChangeDouble(prevAvg, avgResponse)
                : "--%";
        // Active volunteers: compare to previous active count (use prior week as proxy)
        long prevActive = volunteerRepo.countByIsActive(true); // no time filter on volunteers
        String volunteerChange = "+0%"; // volunteers are a snapshot, not time-ranged

        // ── Build the stat card objects ────────────────────────────────────────
        // Format numbers with commas: 1284 → "1,284"
        NumberFormat nf = NumberFormat.getInstance(Locale.US);

        List<ReportsSummaryResponse> stats = new ArrayList<>();
        stats.add(new ReportsSummaryResponse(
                "Total Incidents",
                nf.format(totalIncidents),
                incidentChange,
                true,           // More incidents = positive (we are tracking more)
                "incidents"
        ));
        stats.add(new ReportsSummaryResponse(
                "Resolved",
                nf.format(resolvedCount),
                resolvedChange,
                true,           // More resolved = positive
                "resolved"
        ));
        stats.add(new ReportsSummaryResponse(
                "Volunteers Active",
                nf.format(activeVolunteers),
                volunteerChange,
                true,           // More volunteers = positive
                "volunteers"
        ));
        stats.add(new ReportsSummaryResponse(
                "Avg Response (min)",
                String.valueOf(avgResponse),
                responseChange,
                false,          // For response time, Angular colors the badge based on sign:
                // negative % (faster) is shown in red badge by the existing HTML
                // isPositive=false means the badge will be red — matches the design
                "response"
        ));

        return stats;
    }

    /**
     * Builds the 6 disaster category rows.
     * Order matches the existing HTML: Flood, Hurricane, Fire, Earthquake, Medical, Other.
     */
    private List<DisasterCategoryDto> buildDisasterCategories(LocalDateTime from, LocalDateTime to) {
        List<DisasterCategoryDto> list = new ArrayList<>();
        list.add(new DisasterCategoryDto("Flood",      incidentRepo.countByTypeAndDateRange("FLOOD",     from, to)));
        list.add(new DisasterCategoryDto("Hurricane",  incidentRepo.countByTypeAndDateRange("HURRICANE", from, to)));
        list.add(new DisasterCategoryDto("Fire",       incidentRepo.countByTypeAndDateRange("FIRE",      from, to)));
        list.add(new DisasterCategoryDto("Earthquake", incidentRepo.countByTypeAndDateRange("EARTHQUAKE",from, to)));
        list.add(new DisasterCategoryDto("Medical",    incidentRepo.countByTypeAndDateRange("MEDICAL",   from, to)));
        list.add(new DisasterCategoryDto("Other",      incidentRepo.countByTypeAndDateRange("OTHER",     from, to)));
        return list;
    }

    /**
     * Builds monthly trend arrays (Jan–Jun 2025) for the line and bar charts.
     *
     * For each month 1–6 of 2025, queries the DB for:
     *   - Flood count
     *   - Fire count
     *   - Hurricane count
     *   - Earthquake count
     *   - Average response time
     */
    private TrendsDataDto buildTrends() {
        List<Long>   flood     = new ArrayList<>();
        List<Long>   fire      = new ArrayList<>();
        List<Long>   hurricane = new ArrayList<>();
        List<Long>   earthquake= new ArrayList<>();
        List<Double> avgResp   = new ArrayList<>();

        for (int month : CHART_MONTHS) {
            flood.add(incidentRepo.countByTypeAndMonth("FLOOD",      CHART_YEAR, month));
            fire.add(incidentRepo.countByTypeAndMonth("FIRE",        CHART_YEAR, month));
            hurricane.add(incidentRepo.countByTypeAndMonth("HURRICANE", CHART_YEAR, month));
            earthquake.add(incidentRepo.countByTypeAndMonth("EARTHQUAKE",CHART_YEAR, month));

            Double avg = incidentRepo.avgResponseTimeByMonth(CHART_YEAR, month);
            avgResp.add(avg != null ? Math.round(avg * 10.0) / 10.0 : 0.0);
        }

        return new TrendsDataDto(MONTH_LABELS, flood, fire, hurricane, earthquake, avgResp);
    }

    /**
     * Computes a percentage change string between an old and new long value.
     * Example: oldVal=100, newVal=118 → "+18%"
     *          oldVal=100, newVal=72  → "-28%"
     */
    private String formatChange(long oldVal, long newVal) {
        if (oldVal == 0) return "+0%";
        double pct = ((double)(newVal - oldVal) / oldVal) * 100;
        return (pct >= 0 ? "+" : "") + Math.round(pct) + "%";
    }

    /**
     * Same as formatChange but for double values (used for avg response time).
     */
    private String formatChangeDouble(double oldVal, double newVal) {
        if (oldVal == 0) return "0%";
        double pct = ((newVal - oldVal) / oldVal) * 100;
        return (pct >= 0 ? "+" : "") + Math.round(pct) + "%";
    }
}
