package com.esprit.logistics.repository;

import com.esprit.logistics.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByAssignmentId(Long assignmentId);
    List<Schedule> findByPriority(Schedule.Priority priority);
}
