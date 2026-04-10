package com.esprit.logistics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "assignment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Assignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Référence vers le MS Evenement (via Feign Client)
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "equipment_id")
    private Equipment equipment;

    @ManyToOne
    @JoinColumn(name = "staff_id")
    private Staff staff;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
    private AssignmentStatus status; // PENDING, CONFIRMED, CANCELLED

    public enum AssignmentStatus {
        PENDING, CONFIRMED, CANCELLED
    }
}
