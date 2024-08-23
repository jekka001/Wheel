package com.wheel.core.impl;

import com.wheel.core.config.JwtUtils;
import com.wheel.core.entity.dto.RoleDto;
import com.wheel.core.entity.dto.UserDto;
import com.wheel.core.entity.main.User;
import com.wheel.core.exception.UserNotFoundException;
import com.wheel.core.mapper.RoleMapper;
import com.wheel.core.mapper.UserMapper;
import com.wheel.core.repository.UserRepository;
import com.wheel.core.service.RoleService;
import com.wheel.core.service.UserService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static com.wheel.core.utils.Constants.USER_ROLE;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository repository;
    private final UserMapper mapper;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RoleService roleService;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public boolean create(String login, String password) {
        List<RoleDto> roles = Collections.singletonList(roleService.get(USER_ROLE));
        User user = new User(login);

        user.setPassword(encoder.encode(password));
        user.setRoles(roleMapper.toListEntity(roles));
        repository.saveAndFlush(user);

        return true;
    }

    @Override
    public String createUserToken(User user) {
        return jwtUtils.createToken(user);
    }

    @Override
    @Transactional
    public UserDto get(HttpServletRequest request) {
        String accessToken = jwtUtils.resolveToken(request);
        Claims claims = jwtUtils.resolveClaims(accessToken);

        String login = claims.getSubject();

        User user = repository.findByLogin(login)
                .orElseThrow(() -> new UserNotFoundException(login));

        return mapper.toDto(user);
    }

    @Override
    public UserDto get(UUID uuid) {
        User user = repository.findById(uuid).orElseThrow(() -> new UserNotFoundException(uuid.toString()));

        return mapper.toDto(user);
    }

    @Override
    public String login(String login) {
        User user = repository.findByLogin(login).orElseThrow(() -> new UserNotFoundException(login));

        return createUserToken(user);
    }

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        return repository.findByLogin(login).orElseThrow(() -> new UsernameNotFoundException(login));
    }
}
