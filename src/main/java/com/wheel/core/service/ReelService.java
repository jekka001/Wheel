package com.wheel.core.service;


import com.wheel.core.entity.dto.ReelDto;
import com.wheel.core.entity.main.SpinResult;

import java.util.List;
import java.util.UUID;

public interface ReelService {
    List<ReelDto> getFakeReels(int countOfReels);

    List<ReelDto> generateRealReels(int countOfReels, int countOfSlots, SpinResult spinResult);

    List<ReelDto> getReelsBySpin(UUID spinId);
}
