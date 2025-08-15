package com.example.demo.service;

import com.example.demo.model.LeaveType;
import com.example.demo.repository.LeaveTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.logging.Logger;

@Service
public class LeaveTypeService {

    private static final Logger logger = Logger.getLogger(LeaveTypeService.class.getName());

    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    // Get all leave types
    public List<LeaveType> getAllLeaveTypes() {
        logger.info("Fetching all leave types");
        return leaveTypeRepository.findAll();
    }

    // Get leave type by ID
    public Optional<LeaveType> getLeaveTypeById(Long id) {
        logger.info("Fetching leave type with id: " + id);
        return leaveTypeRepository.findById(id);
    }

    // Create new leave type
    public LeaveType createLeaveType(LeaveType leaveType) {
        logger.info("Creating new leave type: " + leaveType.getName());

        if (leaveType.getName() == null || leaveType.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Leave type name cannot be empty");
        }

        // Prevent duplicate leave type names
        if (leaveTypeRepository.existsByNameIgnoreCase(leaveType.getName())) {
            throw new IllegalArgumentException("Leave type with the same name already exists");
        }

        return leaveTypeRepository.save(leaveType);
    }

    // Update leave type
    public LeaveType updateLeaveType(Long id, LeaveType updatedLeaveType) {
        logger.info("Updating leave type with id: " + id);

        LeaveType existingLeaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LeaveType not found with id: " + id));

        if (updatedLeaveType.getName() == null || updatedLeaveType.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Leave type name cannot be empty");
        }

        // Prevent duplicate leave type names (excluding the current record)
        if (leaveTypeRepository.existsByNameIgnoreCaseAndIdNot(updatedLeaveType.getName(), id)) {
            throw new IllegalArgumentException("Another leave type with the same name already exists");
        }

        existingLeaveType.setName(updatedLeaveType.getName());
        return leaveTypeRepository.save(existingLeaveType);
    }

    // Delete leave type
    public void deleteLeaveType(Long id) {
        logger.info("Deleting leave type with id: " + id);

        LeaveType existingLeaveType = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("LeaveType not found with id: " + id));

        leaveTypeRepository.delete(existingLeaveType);
    }
}
