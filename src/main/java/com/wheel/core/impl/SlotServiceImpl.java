package com.wheel.core.impl;

import com.wheel.core.entity.dto.SlotDto;
import com.wheel.core.entity.main.Reel;
import com.wheel.core.entity.main.Slot;
import com.wheel.core.exception.SlotsNotFoundException;
import com.wheel.core.mapper.SlotMapper;
import com.wheel.core.repository.SlotRepository;
import com.wheel.core.service.SlotService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.util.*;

import static com.wheel.core.utils.Constants.SYMBOLS_FROM;
import static com.wheel.core.utils.Constants.SYMBOLS_TO;

@Service
@RequiredArgsConstructor
public class SlotServiceImpl implements SlotService {
    private static final List<Integer> SYMBOLS = Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9);
    private static final SecureRandom SECURE_RANDOM = new SecureRandom();
    private final SlotRepository repository;
    private final SlotMapper mapper;

    @Override
    @Transactional
    public List<SlotDto> getAll() {
        List<SlotDto> slots = new ArrayList<>();
        Collections.shuffle(SYMBOLS, SECURE_RANDOM);

        for (int counter = 0; counter < SYMBOLS.size(); counter++) {
            int symbol = SYMBOLS.get(counter);
            SlotDto slotDto = new SlotDto(UUID.randomUUID(), symbol, counter);
            slots.add(slotDto);
        }

        return slots;
    }

    @Override
    @Transactional
    public List<SlotDto> getAll(UUID reelUUID) {
        List<Slot> slots = repository.findByReelId(reelUUID).orElseThrow(() -> new SlotsNotFoundException(reelUUID.toString()));

        return mapper.toListDto(slots);
    }

    @Override
    @Transactional
    public List<SlotDto> generateRealSlots(int count, Reel reel) {
        List<SlotDto> slots = new ArrayList<>();

        for (int position = 0; position < count; position++) {
            slots.add(createSlot(reel, position));
        }

        return slots;
    }

    private SlotDto createSlot(Reel reel, int position) {
        int symbol = SECURE_RANDOM.nextInt(SYMBOLS_FROM, SYMBOLS_TO);
        Slot slot = new Slot(symbol, position, reel);
        Slot createdSlot = repository.save(slot);

        return mapper.toDto(createdSlot);
    }
}
