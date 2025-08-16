package com.example.demo.controller;

import com.example.demo.dto.LeaveApplicationDTO;
import com.example.demo.dto.LeaveApplicationRequestDTO;
import com.example.demo.service.LeaveApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*") // Allow all origins for CORS
@RequestMapping("/api/leave-applications")
public class LeaveApplicationController {

    @Autowired
    private LeaveApplicationService leaveApplicationService;

    public LeaveApplicationController(LeaveApplicationService leaveApplicationService) {
        this.leaveApplicationService = leaveApplicationService;
    }

    // Admin and Manager can view all leave applications
    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<LeaveApplicationDTO>> getAllLeaveApplications() {
        return ResponseEntity.ok(leaveApplicationService.getAllLeaveApplications());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER','EMPLOYEE')")
    public ResponseEntity<LeaveApplicationDTO> getLeaveApplicationById(@PathVariable Long id) {
        return leaveApplicationService.getLeaveApplicationById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Only employees can create leave applications for themselves
    @PostMapping
    @PreAuthorize("hasRole('EMPLOYEE')")
    public ResponseEntity<LeaveApplicationDTO> createLeaveApplication(@RequestBody LeaveApplicationRequestDTO requestDTO) {
        try {
            LeaveApplicationDTO created = leaveApplicationService.createLeaveApplication(requestDTO);
            return ResponseEntity.ok(created);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Admin can update any leave
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeaveApplicationDTO> updateLeaveApplication(@PathVariable Long id,
                                                                       @RequestBody LeaveApplicationRequestDTO requestDTO) {
        try {
            return leaveApplicationService.updateLeaveApplication(id, requestDTO)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // Admin can delete any leave
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLeaveApplication(@PathVariable Long id) {
        boolean deleted = leaveApplicationService.deleteLeaveApplication(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    // Employees can view their own leaves, Admin/Manager can view any user's leaves
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER') or #userId == principal.id")
    public ResponseEntity<List<LeaveApplicationDTO>> getLeaveApplicationsByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(leaveApplicationService.getLeaveApplicationsByUserId(userId));
    }

    // Only Admin and Managers can view leaves of their team
    @GetMapping("/manager/{managerId}")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<List<LeaveApplicationDTO>> getLeaveApplicationsByManager(@PathVariable Long managerId) {
        return ResponseEntity.ok(leaveApplicationService.getLeaveApplicationsByManagerId(managerId));
    }

    // Approve leave: Only Admin or Manager (JSON body optional)
    @PutMapping("/{leaveId}/approve")
@PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
public ResponseEntity<LeaveApplicationDTO> approveLeave(@PathVariable Long leaveId) {
    try {
        return leaveApplicationService.approveLeave(leaveId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    } catch (RuntimeException e) {
        return ResponseEntity.badRequest().body(null);
    }
}


    // Reject leave: Only Admin or Manager (accept JSON body with reason)
    @PutMapping("/{leaveId}/reject")
    @PreAuthorize("hasAnyRole('ADMIN','MANAGER')")
    public ResponseEntity<LeaveApplicationDTO> rejectLeave(@PathVariable Long leaveId,
                                                           @RequestBody Map<String, String> request) {
        try {
            String reason = request.get("reason");
            return leaveApplicationService.rejectLeave(leaveId, reason)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
