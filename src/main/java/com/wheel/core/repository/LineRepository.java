package com.wheel.core.repository;

import com.wheel.core.entity.main.Line;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LineRepository extends JpaRepository<Line, UUID> {
}
