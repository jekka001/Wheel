package com.wheel.core.service;

import com.wheel.core.entity.dto.UserDto;
import com.wheel.core.entity.main.User;
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;

public interface UserService {
    boolean create(String login, String password);

    String createUserToken(User user);

    UserDto get(HttpServletRequest request);

    UserDto get(UUID uuid);

    String login(String login);
}
