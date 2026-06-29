package org.example.arakshasmartdisasterreliefbackend.repository;

import org.example.arakshasmartdisasterreliefbackend.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository for querying the incidents table.
 *
 * Spring Data JPA auto-generates the SQL for standard methods.
 * Custom @Query methods use JPQL (Java-style queries, not raw SQL).
 */
@Repository
public interface IncidentRepository extends JpaRepository<Incident, Long> {

    /**
     * Count how many incidents fall within a date range.
     * Used for the "Total Incidents" KPI card.
     *
     * @param from  start of the date range (inclusive)
     * @param to    end of the date range (inclusive)
     */
    @Query("SELECT COUNT(i) FROM Incident i WHERE i.reportedAt BETWEEN :from AND :to")
    long countByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    /**
     * Count how many incidents with status = 'RESOLVED' fall within a date range.
     * Used for the "Resolved" KPI card.
     *
     * @param status  the status string — pass "RESOLVED"
     * @param from    start of the date range
     * @param to      end of the date range
     */
    @Query("SELECT COUNT(i) FROM Incident i WHERE i.status = :status AND i.reportedAt BETWEEN :from AND :to")
    long countByStatusAndDateRange(
            @Param("status") String status,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    /**
     * Calculate the average response time (in minutes) for incidents in a date range.
     * Used for the "Avg Response (min)" KPI card.
     * Only counts incidents that have a responseTimeMinutes value (not null).
     *
     * @param from  start of the date range
     * @param to    end of the date range
     */
    @Query("SELECT AVG(i.responseTimeMinutes) FROM Incident i WHERE i.responseTimeMinutes IS NOT NULL AND i.reportedAt BETWEEN :from AND :to")
    Double avgResponseTimeByDateRange(
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    /**
     * Count incidents of a specific type (e.g. FLOOD, FIRE) within a date range.
     * Used for the "Disaster Category Breakdown" donut chart and progress bars.
     *
     * @param type  the disaster type string (e.g. "FLOOD", "FIRE")
     * @param from  start of the date range
     * @param to    end of the date range
     */
    @Query("SELECT COUNT(i) FROM Incident i WHERE i.type = :type AND i.reportedAt BETWEEN :from AND :to")
    long countByTypeAndDateRange(
            @Param("type") String type,
            @Param("from") LocalDateTime from,
            @Param("to") LocalDateTime to
    );

    /**
     * Find all incidents of a specific type reported in a specific calendar month.
     * Used to build the monthly trend arrays for the line chart.
     *
     * @param type   the disaster type (e.g. "FLOOD")
     * @param year   the calendar year (e.g. 2025)
     * @param month  the calendar month (1 = January … 12 = December)
     */
    @Query("SELECT COUNT(i) FROM Incident i WHERE i.type = :type AND YEAR(i.reportedAt) = :year AND MONTH(i.reportedAt) = :month")
    long countByTypeAndMonth(
            @Param("type") String type,
            @Param("year") int year,
            @Param("month") int month
    );

    /**
     * Calculate the average response time for a specific calendar month.
     * Used to build the monthly average response time bar chart.
     *
     * @param year   the calendar year
     * @param month  the calendar month (1–12)
     */
    @Query("SELECT AVG(i.responseTimeMinutes) FROM Incident i WHERE i.responseTimeMinutes IS NOT NULL AND YEAR(i.reportedAt) = :year AND MONTH(i.reportedAt) = :month")
    Double avgResponseTimeByMonth(
            @Param("year") int year,
            @Param("month") int month
    );

    /**
     * Find all incidents reported within a specific date range.
     * Used internally by the service layer.
     *
     * @param from  start datetime
     * @param to    end datetime
     */
    List<Incident> findByReportedAtBetween(LocalDateTime from, LocalDateTime to);
}
