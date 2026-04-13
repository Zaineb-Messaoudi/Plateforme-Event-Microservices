package tn.esprit.reservation.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservation_availability", uniqueConstraints = {
        @UniqueConstraint(columnNames = "eventId")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationAvailability {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long eventId;

    @Column(nullable = false)
    private String eventTitle;

    @Column(nullable = false)
    private Integer totalSeats;

    @Column(nullable = false)
    private Integer reservedSeats;

    @Transient
    public Integer getAvailableSeats() {
        return totalSeats - reservedSeats;
    }
}
