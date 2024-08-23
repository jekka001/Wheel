package com.wheel.core.impl;

import com.wheel.core.entity.dto.ReelDto;
import com.wheel.core.entity.dto.SpinResultDto;
import com.wheel.core.entity.dto.UserDto;
import com.wheel.core.entity.main.SpinResult;
import com.wheel.core.entity.main.User;
import com.wheel.core.exception.UserSpinResultException;
import com.wheel.core.mapper.ReelMapper;
import com.wheel.core.mapper.SpinMapper;
import com.wheel.core.mapper.UserMapper;
import com.wheel.core.repository.SpinRepository;
import com.wheel.core.service.ReelService;
import com.wheel.core.service.SlotService;
import com.wheel.core.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.Collections;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

class SpinServiceImplTest {

    @Mock
    private SpinRepository repository;

    @Mock
    private SpinMapper mapper;

    @Mock
    private UserService userService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private ReelService reelService;

    @Mock
    private ReelMapper reelMapper;

    @Mock
    private SlotService slotService;

    @InjectMocks
    private SpinServiceImpl spinService;

    private SpinResult spinResult;
    private SpinResultDto spinResultDto;
    private UserDto userDto;
    private ReelDto reelDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        spinResult = new SpinResult();
        spinResult.setId(UUID.randomUUID());
        spinResult.setSpinDate(Timestamp.from(Instant.now()));

        userDto = new UserDto();
        userDto.setId(UUID.randomUUID());
        spinResult.setUser(userMapper.toEntity(userDto));

        reelDto = new ReelDto();

        spinResultDto = new SpinResultDto();
        spinResultDto.setId(spinResult.getId());
        spinResultDto.setReels(Collections.singletonList(reelDto));
        spinResultDto.setUser(userDto);
    }

    @Test
    @Transactional
    void testGet() {
        when(repository.getReferenceById(any(UUID.class))).thenReturn(spinResult);
        when(mapper.toDto(spinResult)).thenReturn(spinResultDto);

        SpinResultDto result = spinService.get(spinResult.getId());

        assertEquals(spinResultDto, result);
    }

    @Test
    @Transactional
    void testCalculateSpin() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        when(userService.get(request)).thenReturn(userDto);
        when(repository.save(any(SpinResult.class))).thenReturn(spinResult);
        when(reelService.generateRealReels(anyInt(), anyInt(), any(SpinResult.class)))
                .thenReturn(Collections.singletonList(reelDto));
        when(reelService.getFakeReels(anyInt())).thenReturn(Collections.emptyList());
        when(userMapper.toDto(spinResult.getUser())).thenReturn(userDto);

        SpinResultDto result = spinService.calculateSpin(request, 3, 5);

        assertNotNull(result);
        assertEquals(spinResult.getId(), result.getId());
        assertEquals(userDto, result.getUser());
    }

    @Test
    @Transactional
    void testCheckSpinResult() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String spinResultId = spinResult.getId().toString();

        User mockUser = new User();
        mockUser.setId(UUID.randomUUID());
        userDto.setId(mockUser.getId());

        SpinResult mockSpinResult = new SpinResult();
        mockSpinResult.setId(UUID.randomUUID());
        mockSpinResult.setUser(mockUser);


        when(userService.get(request)).thenReturn(userDto);
        when(repository.getReferenceById(any(UUID.class))).thenReturn(mockSpinResult);
        when(reelMapper.toListDto(anyList())).thenReturn(Collections.singletonList(reelDto));
        when(slotService.getAll(any(UUID.class))).thenReturn(Collections.emptyList());


        SpinResultDto result = spinService.checkSpinResult(request, spinResultId);


        assertNotNull(result);
        assertEquals(mockSpinResult.getId(), result.getId());
    }

    @Test
    @Transactional
    void testCheckSpinResultThrowsException() {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        String spinResultId = UUID.randomUUID().toString();
        UUID userId = UUID.randomUUID();

        when(userService.get(request)).thenReturn(userDto);
        when(repository.getReferenceById(any(UUID.class))).thenThrow(new UserSpinResultException());

        try {
            spinService.checkSpinResult(request, spinResultId);
            fail("Expected UserSpinResultException to be thrown");
        } catch (UserSpinResultException e) {
            assertTrue(e instanceof UserSpinResultException);
        }
    }

    @Test
    @Transactional
    void testSavePayed() {
        SpinResultDto dto = new SpinResultDto();
        dto.setId(spinResult.getId());
        spinResult.setPayed(false);

        when(repository.getReferenceById(dto.getId())).thenReturn(spinResult);

        spinService.savePayed(dto);

        assertTrue(spinResult.isPayed());
    }
}