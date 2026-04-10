package com.esprit.logistics.repository;

import com.esprit.logistics.entity.Assignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {
    List<Assignment> findByEventId(Long eventId);
    List<Assignment> findByStatus(Assignment.AssignmentStatus status);
}
