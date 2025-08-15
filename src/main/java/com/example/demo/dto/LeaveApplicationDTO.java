package com.example.demo.dto;

import com.example.demo.model.LeaveStatus;
import lombok.*;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplicationDTO {
    private Long id;
    private String userName;
    private String leaveTypeName;
    private LocalDate startDate;
    private LocalDate endDate;
    private LeaveStatus status;
    private String rejectionReason;
}
