package com.wheel.core.impl;

import com.wheel.core.entity.dto.ReelDto;
import com.wheel.core.entity.dto.SpinResultDto;
import com.wheel.core.entity.dto.UserDto;
import com.wheel.core.entity.main.SpinResult;
import com.wheel.core.exception.UserSpinResultException;
import com.wheel.core.mapper.ReelMapper;
import com.wheel.core.mapper.SpinMapper;
import com.wheel.core.mapper.UserMapper;
import com.wheel.core.repository.SpinRepository;
import com.wheel.core.service.ReelService;
import com.wheel.core.service.SlotService;
import com.wheel.core.service.SpinService;
import com.wheel.core.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpinServiceImpl implements SpinService {
    private final SpinRepository repository;
    private final SpinMapper mapper;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ReelService reelService;
    private final ReelMapper reelMapper;
    private final SlotService slotService;

    @Override
    public SpinResultDto get(UUID spinId) {
        SpinResult spinResult = repository.getReferenceById(spinId);

        return mapper.toDto(spinResult);
    }

    @Override
    @Transactional
    public SpinResultDto calculateSpin(HttpServletRequest request, int countOfReels, int countOfSlots) {
        UserDto userDto = userService.get(request);
        SpinResult spinResult = saveNewSpinResult(userDto.getId());

        List<ReelDto> realReels = reelService.generateRealReels(countOfReels, countOfSlots, spinResult);

        return getSpinResult(spinResult, realReels);
    }

    private SpinResult saveNewSpinResult(UUID userId) {
        UserDto user = userService.get(userId);

        SpinResult newSpinResult = new SpinResult();
        newSpinResult.setUser(userMapper.toEntity(user));
        newSpinResult.setSpinDate(Timestamp.from(Instant.now()));

        return repository.save(newSpinResult);
    }

    private SpinResultDto getSpinResult(SpinResult spinResult, List<ReelDto> reels) {
        SpinResultDto spinResultDto = new SpinResultDto();

        spinResultDto.setId(spinResult.getId());
        spinResultDto.setFakeReels(reelService.getFakeReels(reels.size()));
        spinResultDto.setReels(reels);
        spinResultDto.setUser(userMapper.toDto(spinResult.getUser()));

        return spinResultDto;
    }

    @Override
    @Transactional
    public SpinResultDto checkSpinResult(HttpServletRequest request, String spinResultId) {
        UserDto userDto = userService.get(request);
        UUID spinResultUUID = UUID.fromString(spinResultId);
        SpinResult spinResult = repository.getReferenceById(spinResultUUID);

        if (spinResult.getUser().getId().equals(userDto.getId())) {
            return prepareSpinResultDto(spinResult);
        } else {
            throw new UserSpinResultException(userDto.getId().toString(), spinResultUUID.toString());
        }
    }

    private SpinResultDto prepareSpinResultDto(SpinResult spinResult) {
        List<ReelDto> reelsDto = reelMapper.toListDto(spinResult.getReels());

        for (ReelDto reel : reelsDto) {
            reel.setSlotDtos(slotService.getAll(reel.getId()));
        }
        UserDto userDto = userMapper.toDto(spinResult.getUser());
        return new SpinResultDto(spinResult.getId(), null, reelsDto, userDto, spinResult.isPayed());
    }

    @Override
    @Transactional
    public void savePayed(SpinResultDto spinResultDto) {
        SpinResult spinResult = repository.getReferenceById(spinResultDto.getId());
        spinResult.setPayed(true);
        repository.save(spinResult);
    }
}
