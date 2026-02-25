package tn.esprit.microservices.feedback;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    List<Feedback> findByEventIdAndStatus(Long eventId, FeedbackStatus status);

    List<Feedback> findByUserIdAndStatus(Long userId, FeedbackStatus status);

    List<Feedback> findByOrganizerIdAndStatus(Long organizerId, FeedbackStatus status);

    Optional<Feedback> findByUserIdAndEventId(Long userId, Long eventId);

    boolean existsByUserIdAndEventId(Long userId, Long eventId);

    // --- Statistics queries ---

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.eventId = :eventId AND f.status = 'ACTIVE'")
    Optional<Double> findAverageRatingByEventId(@Param("eventId") Long eventId);

    @Query("SELECT COUNT(f) FROM Feedback f WHERE f.eventId = :eventId AND f.status = 'ACTIVE'")
    Long countActiveByEventId(@Param("eventId") Long eventId);

    /**
     * Returns pairs [ratingFloor, count] for distribution buckets (1-5).
     * Uses FLOOR so 3.5 → bucket 3, 4.0 → bucket 4.
     */
    @Query("SELECT FLOOR(f.rating), COUNT(f) FROM Feedback f " +
            "WHERE f.eventId = :eventId AND f.status = 'ACTIVE' " +
            "GROUP BY FLOOR(f.rating) ORDER BY FLOOR(f.rating)")
    List<Object[]> findRatingDistributionByEventId(@Param("eventId") Long eventId);

    @Query("SELECT AVG(f.rating) FROM Feedback f WHERE f.organizerId = :organizerId AND f.status = 'ACTIVE'")
    Optional<Double> findAverageRatingByOrganizerId(@Param("organizerId") Long organizerId);
}