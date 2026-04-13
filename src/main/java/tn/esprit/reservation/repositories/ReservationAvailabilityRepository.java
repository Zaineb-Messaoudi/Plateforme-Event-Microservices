package tn.esprit.reservation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.reservation.entities.ReservationAvailability;

import java.util.Optional;

@Repository
public interface ReservationAvailabilityRepository extends JpaRepository<ReservationAvailability, Long> {
    Optional<ReservationAvailability> findByEventId(Long eventId);
}
