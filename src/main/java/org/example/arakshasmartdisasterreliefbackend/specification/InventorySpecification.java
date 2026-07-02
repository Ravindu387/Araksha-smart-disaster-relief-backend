package org.example.arakshasmartdisasterreliefbackend.specification;

import jakarta.persistence.criteria.Predicate;
import org.example.arakshasmartdisasterreliefbackend.entity.Inventory;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for dynamic Inventory search and filtering.
 * Supports: keyword (name), category, stockStatus (available/low/out).
 *
 * Stock status logic:
 *   "available"  → count > minStock
 *   "low"        → 0 < count <= minStock
 *   "out"        → count = 0
 */
public class InventorySpecification {

    private InventorySpecification() {}

    /**
     * Build a Specification from optional filter parameters.
     *
     * @param keyword     partial match on name
     * @param category    exact match on category
     * @param stockStatus "available" | "low" | "out"
     * @return combined Specification (AND of all active filters)
     */
    public static Specification<Inventory> build(
            String keyword,
            String category,
            String stockStatus) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ── Keyword: name ──
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase().trim() + "%";
                predicates.add(cb.like(cb.lower(root.get("name")), like));
            }

            // ── Category filter: exact match ──
            if (category != null && !category.isBlank()) {
                predicates.add(cb.equal(root.get("category"), category.trim()));
            }

            // ── Stock Status filter (derived from count vs minStock) ──
            if (stockStatus != null && !stockStatus.isBlank()) {
                switch (stockStatus.toLowerCase().trim()) {
                    case "available" ->
                        // count > minStock
                        predicates.add(
                            cb.gt(root.get("count"), root.get("minStock"))
                        );
                    case "low" ->
                        // 0 < count <= minStock
                        predicates.add(cb.and(
                            cb.gt(root.get("count"), 0),
                            cb.le(root.get("count"), root.get("minStock"))
                        ));

                    case "out" ->
                        // count = 0
                        predicates.add(cb.equal(root.<Integer>get("count"), 0));
                    default -> { /* ignore unknown values */ }
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
