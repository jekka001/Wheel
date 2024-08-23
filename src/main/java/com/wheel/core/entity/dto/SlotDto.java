package com.wheel.core.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class SlotDto {
    private UUID id;
    private int value;
    private int positionOnReel;
}
