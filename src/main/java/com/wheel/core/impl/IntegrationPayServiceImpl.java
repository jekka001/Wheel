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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.wheel.core.utils.Constants.INTEGRATION_PAY_SERVICE;

@Service
@Profile(INTEGRATION_PAY_SERVICE)
@RequiredArgsConstructor
public class IntegrationPayServiceImpl implements PayService {
    private final UserService userService;
    private final SpinService spinService;
    private final LineService lineService;
    private final ReelService reelService;
    private final SlotService slotService;

    @Value("${client.system.url}")
    private String clientSystemUrl;

    @Override
    public BigDecimal getUserBalance(HttpServletRequest request) {
        UserDto userDto = userService.get(request);

        RestClient restClient = RestClient.create();

        return restClient.get()
                .uri(clientSystemUrl + "balance/" + userDto.getId())
                .retrieve()
                .body(BigDecimal.class);
    }

    @Override
    public boolean getPayFromUser(HttpServletRequest request) {
        UserDto userDto = userService.get(request);

        RestClient restClient = RestClient.create();

        ResponseEntity<Void> response = restClient.put()
                .uri(clientSystemUrl + "withdraw/" + userDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(new BigDecimal(5))
                .retrieve()
                .toBodilessEntity();

        return response.getStatusCode() == HttpStatus.OK;
    }

    @Override
    public boolean payWinForLine(HttpServletRequest request, String spinResultId) {
        UserDto userDto = userService.get(request);

        UUID spinUUID = UUID.fromString(spinResultId);
        SpinResultDto spinResult = spinService.get(spinUUID);

        if (spinResult.isPayed()) {
            throw new UserSpinResultException(spinResultId);
        } else {
            if (spinResult.getUser().getId().equals(userDto.getId())) {
                BigDecimal prize = pay(spinUUID);
                spinService.savePayed(spinResult);
                return topUpBalance(userDto, prize);
            } else {
                throw new UserSpinResultException(userDto.getId().toString(), spinUUID.toString());
            }
        }

    }

    private BigDecimal pay(UUID spinUUID) {
        List<ReelDto> reels = reelService.getReelsBySpin(spinUUID);

        List<Line> allLines = lineService.getAll();
        List<Line> winLines = new ArrayList<>();

        allLines.stream().filter(line -> isWinLine(line, reels)).forEach(winLines::add);
        int prize = winLines.size() * 5;

        return new BigDecimal(prize);
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

    private boolean topUpBalance(UserDto userDto, BigDecimal prize) {
        RestClient restClient = RestClient.create();

        ResponseEntity<Void> response = restClient.put()
                .uri(clientSystemUrl + "topUp/" + userDto.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .body(prize)
                .retrieve()
                .toBodilessEntity();

        return response.getStatusCode() == HttpStatus.OK;
    }
}
