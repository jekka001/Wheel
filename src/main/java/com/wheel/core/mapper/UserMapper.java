package com.wheel.core.mapper;

import com.wheel.core.entity.dto.UserDto;
import com.wheel.core.entity.main.User;
import org.mapstruct.Mapper;

import static com.wheel.core.utils.Constants.COMPONENT_MODEL;

@Mapper(componentModel = COMPONENT_MODEL)
public interface UserMapper {
    User toEntity(UserDto userDto);

    UserDto toDto(User user);

}
