package com.smartshop.dto;

import com.smartshop.enums.Role;
import com.smartshop.enums.UserStatus;

public class LoginResponse {
    private String token;
    private String tokenType = "Bearer";
    private Long id;
    private String username;
    private String email;
    private String fullName;
    private Role role;
    private UserStatus status;

    public LoginResponse() {}

    public LoginResponse(String token, Long id, String username, String email, String fullName, Role role, UserStatus status) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.status = status;
    }

    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }

    public UserStatus getStatus() { return status; }
    public void setStatus(UserStatus status) { this.status = status; }
}
