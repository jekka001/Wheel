package com.wheel.core.impl;

import com.wheel.core.entity.dto.ReelDto;
import com.wheel.core.entity.dto.SpinResultDto;
import com.wheel.core.entity.dto.UserDto;
import com.wheel.core.entity.main.Line;
import com.wheel.core.exception.UserSpinResultException;
import com.wheel.core.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.UUID;

import static com.wheel.core.utils.Constants.PRECISION;
import static com.wheel.core.utils.Constants.TESTING_BALANCE;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class PayServiceImplTest {

    @Mock
    private UserService userService;

    @Mock
    private LineService lineService;

    @Mock
    private ReelService reelService;

    @Mock
    private SlotService slotService;

    @Mock
    private SpinService spinService;

    @InjectMocks
    private PayServiceImpl payService;
    private static final MathContext MATH_CONTEXT = new MathContext(PRECISION, RoundingMode.HALF_UP);


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetUserBalance() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDto userDto = new UserDto();

        when(userService.get(request)).thenReturn(userDto);

        BigDecimal balance = payService.getUserBalance(request);

        assertNotNull(balance);
        assertEquals(new BigDecimal(TESTING_BALANCE, MATH_CONTEXT), balance);
    }

    @Test
    void testGetPayFromUser() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDto userDto = new UserDto();

        when(userService.get(request)).thenReturn(userDto);

        boolean result = payService.getPayFromUser(request);

        assertTrue(result);
        // Additional assertions can be made to verify the test money deduction
    }

    @Test
    void testPayWinForLineSuccess() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDto userDto = new UserDto();
        userDto.setId(UUID.randomUUID());

        SpinResultDto spinResult = new SpinResultDto();
        spinResult.setId(UUID.randomUUID());
        spinResult.setUser(userDto);
        spinResult.setPayed(false);

        when(userService.get(request)).thenReturn(userDto);
        when(spinService.get(any(UUID.class))).thenReturn(spinResult);
        when(reelService.getReelsBySpin(any(UUID.class))).thenReturn(Collections.singletonList(new ReelDto()));
        when(lineService.getAll()).thenReturn(Collections.singletonList(new Line(UUID.randomUUID(), 1, "0,1,2")));

        boolean result = payService.payWinForLine(request, spinResult.getId().toString());

        assertTrue(result);
        verify(spinService, times(1)).savePayed(spinResult);
    }

    @Test
    void testPayWinForLineAlreadyPaid() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDto userDto = new UserDto();
        userDto.setId(UUID.randomUUID());

        SpinResultDto spinResult = new SpinResultDto();
        spinResult.setId(UUID.randomUUID());
        spinResult.setUser(userDto);
        spinResult.setPayed(true);

        when(userService.get(request)).thenReturn(userDto);
        when(spinService.get(any(UUID.class))).thenReturn(spinResult);

        assertThrows(UserSpinResultException.class, () -> payService.payWinForLine(request, spinResult.getId().toString()));
    }

    @Test
    void testPayWinForLineThrowsExceptionWhenUserMismatch() {
        HttpServletRequest request = mock(HttpServletRequest.class);
        UserDto userDto = new UserDto();
        userDto.setId(UUID.randomUUID());

        UserDto differentUserDto = new UserDto();
        differentUserDto.setId(UUID.randomUUID());

        SpinResultDto spinResult = new SpinResultDto();
        spinResult.setId(UUID.randomUUID());
        spinResult.setUser(differentUserDto);
        spinResult.setPayed(false);

        when(userService.get(request)).thenReturn(userDto);
        when(spinService.get(any(UUID.class))).thenReturn(spinResult);

        assertThrows(UserSpinResultException.class, () -> payService.payWinForLine(request, spinResult.getId().toString()));
    }
}
