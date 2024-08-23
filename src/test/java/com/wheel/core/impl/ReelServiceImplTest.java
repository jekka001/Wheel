package com.wheel.core.impl;

import com.wheel.core.entity.dto.ReelDto;
import com.wheel.core.entity.dto.SlotDto;
import com.wheel.core.entity.main.Reel;
import com.wheel.core.entity.main.SpinResult;
import com.wheel.core.exception.ReelNotFoundException;
import com.wheel.core.mapper.ReelMapper;
import com.wheel.core.repository.ReelRepository;
import com.wheel.core.service.SlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class ReelServiceImplTest {

    @Mock
    private ReelRepository reelRepository;

    @Mock
    private ReelMapper reelMapper;

    @Mock
    private SlotService slotService;

    @InjectMocks
    private ReelServiceImpl reelService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetFakeReels() {
        int countOfReels = 3;
        List<SlotDto> slotDtos = new ArrayList<>();
        when(slotService.getAll()).thenReturn(slotDtos);

        List<ReelDto> fakeReels = reelService.getFakeReels(countOfReels);

        assertNotNull(fakeReels);
        assertEquals(countOfReels, fakeReels.size());
        for (int i = 0; i < fakeReels.size(); i++) {
            assertEquals(i, fakeReels.get(i).getPositionOnWheel());
        }
    }

    @Test
    void testGenerateRealReels() {
        int countOfReels = 3;
        int countOfSlots = 5;
        SpinResult spinResult = new SpinResult();
        Reel reel = new Reel();
        ReelDto reelDto = new ReelDto();
        List<SlotDto> slotDtos = new ArrayList<>();

        when(reelRepository.save(any(Reel.class))).thenReturn(reel);
        when(reelMapper.toDto(reel)).thenReturn(reelDto);
        when(slotService.generateRealSlots(countOfSlots, reel)).thenReturn(slotDtos);

        List<ReelDto> realReels = reelService.generateRealReels(countOfReels, countOfSlots, spinResult);

        assertNotNull(realReels);
        assertEquals(countOfReels, realReels.size());
    }

    @Test
    void testGetReelsBySpin() {
        UUID spinId = UUID.randomUUID();
        List<Reel> reels = new ArrayList<>();
        Reel reel = new Reel();
        reels.add(reel);

        when(reelRepository.getReelBySpinResultId(spinId)).thenReturn(Optional.of(reels));
        when(reelMapper.toListDto(reels)).thenReturn(Collections.singletonList(new ReelDto()));

        List<ReelDto> reelDtos = reelService.getReelsBySpin(spinId);

        assertNotNull(reelDtos);
        assertEquals(1, reelDtos.size());
    }

    @Test
    void testGetReelsBySpinThrowsException() {
        UUID spinId = UUID.randomUUID();

        when(reelRepository.getReelBySpinResultId(spinId)).thenReturn(Optional.empty());

        assertThrows(ReelNotFoundException.class, () -> reelService.getReelsBySpin(spinId));
    }
}
