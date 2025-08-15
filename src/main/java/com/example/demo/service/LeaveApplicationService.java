package com.example.demo.service;

import com.example.demo.dto.LeaveApplicationDTO;
import com.example.demo.dto.LeaveApplicationRequestDTO;
import com.example.demo.model.LeaveApplication;
import com.example.demo.model.LeaveStatus;
import com.example.demo.model.LeaveType;
import com.example.demo.model.User;
import com.example.demo.repository.LeaveApplicationRepository;
import com.example.demo.repository.LeaveTypeRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class LeaveApplicationService {

    @Autowired
    private LeaveApplicationRepository leaveApplicationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private LeaveTypeRepository leaveTypeRepository;

    public LeaveApplicationService(LeaveApplicationRepository leaveApplicationRepository,
                                   UserRepository userRepository,
                                   LeaveTypeRepository leaveTypeRepository) {
        this.leaveApplicationRepository = leaveApplicationRepository;
        this.userRepository = userRepository;
        this.leaveTypeRepository = leaveTypeRepository;
    }

    private LeaveApplicationDTO convertToDTO(LeaveApplication entity) {
        return new LeaveApplicationDTO(
                entity.getId(),
                entity.getUser().getName(),
                entity.getLeaveType().getName(),
                entity.getStartDate(),
                entity.getEndDate(),
                entity.getStatus(),
                entity.getRejectionReason()
        );
    }

    private LeaveApplication convertToEntity(LeaveApplicationRequestDTO dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found with id: " + dto.getUserId()));
        LeaveType leaveType = leaveTypeRepository.findById(dto.getLeaveTypeId())
                .orElseThrow(() -> new RuntimeException("Leave type not found with id: " + dto.getLeaveTypeId()));

        return new LeaveApplication(
                null,
                user,
                leaveType,
                dto.getStartDate(),
                dto.getEndDate(),
                dto.getStatus() != null ? dto.getStatus() : LeaveStatus.PENDING,
                dto.getRejectionReason()
        );
    }

    public List<LeaveApplicationDTO> getAllLeaveApplications() {
        return leaveApplicationRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<LeaveApplicationDTO> getLeaveApplicationById(Long id) {
        return leaveApplicationRepository.findById(id).map(this::convertToDTO);
    }

    public LeaveApplicationDTO createLeaveApplication(LeaveApplicationRequestDTO requestDTO) {
        LeaveApplication entity = convertToEntity(requestDTO);
        return convertToDTO(leaveApplicationRepository.save(entity));
    }

    public Optional<LeaveApplicationDTO> updateLeaveApplication(Long id, LeaveApplicationRequestDTO requestDTO) {
        return leaveApplicationRepository.findById(id).map(existing -> {
            LeaveApplication updated = convertToEntity(requestDTO);
            updated.setId(existing.getId());
            return convertToDTO(leaveApplicationRepository.save(updated));
        });
    }

    public boolean deleteLeaveApplication(Long id) {
        if (leaveApplicationRepository.existsById(id)) {
            leaveApplicationRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public List<LeaveApplicationDTO> getLeaveApplicationsByUserId(Long userId) {
        return leaveApplicationRepository.findByUser_Id(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<LeaveApplicationDTO> getLeaveApplicationsByManagerId(Long managerId) {
        return leaveApplicationRepository.findByUser_Manager_Id(managerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public Optional<LeaveApplicationDTO> approveLeave(Long leaveId) {
        return leaveApplicationRepository.findById(leaveId).map(leave -> {
            if (leave.getStatus() == LeaveStatus.APPROVED) {
                throw new RuntimeException("Leave is already approved");
            }
            leave.setStatus(LeaveStatus.APPROVED);
            leave.setRejectionReason(null);
            return convertToDTO(leaveApplicationRepository.save(leave));
        });
    }

    public Optional<LeaveApplicationDTO> rejectLeave(Long leaveId, String reason) {
        return leaveApplicationRepository.findById(leaveId).map(leave -> {
            if (leave.getStatus() == LeaveStatus.REJECTED) {
                throw new RuntimeException("Leave is already rejected");
            }
            leave.setStatus(LeaveStatus.REJECTED);
            leave.setRejectionReason(reason);
            return convertToDTO(leaveApplicationRepository.save(leave));
        });
    }
}
