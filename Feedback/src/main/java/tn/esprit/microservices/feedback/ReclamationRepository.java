package tn.esprit.microservices.feedback;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReclamationRepository extends JpaRepository<Reclamation, Long> {

    List<Reclamation> findByUserId(Long userId);

    List<Reclamation> findByEventId(Long eventId);

    List<Reclamation> findByOrganizerId(Long organizerId);

    List<Reclamation> findByOrganizerIdAndStatus(Long organizerId, ReclamationStatus status);

    List<Reclamation> findByStatus(ReclamationStatus status);

    long countByOrganizerIdAndStatus(Long organizerId, ReclamationStatus status);
}