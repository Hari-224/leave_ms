package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LeaveBalanceDTO {
    private Long id;
    private UserDTO user;
    private LeaveTypeDTO leaveType;
    private BigDecimal balanceDays;
}
