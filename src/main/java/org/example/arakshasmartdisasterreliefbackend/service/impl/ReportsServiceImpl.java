package org.example.arakshasmartdisasterreliefbackend.service.impl;

import org.example.arakshasmartdisasterreliefbackend.dto.response.*;
import org.example.arakshasmartdisasterreliefbackend.entity.Volunteer;
import org.example.arakshasmartdisasterreliefbackend.entity.Performance;
import org.example.arakshasmartdisasterreliefbackend.repository.IncidentRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.VolunteerRepository;
import org.example.arakshasmartdisasterreliefbackend.repository.PerformanceRepository;
import org.example.arakshasmartdisasterreliefbackend.service.ReportsService;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.Year;
import java.util.*;


@Service
public class ReportsServiceImpl implements ReportsService {

    private final IncidentRepository incidentRepo;
    private final VolunteerRepository volunteerRepo;
    private final PerformanceRepository performanceRepo;

    
    private static final List<String> MONTH_LABELS = List.of("Jan", "Feb", "Mar", "Apr", "May", "Jun");
    
    private static final int[] CHART_MONTHS = {1, 2, 3, 4, 5, 6};
    private static final int CHART_YEAR = 2025;

    public ReportsServiceImpl(IncidentRepository incidentRepo,
                              VolunteerRepository volunteerRepo,
                              PerformanceRepository performanceRepo) {
        this.incidentRepo = incidentRepo;
        this.volunteerRepo = volunteerRepo;
        this.performanceRepo = performanceRepo;
    }

    
    @Override
    public ReportsPageResponse getReportsByPeriod(String period) {
       
        LocalDateTime[] range = getDateRange(period);
        LocalDateTime from = range[0];
        LocalDateTime to   = range[1];

        
        List<ReportsSummaryResponse> stats = buildStats(from, to);

       
        List<DisasterCategoryDto> disasters = buildDisasterCategories(from, to);

        
        TrendsDataDto trends = buildTrends();

      
        List<VolunteerPerformanceDto> volunteers = getTopVolunteers();

        
        String dashboardLabel = getDashboardLabel(period);

        return new ReportsPageResponse(period, dashboardLabel, stats, disasters, trends, volunteers);
    }

   
    @Override
    public List<VolunteerPerformanceDto> getTopVolunteers() {
        List<Volunteer> top5 = volunteerRepo.findTop5ByOrderByTasksDesc();
        List<VolunteerPerformanceDto> result = new ArrayList<>();

        for (int i = 0; i < top5.size(); i++) {
            Volunteer v = top5.get(i);
            int avgResponse = performanceRepo.findByVolunteerId(v.getId())
                    .map(Performance::getResponse)
                    .orElse(0);

            result.add(new VolunteerPerformanceDto(
                    i + 1,                        // rank (1-based)
                    v.getName(),
                    avgResponse,
                    v.getTasks() != null ? v.getTasks() : 0,
                    v.getRating() != null ? v.getRating() : 0.0
            ));
        }
        return result;
    }

   
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
            
            default -> new LocalDateTime[]{now.minusDays(30), now};
        };
    }

    
    private String getDashboardLabel(String period) {
        return switch (period.toUpperCase()) {
            case "LAST_30_DAYS" -> "Executive dashboard - Last 30 Days";
            case "Q2_2025"      -> "Executive dashboard - Q2 2025";
            case "YTD_2025"     -> "Executive dashboard - Year-To-Date 2025";
            default             -> "Executive dashboard";
        };
    }

   
    private List<ReportsSummaryResponse> buildStats(LocalDateTime from, LocalDateTime to) {
        
        long totalIncidents   = incidentRepo.countByDateRange(from, to);
        long resolvedCount    = incidentRepo.countByStatusAndDateRange("RESOLVED", from, to);
        long activeVolunteers = volunteerRepo.countByStatusIgnoreCase("Available") + volunteerRepo.countByStatusIgnoreCase("On Duty");
        Double avgResponseRaw = incidentRepo.avgResponseTimeByDateRange(from, to);
        double avgResponse    = (avgResponseRaw != null) ? Math.round(avgResponseRaw * 10.0) / 10.0 : 0.0;

    
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
       
        long prevActive = volunteerRepo.countByStatusIgnoreCase("ACTIVE"); // no time filter on volunteers
        String volunteerChange = "+0%"; // volunteers are a snapshot, not time-ranged

       
        
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
                false,        
                "response"
        ));

        return stats;
    }

   
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

   
    private String formatChange(long oldVal, long newVal) {
        if (oldVal == 0) return "+0%";
        double pct = ((double)(newVal - oldVal) / oldVal) * 100;
        return (pct >= 0 ? "+" : "") + Math.round(pct) + "%";
    }

    
    private String formatChangeDouble(double oldVal, double newVal) {
        if (oldVal == 0) return "0%";
        double pct = ((newVal - oldVal) / oldVal) * 100;
        return (pct >= 0 ? "+" : "") + Math.round(pct) + "%";
    }
}
