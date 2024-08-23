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
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceImplTest {

    @Mock
    private UserRepository repository;

    @Mock
    private UserMapper mapper;

    @Mock
    private PasswordEncoder encoder;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private RoleService roleService;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        user = new User("testUser");
        user.setPassword("encodedPassword");

        userDto = new UserDto();
        userDto.setLogin("testUser");
    }

    @Test
    void testCreate() {
        when(roleService.get("USER_ROLE")).thenReturn(new RoleDto());
        when(encoder.encode("testPassword")).thenReturn("encodedPassword");
        when(roleMapper.toListEntity(any())).thenReturn(Collections.emptyList());
        when(repository.saveAndFlush(any(User.class))).thenReturn(user);

        boolean result = userService.create("testUser", "testPassword");

        assertEquals(true, result);
    }

    @Test
    void testCreateUserToken() {
        when(jwtUtils.createToken(user)).thenReturn("mockedToken");

        String token = userService.createUserToken(user);

        assertEquals("mockedToken", token);
    }

    @Test
    void testGetByRequest() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Claims claims = Mockito.mock(Claims.class);

        when(jwtUtils.resolveToken(request)).thenReturn("accessToken");
        when(jwtUtils.resolveClaims("accessToken")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("testUser");
        when(repository.findByLogin("testUser")).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.get(request);

        assertEquals(userDto, result);
    }

    @Test
    void testGetByUUID() {
        UUID uuid = UUID.randomUUID();

        when(repository.findById(uuid)).thenReturn(Optional.of(user));
        when(mapper.toDto(user)).thenReturn(userDto);

        UserDto result = userService.get(uuid);

        assertEquals(userDto, result);
    }

    @Test
    void testLogin() {
        when(repository.findByLogin("testUser")).thenReturn(Optional.of(user));
        when(jwtUtils.createToken(user)).thenReturn("mockedToken");

        String token = userService.login("testUser");

        assertEquals("mockedToken", token);
    }

    @Test
    void testLoadUserByUsername() {
        when(repository.findByLogin("testUser")).thenReturn(Optional.of(user));

        UserDetails userDetails = userService.loadUserByUsername("testUser");

        assertEquals(user, userDetails);
    }

    @Test
    void testCreateUserThrowsException() {
        when(roleService.get("USER_ROLE")).thenReturn(new RoleDto());
        when(encoder.encode("testPassword")).thenReturn("encodedPassword");
        when(roleMapper.toListEntity(any())).thenReturn(Collections.emptyList());
        when(repository.saveAndFlush(any(User.class))).thenThrow(new RuntimeException("Database Error"));

        assertThrows(RuntimeException.class, () -> userService.create("testUser", "testPassword"));
    }

    @Test
    void testGetByRequestThrowsException() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        Claims claims = Mockito.mock(Claims.class);

        when(jwtUtils.resolveToken(request)).thenReturn("accessToken");
        when(jwtUtils.resolveClaims("accessToken")).thenReturn(claims);
        when(claims.getSubject()).thenReturn("testUser");
        when(repository.findByLogin("testUser")).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.get(request));
    }

    @Test
    void testGetByUUIDThrowsException() {
        UUID uuid = UUID.randomUUID();

        when(repository.findById(uuid)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.get(uuid));
    }

    @Test
    void testLoadUserByUsernameThrowsException() {
        when(repository.findByLogin("testUser")).thenReturn(Optional.empty());

        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername("testUser"));
    }
}