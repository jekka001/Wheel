package com.wheel.core.impl;

import com.wheel.core.entity.dto.SlotDto;
import com.wheel.core.entity.main.Reel;
import com.wheel.core.entity.main.Slot;
import com.wheel.core.exception.SlotsNotFoundException;
import com.wheel.core.mapper.SlotMapper;
import com.wheel.core.repository.SlotRepository;
import com.wheel.core.service.SlotService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class SlotServiceImplTest {

    @Mock
    private SlotRepository repository;

    @Mock
    private SlotMapper mapper;

    @InjectMocks
    private SlotServiceImpl slotService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAll() {
        List<SlotDto> slots = slotService.getAll();

        assertNotNull(slots);
        assertEquals(10, slots.size());  // Assuming SYMBOLS has 10 items
    }

    @Test
    void testGetAllByReelUUID() {
        UUID reelUUID = UUID.randomUUID();
        List<Slot> slots = new ArrayList<>();
        slots.add(new Slot(1, 0, new Reel()));

        when(repository.findByReelId(reelUUID)).thenReturn(Optional.of(slots));
        when(mapper.toListDto(slots)).thenReturn(Collections.singletonList(new SlotDto(UUID.randomUUID(), 1, 0)));

        List<SlotDto> result = slotService.getAll(reelUUID);

        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void testGetAllByReelUUIDThrowsException() {
        UUID reelUUID = UUID.randomUUID();

        when(repository.findByReelId(reelUUID)).thenReturn(Optional.empty());

        assertThrows(SlotsNotFoundException.class, () -> slotService.getAll(reelUUID));
    }

    @Test
    void testGenerateRealSlots() {
        Reel reel = new Reel();
        List<SlotDto> slots = slotService.generateRealSlots(3, reel);

        assertNotNull(slots);
        assertEquals(3, slots.size());
    }
}
