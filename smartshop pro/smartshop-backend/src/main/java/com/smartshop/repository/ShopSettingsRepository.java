package com.smartshop.repository;

import com.smartshop.models.ShopSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShopSettingsRepository extends JpaRepository<ShopSettings, Long> {
    Optional<ShopSettings> findFirstByOrderByIdAsc();
}
