package com.wheel.core.entity.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class UserDto {
    private UUID id;
    private String login;
    private boolean deactivate;
}
