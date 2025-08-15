package com.example.demo.repository;

import com.example.demo.model.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
    List<LeaveApplication> findByUser_Id(Long userId);
    List<LeaveApplication> findByUser_Manager_Id(Long managerId);

    boolean existsByUser_IdAndEndDateAfterAndStartDateBefore(Long userId, LocalDate start, LocalDate end);
}
