package com.wheel.core.mapper;

import com.wheel.core.entity.dto.SpinResultDto;
import com.wheel.core.entity.main.SpinResult;
import org.mapstruct.Mapper;

import java.util.List;

import static com.wheel.core.utils.Constants.COMPONENT_MODEL;

@Mapper(componentModel = COMPONENT_MODEL)
public interface SpinMapper {
    SpinResult toEntity(SpinResultDto spinDto);

    SpinResultDto toDto(SpinResult spin);

    List<SpinResultDto> toListDto(List<SpinResult> spins);
}
