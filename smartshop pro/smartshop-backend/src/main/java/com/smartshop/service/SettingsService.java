package com.smartshop.service;

import com.smartshop.dto.ShopSettingsDto;
import com.smartshop.enums.AuditAction;
import com.smartshop.models.ShopSettings;
import com.smartshop.repository.ShopSettingsRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class SettingsService {

    private final ShopSettingsRepository settingsRepository;
    private final AuditLogService auditLogService;

    @Autowired
    public SettingsService(ShopSettingsRepository settingsRepository, AuditLogService auditLogService) {
        this.settingsRepository = settingsRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public ShopSettingsDto getSettings() {
        ShopSettings settings = settingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(() -> settingsRepository.save(new ShopSettings()));
        return new ShopSettingsDto(settings);
    }

    @Transactional
    public ShopSettingsDto updateSettings(ShopSettingsDto dto, HttpServletRequest request) {
        ShopSettings settings = settingsRepository.findFirstByOrderByIdAsc()
                .orElseGet(ShopSettings::new);

        settings.setShopName(dto.getShopName().trim());
        settings.setTagline(dto.getTagline());
        settings.setAddress(dto.getAddress());
        settings.setPhone(dto.getPhone());
        settings.setEmail(dto.getEmail());
        settings.setWebsite(dto.getWebsite());
        settings.setTaxNumber(dto.getTaxNumber());
        settings.setCurrencySymbol(dto.getCurrencySymbol() != null ? dto.getCurrencySymbol() : "$");
        settings.setCurrencyCode(dto.getCurrencyCode() != null ? dto.getCurrencyCode() : "USD");
        settings.setDefaultTaxRate(dto.getDefaultTaxRate());
        settings.setEnableTax(dto.isEnableTax());
        settings.setDefaultLowStockAlert(dto.getDefaultLowStockAlert());
        settings.setReceiptHeader(dto.getReceiptHeader());
        settings.setReceiptFooter(dto.getReceiptFooter());
        settings.setUpdatedAt(LocalDateTime.now());

        ShopSettings saved = settingsRepository.save(settings);

        auditLogService.log(
                AuditAction.UPDATE_SETTINGS,
                "Updated shop settings and receipt configuration",
                request
        );

        return new ShopSettingsDto(saved);
    }
}
