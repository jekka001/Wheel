package com.wheel.core.service;


import com.wheel.core.entity.dto.SlotDto;
import com.wheel.core.entity.main.Reel;

import java.util.List;
import java.util.UUID;

public interface SlotService {
    List<SlotDto> getAll();

    List<SlotDto> getAll(UUID reelUUID);

    List<SlotDto> generateRealSlots(int count, Reel reel);
}
