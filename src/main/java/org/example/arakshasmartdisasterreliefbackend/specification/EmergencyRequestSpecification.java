package org.example.arakshasmartdisasterreliefbackend.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.arakshasmartdisasterreliefbackend.entity.EmergencyRequest;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for dynamic EmergencyRequest search and filtering.
 * Supports: keyword (requestId/citizenName/location), status, priority,
 *           disasterType, district, date range.
 */
public class EmergencyRequestSpecification {

    private EmergencyRequestSpecification() {}

    /**
     * Build a Specification from optional filter parameters.
     *
     * @param keyword      searches requestId, citizenName, location
     * @param status       exact match on status
     * @param priority     exact match on priority
     * @param disasterType exact match on emergencyType
     * @param district     partial match on location
     * @param dateFrom     requestTime >= dateFrom (inclusive)
     * @param dateTo       requestTime <= dateTo (end of day, inclusive)
     * @return combined Specification (AND of all active filters)
     */
    public static Specification<EmergencyRequest> build(
            String keyword,
            String status,
            String priority,
            String disasterType,
            String district,
            LocalDate dateFrom,
            LocalDate dateTo) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ── Keyword: requestId, citizenName, location ──
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase().trim() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("requestId")), like),
                        cb.like(cb.lower(root.get("citizenName")), like),
                        cb.like(cb.lower(root.get("location")), like)
                ));
            }

            // ── Status filter ──
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status.trim()));
            }

            // ── Priority filter ──
            if (priority != null && !priority.isBlank()) {
                predicates.add(cb.equal(root.get("priority"), priority.trim()));
            }

            // ── Disaster type filter ──
            if (disasterType != null && !disasterType.isBlank()) {
                predicates.add(cb.equal(root.get("emergencyType"), disasterType.trim()));
            }

            // ── District filter: partial match on location ──
            if (district != null && !district.isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("location")),
                                "%" + district.toLowerCase().trim() + "%")
                );
            }

            // ── Date range filter: requestTime ──
            if (dateFrom != null) {
                LocalDateTime from = dateFrom.atStartOfDay();
                predicates.add(cb.greaterThanOrEqualTo(root.get("requestTime"), from));
            }

            if (dateTo != null) {
                LocalDateTime to = dateTo.atTime(23, 59, 59);
                predicates.add(cb.lessThanOrEqualTo(root.get("requestTime"), to));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
