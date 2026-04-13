package tn.esprit.reservation.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tn.esprit.reservation.dto.AvailabilityRequest;
import tn.esprit.reservation.dto.AvailabilityResponse;
import tn.esprit.reservation.dto.ReservationRequest;
import tn.esprit.reservation.dto.ReservationResponse;
import tn.esprit.reservation.entities.ReservationStatus;
import tn.esprit.reservation.services.IReservationService;

import java.util.List;

@RestController
@RequestMapping("/api/reservations")
@RequiredArgsConstructor
public class ReservationController {
    private final IReservationService reservationService;

    @PostMapping("/availability")
    @ResponseStatus(HttpStatus.CREATED)
    public AvailabilityResponse saveAvailability(@Valid @RequestBody AvailabilityRequest request) {
        return reservationService.saveAvailability(request);
    }

    @PutMapping("/availability/{eventId}")
    public AvailabilityResponse updateAvailability(@PathVariable Long eventId,
                                                   @Valid @RequestBody AvailabilityRequest request) {
        return reservationService.updateAvailability(eventId, request);
    }

    @GetMapping("/availability")
    public List<AvailabilityResponse> getAllAvailabilities() {
        return reservationService.getAllAvailabilities();
    }

    @GetMapping("/availability/{eventId}")
    public AvailabilityResponse getAvailability(@PathVariable Long eventId) {
        return reservationService.getAvailabilityByEventId(eventId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReservationResponse createReservation(@Valid @RequestBody ReservationRequest request) {
        return reservationService.createReservation(request);
    }

    @GetMapping
    public List<ReservationResponse> findAllReservation() {
        return reservationService.getAllReservations();
    }

    @GetMapping("/{idReservation}")
    public ReservationResponse findById(@PathVariable Long idReservation) {
        return reservationService.getReservationById(idReservation);
    }

    @GetMapping("/code/{reservationCode}")
    public ReservationResponse findByCode(@PathVariable String reservationCode) {
        return reservationService.getReservationByCode(reservationCode);
    }

    @GetMapping("/user/{userId}")
    public List<ReservationResponse> findByUser(@PathVariable Long userId) {
        return reservationService.getReservationsByUserId(userId);
    }

    @GetMapping("/event/{eventId}")
    public List<ReservationResponse> findByEvent(@PathVariable Long eventId) {
        return reservationService.getReservationsByEventId(eventId);
    }

    @GetMapping("/status/{status}")
    public List<ReservationResponse> findByStatus(@PathVariable ReservationStatus status) {
        return reservationService.getReservationsByStatus(status);
    }

    @PutMapping("/{idReservation}/cancel")
    public ReservationResponse cancelReservation(@PathVariable Long idReservation) {
        return reservationService.cancelReservation(idReservation);
    }

    @PutMapping("/{idReservation}/refund")
    public ReservationResponse refundReservation(@PathVariable Long idReservation) {
        return reservationService.refundReservation(idReservation);
    }
}
