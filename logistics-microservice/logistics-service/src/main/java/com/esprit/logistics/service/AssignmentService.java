package com.esprit.logistics.service;

import com.esprit.logistics.dto.AssignmentDTO;
import com.esprit.logistics.dto.LogisticsEventDTO;
import com.esprit.logistics.entity.Assignment;
import com.esprit.logistics.entity.Equipment;
import com.esprit.logistics.entity.Staff;
import com.esprit.logistics.messaging.LogisticsProducer;
import com.esprit.logistics.repository.AssignmentRepository;
import com.esprit.logistics.repository.EquipmentRepository;
import com.esprit.logistics.repository.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class AssignmentService {

    @Autowired
    private AssignmentRepository assignmentRepository;

    @Autowired
    private EquipmentRepository equipmentRepository;

    @Autowired
    private StaffRepository staffRepository;

    @Autowired
    private LogisticsProducer logisticsProducer;

    private static final Logger log = LoggerFactory.getLogger(AssignmentService.class);

    public List<Assignment> getAllAssignments() {
        return assignmentRepository.findAll();
    }

    public Optional<Assignment> getAssignmentById(Long id) {
        return assignmentRepository.findById(id);
    }

    public List<Assignment> getAssignmentsByEventId(Long eventId) {
        return assignmentRepository.findByEventId(eventId);
    }

    @Transactional
    public Assignment createAssignment(AssignmentDTO dto) {
        Equipment equipment = equipmentRepository.findById(dto.getEquipmentId())
                .orElseThrow(() -> new RuntimeException("Equipement non trouvé : " + dto.getEquipmentId()));

        Staff staff = staffRepository.findById(dto.getStaffId())
                .orElseThrow(() -> new RuntimeException("Personnel non trouvé : " + dto.getStaffId()));

        Assignment assignment = new Assignment();
        assignment.setEventId(dto.getEventId());
        assignment.setEquipment(equipment);
        assignment.setStaff(staff);
        assignment.setDate(dto.getDate());
        assignment.setStatus(Assignment.AssignmentStatus.valueOf(dto.getStatus()));

        Assignment saved = assignmentRepository.save(assignment);
        log.info("Affectation créée pour eventId={}", dto.getEventId());

        // Publier une notification RabbitMQ après création
        logisticsProducer.sendAssignmentNotification(dto);

        return saved;
    }

    public Assignment updateStatus(Long id, String status) {
        return assignmentRepository.findById(id).map(a -> {
            a.setStatus(Assignment.AssignmentStatus.valueOf(status));
            return assignmentRepository.save(a);
        }).orElseThrow(() -> new RuntimeException("Affectation non trouvée : " + id));
    }

    public void deleteAssignment(Long id) {
        assignmentRepository.deleteById(id);
    }

    /**
     * Appelé par le consommateur RabbitMQ quand un événement arrive.
     * Prépare automatiquement la logistique pour cet événement.
     */
    public void prepareLogisticsForEvent(LogisticsEventDTO eventDTO) {
        log.info("Préparation logistique pour l'événement : {} (id={})",
                eventDTO.getEventName(), eventDTO.getEventId());
        // Logique métier : vérifier disponibilité du matériel et du personnel
        // puis créer des affectations préliminaires si nécessaire
        List<Assignment> existing = assignmentRepository.findByEventId(eventDTO.getEventId());
        if (existing.isEmpty()) {
            log.info("Aucune affectation existante — prêt pour planification manuelle");
        } else {
            log.info("{} affectation(s) déjà prévue(s) pour cet événement", existing.size());
        }
    }
}
