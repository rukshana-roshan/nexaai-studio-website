package com.smartshop.service;

import com.smartshop.dto.PasswordResetRequest;
import com.smartshop.dto.RegisterCashierRequest;
import com.smartshop.dto.UserDto;
import com.smartshop.enums.AuditAction;
import com.smartshop.enums.Role;
import com.smartshop.enums.UserStatus;
import com.smartshop.models.User;
import com.smartshop.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuditLogService auditLogService;

    @Autowired
    public UserService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuditLogService auditLogService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserDto::new).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<UserDto> getCashiers() {
        return userRepository.findByRole(Role.ROLE_CASHIER).stream().map(UserDto::new).collect(Collectors.toList());
    }

    @Transactional
    public UserDto createCashier(RegisterCashierRequest req, HttpServletRequest servletRequest) {
        if (userRepository.existsByUsername(req.getUsername().trim())) {
            throw new RuntimeException("Username '" + req.getUsername() + "' is already taken");
        }
        if (userRepository.existsByEmail(req.getEmail().trim())) {
            throw new RuntimeException("Email '" + req.getEmail() + "' is already in use");
        }

        User user = new User(
                req.getUsername().trim(),
                req.getEmail().trim(),
                passwordEncoder.encode(req.getPassword()),
                req.getFullName().trim(),
                req.getPhone() != null ? req.getPhone().trim() : null,
                req.getRole() != null ? req.getRole() : Role.ROLE_CASHIER,
                UserStatus.ACTIVE
        );

        User savedUser = userRepository.save(user);

        auditLogService.log(
                AuditAction.CREATE_USER,
                "Created new cashier account: " + savedUser.getUsername() + " (" + savedUser.getFullName() + ")",
                servletRequest
        );

        return new UserDto(savedUser);
    }

    @Transactional
    public UserDto toggleUserStatus(Long userId, HttpServletRequest servletRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        if (user.getRole() == Role.ROLE_OWNER) {
            throw new RuntimeException("Cannot disable the Owner account");
        }

        user.setStatus(user.getStatus() == UserStatus.ACTIVE ? UserStatus.INACTIVE : UserStatus.ACTIVE);
        User updated = userRepository.save(user);

        auditLogService.log(
                AuditAction.UPDATE_USER_STATUS,
                "Updated status of user " + user.getUsername() + " to " + user.getStatus().name(),
                servletRequest
        );

        return new UserDto(updated);
    }

    @Transactional
    public void resetPassword(Long userId, PasswordResetRequest req, HttpServletRequest servletRequest) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        user.setPassword(passwordEncoder.encode(req.getNewPassword()));
        userRepository.save(user);

        auditLogService.log(
                AuditAction.RESET_PASSWORD,
                "Reset password for user " + user.getUsername(),
                servletRequest
        );
    }
}
