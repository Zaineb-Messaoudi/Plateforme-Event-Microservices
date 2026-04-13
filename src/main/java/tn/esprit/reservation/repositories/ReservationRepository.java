package tn.esprit.reservation.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.reservation.entities.Reservation;
import tn.esprit.reservation.entities.ReservationStatus;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    Optional<Reservation> findByReservationCode(String reservationCode);

    List<Reservation> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<Reservation> findByEventIdOrderByCreatedAtDesc(Long eventId);

    List<Reservation> findByStatusOrderByCreatedAtDesc(ReservationStatus status);
}
