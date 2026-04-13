package tn.esprit.reservation.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.esprit.reservation.dto.AvailabilityRequest;
import tn.esprit.reservation.dto.AvailabilityResponse;
import tn.esprit.reservation.dto.ReservationRequest;
import tn.esprit.reservation.dto.ReservationResponse;
import tn.esprit.reservation.entities.PaymentStatus;
import tn.esprit.reservation.entities.Reservation;
import tn.esprit.reservation.entities.ReservationAvailability;
import tn.esprit.reservation.entities.ReservationStatus;
import tn.esprit.reservation.exceptions.BusinessException;
import tn.esprit.reservation.exceptions.ResourceNotFoundException;
import tn.esprit.reservation.repositories.ReservationAvailabilityRepository;
import tn.esprit.reservation.repositories.ReservationRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
@Transactional
public class ReservationServiceImpl implements IReservationService {
    private final ReservationRepository reservationRepository;
    private final ReservationAvailabilityRepository availabilityRepository;

    @Override
    public AvailabilityResponse saveAvailability(AvailabilityRequest request) {
        ReservationAvailability availability = availabilityRepository.findByEventId(request.eventId())
                .orElse(ReservationAvailability.builder()
                        .eventId(request.eventId())
                        .reservedSeats(0)
                        .build());

        if (availability.getReservedSeats() > request.totalSeats()) {
            throw new BusinessException("Le nombre total de places ne peut pas etre inferieur aux places deja reservees.");
        }

        availability.setEventTitle(request.eventTitle());
        availability.setTotalSeats(request.totalSeats());
        return toAvailabilityResponse(availabilityRepository.save(availability));
    }

    @Override
    public AvailabilityResponse updateAvailability(Long eventId, AvailabilityRequest request) {
        ReservationAvailability availability = getAvailabilityEntity(eventId);

        if (!eventId.equals(request.eventId())) {
            throw new BusinessException("L'identifiant de l'evenement dans l'URL et dans le corps doit etre identique.");
        }
        if (availability.getReservedSeats() > request.totalSeats()) {
            throw new BusinessException("Impossible de reduire la capacite sous le nombre de places deja reservees.");
        }

        availability.setEventTitle(request.eventTitle());
        availability.setTotalSeats(request.totalSeats());
        return toAvailabilityResponse(availabilityRepository.save(availability));
    }

    @Override
    @Transactional(readOnly = true)
    public AvailabilityResponse getAvailabilityByEventId(Long eventId) {
        return toAvailabilityResponse(getAvailabilityEntity(eventId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AvailabilityResponse> getAllAvailabilities() {
        return availabilityRepository.findAll()
                .stream()
                .map(this::toAvailabilityResponse)
                .toList();
    }

    @Override
    public ReservationResponse createReservation(ReservationRequest request) {
        ReservationAvailability availability = getAvailabilityEntity(request.eventId());
        int remainingSeats = availability.getTotalSeats() - availability.getReservedSeats();

        if (request.requestedSeats() > remainingSeats) {
            throw new BusinessException("Places insuffisantes. Restant: " + remainingSeats);
        }

        BigDecimal unitPrice = request.unitPrice() == null ? BigDecimal.ZERO : request.unitPrice();
        BigDecimal totalAmount = unitPrice.multiply(BigDecimal.valueOf(request.requestedSeats()));
        LocalDateTime now = LocalDateTime.now();

        Reservation reservation = Reservation.builder()
                .reservationCode(generateReservationCode())
                .eventId(availability.getEventId())
                .userId(request.userId())
                .eventTitle(availability.getEventTitle())
                .requestedSeats(request.requestedSeats())
                .unitPrice(unitPrice)
                .totalAmount(totalAmount)
                .status(ReservationStatus.CONFIRMED)
                .paymentStatus(PaymentStatus.PAID)
                .createdAt(now)
                .updatedAt(now)
                .build();

        availability.setReservedSeats(availability.getReservedSeats() + request.requestedSeats());
        availabilityRepository.save(availability);

        return toReservationResponse(reservationRepository.save(reservation));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getAllReservations() {
        return reservationRepository.findAll()
                .stream()
                .map(this::toReservationResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationById(Long id) {
        return toReservationResponse(getReservationEntity(id));
    }

    @Override
    @Transactional(readOnly = true)
    public ReservationResponse getReservationByCode(String reservationCode) {
        return toReservationResponse(reservationRepository.findByReservationCode(reservationCode)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune reservation trouvee avec le code " + reservationCode)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByUserId(Long userId) {
        return reservationRepository.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::toReservationResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByEventId(Long eventId) {
        return reservationRepository.findByEventIdOrderByCreatedAtDesc(eventId)
                .stream()
                .map(this::toReservationResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReservationResponse> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatusOrderByCreatedAtDesc(status)
                .stream()
                .map(this::toReservationResponse)
                .toList();
    }

    @Override
    public ReservationResponse cancelReservation(Long id) {
        Reservation reservation = getReservationEntity(id);

        if (reservation.getStatus() == ReservationStatus.CANCELLED || reservation.getStatus() == ReservationStatus.REFUNDED) {
            throw new BusinessException("Cette reservation est deja annulee ou remboursee.");
        }

        ReservationAvailability availability = getAvailabilityEntity(reservation.getEventId());
        availability.setReservedSeats(Math.max(0, availability.getReservedSeats() - reservation.getRequestedSeats()));

        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setCancelledAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        availabilityRepository.save(availability);
        return toReservationResponse(reservationRepository.save(reservation));
    }

    @Override
    public ReservationResponse refundReservation(Long id) {
        Reservation reservation = getReservationEntity(id);

        if (reservation.getStatus() != ReservationStatus.CANCELLED) {
            throw new BusinessException("Le remboursement n'est possible qu'apres annulation.");
        }
        if (reservation.getPaymentStatus() == PaymentStatus.REFUNDED) {
            throw new BusinessException("Cette reservation a deja ete remboursee.");
        }

        reservation.setStatus(ReservationStatus.REFUNDED);
        reservation.setPaymentStatus(PaymentStatus.REFUNDED);
        reservation.setRefundAmount(reservation.getTotalAmount());
        reservation.setRefundedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        return toReservationResponse(reservationRepository.save(reservation));
    }

    private Reservation getReservationEntity(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune reservation trouvee avec l'id " + id));
    }

    private ReservationAvailability getAvailabilityEntity(Long eventId) {
        return availabilityRepository.findByEventId(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Aucune disponibilite trouvee pour l'evenement " + eventId));
    }

    private AvailabilityResponse toAvailabilityResponse(ReservationAvailability availability) {
        return new AvailabilityResponse(
                availability.getEventId(),
                availability.getEventTitle(),
                availability.getTotalSeats(),
                availability.getReservedSeats(),
                availability.getAvailableSeats()
        );
    }

    private ReservationResponse toReservationResponse(Reservation reservation) {
        return new ReservationResponse(
                reservation.getId(),
                reservation.getReservationCode(),
                reservation.getEventId(),
                reservation.getUserId(),
                reservation.getEventTitle(),
                reservation.getRequestedSeats(),
                reservation.getUnitPrice(),
                reservation.getTotalAmount(),
                reservation.getStatus(),
                reservation.getPaymentStatus(),
                reservation.getCreatedAt(),
                reservation.getUpdatedAt(),
                reservation.getCancelledAt(),
                reservation.getRefundedAt(),
                reservation.getRefundAmount()
        );
    }

    private String generateReservationCode() {
        return "RES-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
