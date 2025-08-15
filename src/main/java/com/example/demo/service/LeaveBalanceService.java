package com.example.demo.service;

import com.example.demo.dto.LeaveBalanceDTO;
import com.example.demo.dto.LeaveBalanceRequestDTO;
import com.example.demo.dto.LeaveTypeDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.model.LeaveApplication;
import com.example.demo.model.LeaveBalance;
import com.example.demo.model.LeaveType;
import com.example.demo.model.User;
import com.example.demo.repository.LeaveApplicationRepository;
import com.example.demo.repository.LeaveBalanceRepository;
import com.example.demo.repository.LeaveTypeRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaveBalanceService {

    @Autowired
    private LeaveBalanceRepository leaveBalanceRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeaveTypeRepository leaveTypeRepository;
    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;

    public LeaveBalanceService(LeaveBalanceRepository leaveBalanceRepository,
                               UserRepository userRepository,
                               LeaveTypeRepository leaveTypeRepository,
                               LeaveApplicationRepository leaveApplicationRepository) {
        this.leaveBalanceRepository = leaveBalanceRepository;
        this.userRepository = userRepository;
        this.leaveTypeRepository = leaveTypeRepository;
        this.leaveApplicationRepository = leaveApplicationRepository;
    }

    public List<LeaveBalanceDTO> getAllLeaveBalances() {
        return leaveBalanceRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<LeaveBalanceDTO> getLeaveBalanceById(Long id) {
        return leaveBalanceRepository.findById(id).map(this::convertToDTO);
    }

    public LeaveBalanceDTO createLeaveBalance(LeaveBalanceRequestDTO request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found"));
        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("LeaveType not found"));

        LeaveBalance leaveBalance = new LeaveBalance();
        leaveBalance.setUser(user);
        leaveBalance.setLeaveType(leaveType);
        leaveBalance.setBalanceDays(request.getBalanceDays() != null
                ? request.getBalanceDays()
                : BigDecimal.ZERO);

        return convertToDTO(leaveBalanceRepository.save(leaveBalance));
    }

    public Optional<LeaveBalanceDTO> updateLeaveBalance(Long id, LeaveBalanceRequestDTO request) {
        return leaveBalanceRepository.findById(id).map(existing -> {
            User user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new RuntimeException("User not found"));
            LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                    .orElseThrow(() -> new RuntimeException("LeaveType not found"));

            existing.setUser(user);
            existing.setLeaveType(leaveType);
            existing.setBalanceDays(request.getBalanceDays() != null
                    ? request.getBalanceDays()
                    : BigDecimal.ZERO);

            return convertToDTO(leaveBalanceRepository.save(existing));
        });
    }

    public void deleteLeaveBalance(Long id) {
        leaveBalanceRepository.deleteById(id);
    }

    public Optional<LeaveBalanceDTO> getLeaveBalanceByUserAndLeaveType(Long userId, Long leaveTypeId) {
        return leaveBalanceRepository.findByUser_IdAndLeaveType_Id(userId, leaveTypeId)
                .map(this::convertToDTO);
    }

    /**
     * Deduct leave balance when leave is approved
     */
    public void deductLeaveBalanceOnApproval(Long leaveApplicationId) {
        LeaveApplication leaveApp = leaveApplicationRepository.findById(leaveApplicationId)
                .orElseThrow(() -> new RuntimeException("Leave Application not found"));

        Long userId = leaveApp.getUser().getId();
        Long leaveTypeId = leaveApp.getLeaveType().getId();

        LeaveBalance leaveBalance = leaveBalanceRepository.findByUser_IdAndLeaveType_Id(userId, leaveTypeId)
                .orElseThrow(() -> new RuntimeException("Leave balance not found for user and leave type"));

        long days = ChronoUnit.DAYS.between(leaveApp.getStartDate(), leaveApp.getEndDate()) + 1;

        BigDecimal remaining = leaveBalance.getBalanceDays().subtract(BigDecimal.valueOf(days));

        if (remaining.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("Insufficient leave balance. Cannot approve leave.");
        }

        leaveBalance.setBalanceDays(remaining);
        leaveBalanceRepository.save(leaveBalance);
    }

    private LeaveBalanceDTO convertToDTO(LeaveBalance leaveBalance) {
        User user = leaveBalance.getUser();
        UserDTO userDTO = new UserDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                (user.getManager() != null ? user.getManager().getId() : null),
                null
        );

        LeaveType leaveType = leaveBalance.getLeaveType();
        LeaveTypeDTO leaveTypeDTO = new LeaveTypeDTO(leaveType.getId(), leaveType.getName());

        return new LeaveBalanceDTO(
                leaveBalance.getId(),
                userDTO,
                leaveTypeDTO,
                leaveBalance.getBalanceDays()
        );
    }
}
