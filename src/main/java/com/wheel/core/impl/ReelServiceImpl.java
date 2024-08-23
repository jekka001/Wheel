package com.wheel.core.impl;

import com.wheel.core.entity.dto.ReelDto;
import com.wheel.core.entity.dto.SlotDto;
import com.wheel.core.entity.main.Reel;
import com.wheel.core.entity.main.SpinResult;
import com.wheel.core.exception.ReelNotFoundException;
import com.wheel.core.mapper.ReelMapper;
import com.wheel.core.repository.ReelRepository;
import com.wheel.core.service.ReelService;
import com.wheel.core.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ReelServiceImpl implements ReelService {
    private final ReelRepository repository;
    private final ReelMapper mapper;
    private final SlotService slotService;

    @Override
    @Transactional
    public List<ReelDto> getFakeReels(int countOfReels) {
        List<ReelDto> fakeReels = new ArrayList<>();

        for (int count = 0; count < countOfReels; count++) {
            List<SlotDto> allSlots = slotService.getAll();
            ReelDto reelDto = new ReelDto(UUID.randomUUID(), allSlots, count);
            fakeReels.add(reelDto);
        }

        return fakeReels;
    }

    @Override
    @Transactional
    public List<ReelDto> generateRealReels(int countOfReels, int countOfSlots, SpinResult spinResult) {
        List<ReelDto> realReels = new ArrayList<>();

        for (int count = 0; count < countOfReels; count++) {
            realReels.add(createReelWithSlots(countOfSlots, spinResult, count));
        }

        return realReels;
    }

    private ReelDto createReelWithSlots(int countOfSlots, SpinResult spinResult, int count) {
        Reel reel = createReel(count, spinResult);
        ReelDto reelDto = mapper.toDto(reel);

        List<SlotDto> realSlots = slotService.generateRealSlots(countOfSlots, reel);
        reelDto.setSlotDtos(realSlots);

        return reelDto;
    }

    private Reel createReel(int reelPosition, SpinResult spinResult) {
        Reel reel = new Reel();
        reel.setPositionOnWheel(reelPosition);
        reel.setSpinResult(spinResult);

        return repository.save(reel);
    }

    @Override
    @Transactional
    public List<ReelDto> getReelsBySpin(UUID spinId) {
        List<Reel> reels = repository.getReelBySpinResultId(spinId).orElseThrow(() -> new ReelNotFoundException(spinId.toString()));
        return mapper.toListDto(reels);
    }
}
