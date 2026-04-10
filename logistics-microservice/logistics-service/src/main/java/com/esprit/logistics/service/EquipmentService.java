package com.esprit.logistics.service;

import com.esprit.logistics.entity.Equipment;
import com.esprit.logistics.repository.EquipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class EquipmentService {

    @Autowired
    private EquipmentRepository equipmentRepository;

    private static final Logger log = LoggerFactory.getLogger(EquipmentService.class);

    public List<Equipment> getAllEquipments() {
        return equipmentRepository.findAll();
    }

    public Optional<Equipment> getEquipmentById(Long id) {
        return equipmentRepository.findById(id);
    }

    public Equipment saveEquipment(Equipment equipment) {
        log.info("Sauvegarde équipement : {}", equipment.getName());
        return equipmentRepository.save(equipment);
    }

    public Equipment updateEquipment(Long id, Equipment updated) {
        return equipmentRepository.findById(id).map(eq -> {
            eq.setName(updated.getName());
            eq.setType(updated.getType());
            eq.setQuantity(updated.getQuantity());
            eq.setStatus(updated.getStatus());
            eq.setLocation(updated.getLocation());
            return equipmentRepository.save(eq);
        }).orElseThrow(() -> new RuntimeException("Equipement non trouvé : " + id));
    }

    public void deleteEquipment(Long id) {
        equipmentRepository.deleteById(id);
        log.info("Equipement supprimé : id={}", id);
    }

    public List<Equipment> getByStatus(Equipment.EquipmentStatus status) {
        return equipmentRepository.findByStatus(status);
    }

    public List<Equipment> getByType(String type) {
        return equipmentRepository.findByType(type);
    }
}
