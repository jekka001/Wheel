package com.wheel.core.mapper;

import com.wheel.core.entity.dto.ReelDto;
import com.wheel.core.entity.main.Reel;
import org.mapstruct.Mapper;

import java.util.List;
import java.util.stream.Collectors;

import static com.wheel.core.utils.Constants.COMPONENT_MODEL;

@Mapper(componentModel = COMPONENT_MODEL)
public interface ReelMapper {
    default Reel toEntity(ReelDto reelDto) {
        Reel reel = new Reel();
        reel.setId(reelDto.getId());
        reel.setPositionOnWheel(reelDto.getPositionOnWheel());

        return reel;
    }

    default ReelDto toDto(Reel reel) {
        ReelDto reelDto = new ReelDto();
        reelDto.setId(reel.getId());
        reelDto.setPositionOnWheel(reel.getPositionOnWheel());

        return reelDto;
    }

    default List<ReelDto> toListDto(List<Reel> reels) {
        return reels.stream().map(this::toDto).collect(Collectors.toList());
    }
}
