package com.wheel.core.impl;

import com.wheel.core.entity.dto.RoleDto;
import com.wheel.core.entity.main.Role;
import com.wheel.core.exception.RoleNotFoundException;
import com.wheel.core.mapper.RoleMapper;
import com.wheel.core.repository.RoleRepository;
import com.wheel.core.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository repository;
    private final RoleMapper mapper;

    @Override
    @Transactional
    public RoleDto get(String name) {
        Role role = repository.findByName(name).orElseThrow(RoleNotFoundException::new);
        return mapper.toDto(role);
    }
}
