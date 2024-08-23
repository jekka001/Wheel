package com.wheel.core.repository;

import com.wheel.core.entity.main.Slot;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SlotRepository extends JpaRepository<Slot, UUID> {

    Optional<List<Slot>> findByReelId(UUID reelId);

}
