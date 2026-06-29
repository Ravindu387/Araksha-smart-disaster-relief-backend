package org.example.arakshasmartdisasterreliefbackend.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * DTO for one row in the Disaster Category Breakdown section.
 *
 * Maps to one item in the progress-bars list AND one slice in the donut chart.
 *
 * Example JSON:
 * {
 *   "name": "Flood",
 *   "count": 480
 * }
 *
 * Note: The Angular component will compute the Tailwind widthClass and color
 * class from the name — we do NOT send those from the backend.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DisasterCategoryDto {

    /** Category name, e.g. "Flood", "Fire", "Hurricane" */
    private String name;

    /** Total incident count for this category in the selected time period */
    private long count;
}
