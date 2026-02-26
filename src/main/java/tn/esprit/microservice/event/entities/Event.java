package tn.esprit.microservice.event.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String description;
    private String category;
    private String location;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private int capacity;
    private double price;

    private boolean published;
}