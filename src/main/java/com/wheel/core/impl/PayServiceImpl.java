package com.wheel.core.impl;

import com.wheel.core.entity.dto.ReelDto;
import com.wheel.core.entity.dto.SlotDto;
import com.wheel.core.entity.dto.SpinResultDto;
import com.wheel.core.entity.dto.UserDto;
import com.wheel.core.entity.main.Line;
import com.wheel.core.entity.main.WinHelper;
import com.wheel.core.exception.UserSpinResultException;
import com.wheel.core.service.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.wheel.core.utils.Constants.*;

@Service
@RequiredArgsConstructor
@Profile(TESTING_PAY_SERVICE)
public class PayServiceImpl implements PayService {
    private static final MathContext MATH_CONTEXT = new MathContext(PRECISION, RoundingMode.HALF_UP);
    private final Logger logger = LoggerFactory.getLogger(PayServiceImpl.class);
    private final UserService userService;
    private final LineService lineService;
    private final ReelService reelService;
    private final SlotService slotService;
    private final SpinService spinService;
    private BigDecimal testMoney = new BigDecimal(TESTING_BALANCE, MATH_CONTEXT);

    @Override
    public BigDecimal getUserBalance(HttpServletRequest request) {
        UserDto userDto = userService.get(request);

        return testMoney;
    }

    @Override
    public boolean getPayFromUser(HttpServletRequest request) {
        UserDto userDto = userService.get(request);

        testMoney = testMoney.subtract(new BigDecimal(5));
        return true;
    }

    @Override
    @Transactional
    public boolean payWinForLine(HttpServletRequest request, String spinResultId) {
        UserDto userDto = userService.get(request);

        UUID spinUUID = UUID.fromString(spinResultId);
        SpinResultDto spinResult = spinService.get(spinUUID);

        if (spinResult.isPayed()) {
            throw new UserSpinResultException(spinResultId);
        } else {
            if (spinResult.getUser().getId().equals(userDto.getId())) {
                pay(userDto, spinUUID);
                spinService.savePayed(spinResult);
                return true;
            } else {
                throw new UserSpinResultException(userDto.getId().toString(), spinUUID.toString());
            }
        }
    }

    private boolean pay(UserDto userDto, UUID spinUUID) {
        List<ReelDto> reels = reelService.getReelsBySpin(spinUUID);

        List<Line> allLines = lineService.getAll();
        List<Line> winLines = new ArrayList<>();

        allLines.stream().filter(line -> isWinLine(line, reels)).forEach(winLines::add);
        int prize = winLines.size() * 5;

        logger.info("Pay " + prize + "$ to user with login - " + userDto.getLogin());
        testMoney = testMoney.add(new BigDecimal(prize));

        return true;
    }

    private boolean isWinLine(Line line, List<ReelDto> reels) {
        List<Integer> winPositions = line.getWinPositions();
        WinHelper winHelper = new WinHelper(winPositions.size(), Integer.MIN_VALUE);

        for (ReelDto reel : reels) {
            int reelPosition = reel.getPositionOnWheel();
            List<SlotDto> slots = slotService.getAll(reel.getId());
            slots.forEach(slot -> checkSlot(winPositions, winHelper, reelPosition, slots.size(), slot));

            if (winHelper.isWin()) {
                return true;
            }
        }
        return false;
    }

    private void checkSlot(List<Integer> winPositions, WinHelper winHelper, int reelPosition, int slotSize, SlotDto slot) {
        int slotPosition = reelPosition * slotSize + slot.getPositionOnReel();

        if (winPositions.contains(slotPosition)) {
            if (winHelper.getWinValue() == Integer.MIN_VALUE) {
                winHelper.setWinValue(slot.getValue());
                winHelper.changeWinCounter();
            } else if (winHelper.getWinValue() == slot.getValue()) {
                winHelper.changeWinCounter();
            }
        }
    }
}
