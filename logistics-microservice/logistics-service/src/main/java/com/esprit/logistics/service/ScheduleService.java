package com.esprit.logistics.service;

import com.esprit.logistics.entity.Assignment;
import com.esprit.logistics.entity.Schedule;
import com.esprit.logistics.repository.AssignmentRepository;
import com.esprit.logistics.repository.ScheduleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ScheduleService {

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private AssignmentRepository assignmentRepository;

    private static final Logger log = LoggerFactory.getLogger(ScheduleService.class);

    public List<Schedule> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public Optional<Schedule> getScheduleById(Long id) {
        return scheduleRepository.findById(id);
    }

    public List<Schedule> getByAssignmentId(Long assignmentId) {
        return scheduleRepository.findByAssignmentId(assignmentId);
    }

    public List<Schedule> getByPriority(Schedule.Priority priority) {
        return scheduleRepository.findByPriority(priority);
    }

    public Schedule saveSchedule(Long assignmentId, Schedule schedule) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new RuntimeException("Affectation non trouvée : " + assignmentId));
        schedule.setAssignment(assignment);
        log.info("Planning créé pour affectation id={}", assignmentId);
        return scheduleRepository.save(schedule);
    }

    public Schedule updateSchedule(Long id, Schedule updated) {
        return scheduleRepository.findById(id).map(s -> {
            s.setStartTime(updated.getStartTime());
            s.setEndTime(updated.getEndTime());
            s.setPriority(updated.getPriority());
            return scheduleRepository.save(s);
        }).orElseThrow(() -> new RuntimeException("Planning non trouvé : " + id));
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.deleteById(id);
    }
}
