package com.wheel.core.repository;

import com.wheel.core.entity.main.Reel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ReelRepository extends JpaRepository<Reel, UUID> {

    Optional<List<Reel>> getReelBySpinResultId(UUID spinUUID);
}
