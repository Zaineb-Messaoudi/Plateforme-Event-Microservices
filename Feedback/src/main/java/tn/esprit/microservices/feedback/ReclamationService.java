package tn.esprit.microservices.feedback;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReclamationService {

    private final ReclamationRepository reclamationRepository;

    private static final Set<ReclamationStatus> ORGANIZER_ALLOWED_STATUSES =
            Set.of(ReclamationStatus.RESOLVED, ReclamationStatus.REJECTED, ReclamationStatus.IN_PROGRESS);


    @Transactional
    public ReclamationResponse create(ReclamationRequest request) {
        Reclamation reclamation = Reclamation.builder()
                .userId(request.getUserId())
                .eventId(request.getEventId())
                .organizerId(request.getOrganizerId())
                .subject(request.getSubject())
                .description(request.getDescription())
                .build();

        return toResponse(reclamationRepository.save(reclamation));
    }

    @Transactional(readOnly = true)
    public ReclamationResponse findById(Long id) {
        return toResponse(getById(id));
    }

    @Transactional(readOnly = true)
    public List<ReclamationResponse> findByUserId(Long userId) {
        return reclamationRepository.findByUserId(userId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReclamationResponse> findByOrganizerId(Long organizerId) {
        return reclamationRepository.findByOrganizerId(organizerId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReclamationResponse> findByOrganizerIdAndStatus(Long organizerId, ReclamationStatus status) {
        return reclamationRepository.findByOrganizerIdAndStatus(organizerId, status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReclamationResponse> findByEventId(Long eventId) {
        return reclamationRepository.findByEventId(eventId)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ReclamationResponse> findByStatus(ReclamationStatus status) {
        return reclamationRepository.findByStatus(status)
                .stream().map(this::toResponse).collect(Collectors.toList());
    }

    @Transactional
    public ReclamationResponse respondToReclamation(Long id, OrganizerResponseRequest request) {
        Reclamation reclamation = getById(id);

        if (reclamation.getStatus() == ReclamationStatus.CLOSED) {
            throw new RuntimeException("Cannot respond to a CLOSED reclamation.");
        }
        if (!ORGANIZER_ALLOWED_STATUSES.contains(request.getStatus())) {
            throw new RuntimeException("Organizer can only set status to: " + ORGANIZER_ALLOWED_STATUSES);
        }

        reclamation.setOrganizerResponse(request.getResponse());
        reclamation.setStatus(request.getStatus());
        reclamation.setRespondedAt(LocalDateTime.now());

        return toResponse(reclamationRepository.save(reclamation));
    }

    @Transactional
    public ReclamationResponse closeReclamation(Long id) {
        Reclamation reclamation = getById(id);
        reclamation.setStatus(ReclamationStatus.CLOSED);
        return toResponse(reclamationRepository.save(reclamation));
    }

    private Reclamation getById(Long id) {
        return reclamationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Reclamation not found with id: " + id));
    }

    private ReclamationResponse toResponse(Reclamation r) {
        return ReclamationResponse.builder()
                .id(r.getId())
                .userId(r.getUserId())
                .eventId(r.getEventId())
                .organizerId(r.getOrganizerId())
                .subject(r.getSubject())
                .description(r.getDescription())
                .status(r.getStatus())
                .organizerResponse(r.getOrganizerResponse())
                .respondedAt(r.getRespondedAt())
                .createdAt(r.getCreatedAt())
                .updatedAt(r.getUpdatedAt())
                .build();
    }
}