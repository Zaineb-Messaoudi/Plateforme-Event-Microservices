package com.esprit.logistics.service;

import com.esprit.logistics.entity.Staff;
import com.esprit.logistics.repository.StaffRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StaffService {

    @Autowired
    private StaffRepository staffRepository;

    private static final Logger log = LoggerFactory.getLogger(StaffService.class);

    public List<Staff> getAllStaff() {
        return staffRepository.findAll();
    }

    public Optional<Staff> getStaffById(Long id) {
        return staffRepository.findById(id);
    }

    public Staff saveStaff(Staff staff) {
        log.info("Sauvegarde personnel : {}", staff.getName());
        return staffRepository.save(staff);
    }

    public Staff updateStaff(Long id, Staff updated) {
        return staffRepository.findById(id).map(s -> {
            s.setName(updated.getName());
            s.setRole(updated.getRole());
            s.setAvailability(updated.isAvailability());
            s.setContact(updated.getContact());
            return staffRepository.save(s);
        }).orElseThrow(() -> new RuntimeException("Personnel non trouvé : " + id));
    }

    public void deleteStaff(Long id) {
        staffRepository.deleteById(id);
        log.info("Personnel supprimé : id={}", id);
    }

    public List<Staff> getAvailableStaff() {
        return staffRepository.findByAvailability(true);
    }

    public List<Staff> getByRole(String role) {
        return staffRepository.findByRole(role);
    }
}
