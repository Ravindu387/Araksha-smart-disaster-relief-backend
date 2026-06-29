package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for a single KPI summary card.
 *
 * Maps to one card in the "Stats Cards Row" of the frontend.
 *
 * Example JSON:
 * {
 *   "title": "Total Incidents",
 *   "value": "1,284",
 *   "change": "+18%",
 *   "isPositive": true,
 *   "type": "incidents"
 * }
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ReportsSummaryResponse {

    /** Display title shown below the number, e.g. "Total Incidents" */
    private String title;

    /** Formatted value string, e.g. "1,284" or "13.2" */
    private String value;

    /** Change percentage string shown in the badge, e.g. "+18%" or "-28%" */
    private String change;

    /**
     * Whether the change is considered positive/good.
     * For incidents: more incidents = true (more activity tracked).
     * For response time: negative % = positive (faster response is good).
     * Angular uses this to color the badge green (emerald) or red.
     */
    private boolean isPositive;

    /**
     * Card type identifier. Angular uses this to pick which SVG icon to show.
     * Must be one of: "incidents", "resolved", "volunteers", "response"
     */
    private String type;
}
