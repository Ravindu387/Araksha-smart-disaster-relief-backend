package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * The complete API response for the Reports page.
 *
 * This single object is returned by GET /api/v1/reports?period=...
 * and contains everything the Angular Reports component needs to render the page.
 *
 * Example JSON structure:
 * {
 *   "period": "Q2_2025",
 *   "dashboardLabel": "Executive dashboard - Q2 2025",
 *   "stats": [ ... 4 KPI cards ... ],
 *   "disasters": [ ... 6 category rows ... ],
 *   "trends": { ... monthly arrays for 4 disaster types + avgResponse ... },
 *   "volunteers": [ ... top 5 volunteers ... ]
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportsPageResponse {

    /**
     * The time period key, e.g. "LAST_30_DAYS", "Q2_2025", "YTD_2025".
     * Useful for debugging on the Angular side.
     */
    private String period;

    /**
     * Human-readable dashboard label shown below the page title,
     * e.g. "Executive dashboard - Q2 2025"
     */
    private String dashboardLabel;

    /**
     * List of 4 KPI stat cards.
     * Order: [Total Incidents, Resolved, Volunteers Active, Avg Response]
     */
    private List<ReportsSummaryResponse> stats;

    /**
     * List of 6 disaster categories with counts.
     * Order: [Flood, Hurricane, Fire, Earthquake, Medical, Other]
     */
    private List<DisasterCategoryDto> disasters;

    /**
     * Monthly trend data for the line chart (4 disaster types)
     * and bar chart (average response time per month).
     */
    private TrendsDataDto trends;

    /**
     * Top 5 volunteers ranked by tasks completed.
     */
    private List<VolunteerPerformanceDto> volunteers;
}
