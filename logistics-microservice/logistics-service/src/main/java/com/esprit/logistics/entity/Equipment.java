package com.esprit.logistics.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "equipment")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String type; // ex: chaise, scène, micro, écran

    private int quantity;

    @Enumerated(EnumType.STRING)
    private EquipmentStatus status; // AVAILABLE, RESERVED, MAINTENANCE

    private String location;

    public enum EquipmentStatus {
        AVAILABLE, RESERVED, MAINTENANCE
    }
}
