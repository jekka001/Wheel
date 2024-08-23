package com.wheel.core.mapper;

import com.wheel.core.entity.dto.SlotDto;
import com.wheel.core.entity.main.Slot;
import org.mapstruct.Mapper;

import java.util.List;

import static com.wheel.core.utils.Constants.COMPONENT_MODEL;

@Mapper(componentModel = COMPONENT_MODEL)
public interface SlotMapper {
    Slot toEntity(SlotDto slotDto);

    SlotDto toDto(Slot slot);

    List<SlotDto> toListDto(List<Slot> slots);
}
