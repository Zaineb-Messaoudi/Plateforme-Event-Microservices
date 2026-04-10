package com.esprit.logistics.controller;

import com.esprit.logistics.entity.Schedule;
import com.esprit.logistics.service.ScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/schedules")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @GetMapping
    public List<Schedule> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Schedule> getScheduleById(@PathVariable Long id) {
        return scheduleService.getScheduleById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/assignment/{assignmentId}")
    public List<Schedule> getByAssignmentId(@PathVariable Long assignmentId) {
        return scheduleService.getByAssignmentId(assignmentId);
    }

    @GetMapping("/priority/{priority}")
    public List<Schedule> getByPriority(@PathVariable Schedule.Priority priority) {
        return scheduleService.getByPriority(priority);
    }

    @PostMapping("/assignment/{assignmentId}")
    public ResponseEntity<Schedule> createSchedule(@PathVariable Long assignmentId,
                                                    @RequestBody Schedule schedule) {
        return ResponseEntity.ok(scheduleService.saveSchedule(assignmentId, schedule));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Schedule> updateSchedule(@PathVariable Long id,
                                                    @RequestBody Schedule schedule) {
        return ResponseEntity.ok(scheduleService.updateSchedule(id, schedule));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.noContent().build();
    }
}
