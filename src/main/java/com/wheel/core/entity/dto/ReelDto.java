package com.wheel.core.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReelDto {
    private UUID id;
    private List<SlotDto> slotDtos;
    private int positionOnWheel;
}
