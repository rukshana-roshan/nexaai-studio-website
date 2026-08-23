package com.smartshop.controller;

import com.smartshop.dto.ApiResponse;
import com.smartshop.dto.ShopSettingsDto;
import com.smartshop.service.SettingsService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    @Autowired
    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<ShopSettingsDto>> getSettings() {
        return ResponseEntity.ok(ApiResponse.ok("Settings retrieved", settingsService.getSettings()));
    }

    @PutMapping
    @PreAuthorize("hasRole('OWNER') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ShopSettingsDto>> updateSettings(
            @Valid @RequestBody ShopSettingsDto dto,
            HttpServletRequest request) {
        ShopSettingsDto updated = settingsService.updateSettings(dto, request);
        return ResponseEntity.ok(ApiResponse.ok("Settings updated successfully", updated));
    }
}
