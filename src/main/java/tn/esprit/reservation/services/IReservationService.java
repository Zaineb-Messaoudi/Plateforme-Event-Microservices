package tn.esprit.reservation.services;

import tn.esprit.reservation.dto.AvailabilityRequest;
import tn.esprit.reservation.dto.AvailabilityResponse;
import tn.esprit.reservation.dto.ReservationRequest;
import tn.esprit.reservation.dto.ReservationResponse;
import tn.esprit.reservation.entities.ReservationStatus;

import java.util.List;

public interface IReservationService {
    AvailabilityResponse saveAvailability(AvailabilityRequest request);

    AvailabilityResponse updateAvailability(Long eventId, AvailabilityRequest request);

    AvailabilityResponse getAvailabilityByEventId(Long eventId);

    List<AvailabilityResponse> getAllAvailabilities();

    ReservationResponse createReservation(ReservationRequest request);

    List<ReservationResponse> getAllReservations();

    ReservationResponse getReservationById(Long id);

    ReservationResponse getReservationByCode(String reservationCode);

    List<ReservationResponse> getReservationsByUserId(Long userId);

    List<ReservationResponse> getReservationsByEventId(Long eventId);

    List<ReservationResponse> getReservationsByStatus(ReservationStatus status);

    ReservationResponse cancelReservation(Long id);

    ReservationResponse refundReservation(Long id);
}
