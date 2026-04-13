package tn.esprit.reservation.dto;

public record AvailabilityResponse(
        Long eventId,
        String eventTitle,
        Integer totalSeats,
        Integer reservedSeats,
        Integer availableSeats
) {
}
