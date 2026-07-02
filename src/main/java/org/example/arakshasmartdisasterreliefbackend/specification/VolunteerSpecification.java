package org.example.arakshasmartdisasterreliefbackend.specification;

import jakarta.persistence.criteria.*;
import org.example.arakshasmartdisasterreliefbackend.entity.Volunteer;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA Specification for dynamic Volunteer search and filtering.
 * Supports: keyword (name/phone/location), status, district, skill.
 */
public class VolunteerSpecification {

    private VolunteerSpecification() {}

    /**
     * Build a Specification from optional filter parameters.
     *
     * @param keyword  searches name, phone, location
     * @param status   exact match on status field
     * @param district partial match on location field
     * @param skill    partial match on any skill in skills collection
     * @return combined Specification (AND of all active filters)
     */
    public static Specification<Volunteer> build(
            String keyword,
            String status,
            String district,
            String skill) {

        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // ── Keyword: search across name, phone, location ──
            if (keyword != null && !keyword.isBlank()) {
                String like = "%" + keyword.toLowerCase().trim() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), like),
                        cb.like(cb.lower(root.get("phone")), like),
                        cb.like(cb.lower(root.get("location")), like)
                ));
            }

            // ── Status filter: exact match ──
            if (status != null && !status.isBlank()) {
                predicates.add(cb.equal(root.get("status"), status.trim()));
            }

            // ── District filter: partial match on location ──
            if (district != null && !district.isBlank()) {
                predicates.add(
                        cb.like(cb.lower(root.get("location")),
                                "%" + district.toLowerCase().trim() + "%")
                );
            }

            // ── Skill filter: EXISTS subquery on skills collection ──
            // Using a subquery avoids duplicate rows that a plain JOIN would cause,
            // which would break the COUNT query used for pagination.
            if (skill != null && !skill.isBlank()) {
                String skillLike = "%" + skill.toLowerCase().trim() + "%";

                Subquery<Long> skillSubquery = query.subquery(Long.class);
                Root<Volunteer> skillRoot = skillSubquery.from(Volunteer.class);
                Join<Volunteer, String> skillJoin = skillRoot.join("skills", JoinType.INNER);

                skillSubquery.select(skillRoot.get("id")).where(
                        cb.and(
                                cb.equal(skillRoot.get("id"), root.get("id")),
                                cb.like(cb.lower(skillJoin), skillLike)
                        )
                );
                predicates.add(cb.exists(skillSubquery));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
