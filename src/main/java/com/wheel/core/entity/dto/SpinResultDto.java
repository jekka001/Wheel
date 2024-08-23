package com.wheel.core.entity.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpinResultDto {
    private UUID id;
    private List<ReelDto> fakeReels;
    private List<ReelDto> reels;
    private UserDto user;
    private boolean payed;
}
