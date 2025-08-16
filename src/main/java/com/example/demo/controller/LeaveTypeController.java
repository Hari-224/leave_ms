package com.example.demo.controller;

import com.example.demo.model.LeaveType;
import com.example.demo.service.LeaveTypeService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@RestController
@CrossOrigin(origins = "*") // Allow all origins for CORS
@RequestMapping("/api/leave-types")
public class LeaveTypeController {

    private static final Logger logger = Logger.getLogger(LeaveTypeController.class.getName());

    @Autowired
    private LeaveTypeService leaveTypeService;

    // Get all leave types - accessible to all authenticated users
    @GetMapping
    public ResponseEntity<List<LeaveType>> getAllLeaveTypes() {
        logger.info("GET /api/leave-types");
        return ResponseEntity.ok(leaveTypeService.getAllLeaveTypes());
    }

    // Get leave type by ID - accessible to all authenticated users
    @GetMapping("/{id}")
    public ResponseEntity<LeaveType> getLeaveTypeById(@PathVariable Long id) {
        logger.info("GET /api/leave-types/" + id);
        return leaveTypeService.getLeaveTypeById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Create a new leave type - only ADMIN
    @PostMapping
    public ResponseEntity<?> createLeaveType(@RequestBody LeaveType leaveType, HttpServletRequest request) {
        logger.info("POST /api/leave-types");
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access denied: only ADMIN can create leave types");
        }
        try {
            LeaveType createdLeaveType = leaveTypeService.createLeaveType(leaveType);
            return ResponseEntity.ok(createdLeaveType);
        } catch (IllegalArgumentException e) {
            logger.warning("Error creating leave type: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Update an existing leave type - only ADMIN
    @PutMapping("/{id}")
    public ResponseEntity<?> updateLeaveType(@PathVariable Long id, @RequestBody LeaveType leaveType, HttpServletRequest request) {
        logger.info("PUT /api/leave-types/" + id);
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access denied: only ADMIN can update leave types");
        }
        try {
            LeaveType updatedLeaveType = leaveTypeService.updateLeaveType(id, leaveType);
            return ResponseEntity.ok(updatedLeaveType);
        } catch (IllegalArgumentException e) {
            logger.warning("Error updating leave type: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // Delete a leave type - only ADMIN
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteLeaveType(@PathVariable Long id, HttpServletRequest request) {
        logger.info("DELETE /api/leave-types/" + id);
        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            return ResponseEntity.status(403).body("Access denied: only ADMIN can delete leave types");
        }
        try {
            leaveTypeService.deleteLeaveType(id);
            return ResponseEntity.ok("LeaveType deleted successfully.");
        } catch (IllegalArgumentException e) {
            logger.warning("Error deleting leave type: " + e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
