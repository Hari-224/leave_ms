package com.example.demo.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class LeaveBalanceRequestDTO {
    private Long userId;
    private Long leaveTypeId;
    private BigDecimal balanceDays;
}
