package com.esprit.logistics.repository;

import com.esprit.logistics.entity.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EquipmentRepository extends JpaRepository<Equipment, Long> {
    List<Equipment> findByStatus(Equipment.EquipmentStatus status);
    List<Equipment> findByType(String type);
}
