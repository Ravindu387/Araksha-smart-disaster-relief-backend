package org.example.arakshasmartdisasterreliefbackend.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.arakshasmartdisasterreliefbackend.entity.Shelter;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for dynamic Shelter search and filtering.
 * Supports: keyword (name/address), status (Available/Limited/Full),
 *           minCapacity, maxCapacity.
 */
public class ShelterSpecification {

    private ShelterSpecification() {}

    /**
     * Build a Specification from optional filter parameters.
     *
     * @param keyword     partial match on name or address
     * @param status      exact match on status (Available/Limited/Full)
     * @param minCapacity capacity >= minCapacity
     * @param maxCapacity capacity <= maxCapacity
     * @return combined Specification (AND of all active filters)
     */
    public static Specification<Shelter> build(
            String keyword,
            String status,
            Integer minCapacity,
            Integer maxCapacity) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ── Keyword: name or address ──
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase().trim() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("address")), like)
                ));
            }

            // ── Status filter ──
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status.trim()));
            }

            // ── Capacity range filters ──
            if (minCapacity != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("capacity"), minCapacity));
            }

            if (maxCapacity != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("capacity"), maxCapacity));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
