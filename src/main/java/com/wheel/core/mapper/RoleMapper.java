package com.wheel.core.mapper;

import com.wheel.core.entity.dto.RoleDto;
import com.wheel.core.entity.main.Role;
import org.mapstruct.Mapper;

import java.util.List;

import static com.wheel.core.utils.Constants.COMPONENT_MODEL;

@Mapper(componentModel = COMPONENT_MODEL)
public interface RoleMapper {
    Role toEntity(RoleDto roleDto);

    RoleDto toDto(Role role);

    List<Role> toListEntity(List<RoleDto> rolesDto);
}
