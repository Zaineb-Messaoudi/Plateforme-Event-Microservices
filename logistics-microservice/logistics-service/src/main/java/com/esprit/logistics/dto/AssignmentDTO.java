package com.esprit.logistics.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

/**
 * DTO pour la création/mise à jour d'une affectation.
 * Reçoit les IDs au lieu des entités complètes.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignmentDTO {
    private Long eventId;
    private Long equipmentId;
    private Long staffId;
    private LocalDate date;
    private String status; // PENDING, CONFIRMED, CANCELLED
}
