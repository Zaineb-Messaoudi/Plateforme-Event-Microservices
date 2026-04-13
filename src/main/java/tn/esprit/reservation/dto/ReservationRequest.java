package tn.esprit.reservation.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ReservationRequest(
        @NotNull(message = "eventId est obligatoire")
        Long eventId,
        @NotNull(message = "userId est obligatoire")
        Long userId,
        @NotNull(message = "requestedSeats est obligatoire")
        @Positive(message = "requestedSeats doit etre superieur a 0")
        Integer requestedSeats,
        @DecimalMin(value = "0.0", inclusive = true, message = "unitPrice doit etre positif ou nul")
        BigDecimal unitPrice
) {
}
