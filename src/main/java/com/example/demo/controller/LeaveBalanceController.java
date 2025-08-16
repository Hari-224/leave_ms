package com.example.demo.controller;

import com.example.demo.dto.LeaveBalanceDTO;
import com.example.demo.dto.LeaveBalanceRequestDTO;
import com.example.demo.service.LeaveBalanceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin(origins = "*") // Allow all origins for CORS
@RequestMapping("/api/leave-balances")
public class LeaveBalanceController {

    @Autowired
    private LeaveBalanceService leaveBalanceService;

    public LeaveBalanceController(LeaveBalanceService leaveBalanceService) {
        this.leaveBalanceService = leaveBalanceService;
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public List<LeaveBalanceDTO> getAllLeaveBalances() {
        return leaveBalanceService.getAllLeaveBalances();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<LeaveBalanceDTO> getLeaveBalanceById(@PathVariable Long id) {
        return leaveBalanceService.getLeaveBalanceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public LeaveBalanceDTO createLeaveBalance(@RequestBody LeaveBalanceRequestDTO request) {
        return leaveBalanceService.createLeaveBalance(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<LeaveBalanceDTO> updateLeaveBalance(@PathVariable Long id,
                                                              @RequestBody LeaveBalanceRequestDTO request) {
        return leaveBalanceService.updateLeaveBalance(id, request)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteLeaveBalance(@PathVariable Long id) {
        leaveBalanceService.deleteLeaveBalance(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/user/{userId}/type/{leaveTypeId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'EMPLOYEE')")
    public ResponseEntity<LeaveBalanceDTO> getLeaveBalanceByUserAndLeaveType(@PathVariable Long userId,
                                                                             @PathVariable Long leaveTypeId) {
        return leaveBalanceService.getLeaveBalanceByUserAndLeaveType(userId, leaveTypeId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/deduct/{leaveApplicationId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<String> deductLeaveBalance(@PathVariable Long leaveApplicationId) {
        leaveBalanceService.deductLeaveBalanceOnApproval(leaveApplicationId);
        return ResponseEntity.ok("Leave balance deducted successfully.");
    }
}
