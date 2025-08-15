package com.example.demo.dto;

import com.example.demo.model.LeaveStatus;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplicationRequestDTO {
    private Long userId;
    private Long leaveTypeId;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveStatus status;
    private String rejectionReason;
}
