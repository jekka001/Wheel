package com.wheel.core.repository;

import com.wheel.core.entity.main.SpinResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface SpinRepository extends JpaRepository<SpinResult, UUID> {
}
