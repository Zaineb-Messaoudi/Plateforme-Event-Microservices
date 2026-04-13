package tn.esprit.reservation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record AvailabilityRequest(
        @NotNull(message = "eventId est obligatoire")
        Long eventId,
        @NotBlank(message = "eventTitle est obligatoire")
        String eventTitle,
        @NotNull(message = "totalSeats est obligatoire")
        @Positive(message = "totalSeats doit etre superieur a 0")
        Integer totalSeats
) {
}
