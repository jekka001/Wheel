package com.wheel.core.service;

import com.wheel.core.entity.dto.SpinResultDto;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface SpinService {
    SpinResultDto get(UUID spinId);

    SpinResultDto calculateSpin(HttpServletRequest request, int countOfReels, int countOfSlots);

    SpinResultDto checkSpinResult(HttpServletRequest request, String spinResultId);

    void savePayed(SpinResultDto spinResult);
}
