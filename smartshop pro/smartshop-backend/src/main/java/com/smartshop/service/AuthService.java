package com.smartshop.service;

import com.smartshop.dto.LoginRequest;
import com.smartshop.dto.LoginResponse;
import com.smartshop.enums.AuditAction;
import com.smartshop.enums.UserStatus;
import com.smartshop.models.User;
import com.smartshop.repository.UserRepository;
import com.smartshop.security.JwtTokenProvider;
import com.smartshop.security.UserPrincipal;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider tokenProvider;
    private final AuditLogService auditLogService;

    @Autowired
    public AuthService(AuthenticationManager authenticationManager,
                       UserRepository userRepository,
                       JwtTokenProvider tokenProvider,
                       AuditLogService auditLogService) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
        this.auditLogService = auditLogService;
    }

    @Transactional
    public LoginResponse login(LoginRequest req, HttpServletRequest servletRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(req.getUsername().trim(), req.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = tokenProvider.generateToken(authentication);

            UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();

            // Update last login timestamp
            userRepository.findById(principal.getId()).ifPresent(u -> {
                u.setLastLoginAt(LocalDateTime.now());
                userRepository.save(u);
            });

            auditLogService.logWithUser(
                    AuditAction.LOGIN_SUCCESS,
                    "User logged in successfully with role: " + principal.getRole().name(),
                    principal.getUsername(),
                    principal.getFullName(),
                    servletRequest
            );

            return new LoginResponse(
                    jwt,
                    principal.getId(),
                    principal.getUsername(),
                    principal.getEmail(),
                    principal.getFullName(),
                    principal.getRole(),
                    principal.getStatus()
            );

        } catch (DisabledException ex) {
            auditLogService.log(AuditAction.LOGIN_FAILED, "Login failed: Account is disabled for " + req.getUsername(), servletRequest);
            throw new RuntimeException("Account is disabled. Please contact shop owner.");
        } catch (BadCredentialsException ex) {
            auditLogService.log(AuditAction.LOGIN_FAILED, "Login failed: Invalid credentials for " + req.getUsername(), servletRequest);
            throw new RuntimeException("Invalid username or password");
        } catch (Exception ex) {
            auditLogService.log(AuditAction.LOGIN_FAILED, "Login error for " + req.getUsername() + ": " + ex.getMessage(), servletRequest);
            throw new RuntimeException(ex.getMessage());
        }
    }
}
