package tn.esprit.reservation.dto;

import tn.esprit.reservation.entities.PaymentStatus;
import tn.esprit.reservation.entities.ReservationStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ReservationResponse(
        Long id,
        String reservationCode,
        Long eventId,
        Long userId,
        String eventTitle,
        Integer requestedSeats,
        BigDecimal unitPrice,
        BigDecimal totalAmount,
        ReservationStatus status,
        PaymentStatus paymentStatus,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        LocalDateTime cancelledAt,
        LocalDateTime refundedAt,
        BigDecimal refundAmount
) {
}
