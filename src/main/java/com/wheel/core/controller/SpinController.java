package com.wheel.core.controller;

import com.wheel.core.entity.dto.SpinResultDto;
import com.wheel.core.service.PayService;
import com.wheel.core.service.SpinService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static com.wheel.core.utils.Constants.ALLOW_CREDENTIALS_TRUE;
import static com.wheel.core.utils.Constants.CROSS_ORIGIN_URL;
import static com.wheel.core.utils.UrlConstants.*;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(SPIN)
@CrossOrigin(origins = CROSS_ORIGIN_URL, allowCredentials = ALLOW_CREDENTIALS_TRUE)
public class SpinController extends BaseController {
    private final SpinService spinService;
    private final PayService payService;


    @PostMapping(value = START, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SpinResultDto> start(HttpServletRequest request,
                                               @RequestParam(name = "countOfReels") int countOfReels,
                                               @RequestParam(name = "countOfSlots") int countOfSlots) {
        try {
            return asyncSpin(request, countOfReels, countOfSlots);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<SpinResultDto> asyncSpin(HttpServletRequest request, int countOfReels, int countOfSlots) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
            boolean result = payService.getPayFromUser(request);
            SpinResultDto spinResultDto = spinService.calculateSpin(request, countOfReels, countOfSlots);

            return new ResponseEntity<>(spinResultDto, HttpStatus.OK);
        }, executorService).get();
    }

    @GetMapping(value = CHECK, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<SpinResultDto> check(HttpServletRequest request, @PathVariable(name = "spinId") String spinId) {
        try {
            return asyncCheckSpin(request, spinId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<SpinResultDto> asyncCheckSpin(HttpServletRequest request, String spinId) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
            SpinResultDto spinResultDto = spinService.checkSpinResult(request, spinId);

            return new ResponseEntity<>(spinResultDto, HttpStatus.OK);
        }, executorService).get();
    }

    @GetMapping(value = BALANCE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<BigDecimal> check(HttpServletRequest request) {
        try {
            return asyncCheckBalance(request);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<BigDecimal> asyncCheckBalance(HttpServletRequest request) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
            BigDecimal balance = payService.getUserBalance(request);

            return new ResponseEntity<>(balance, HttpStatus.OK);
        }, executorService).get();
    }

    @PostMapping(value = FINISH, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> finish(HttpServletRequest request, @RequestParam(name = "spinId") String spinId) {
        try {
            return asyncFinishSpin(request, spinId);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResponseEntity<Boolean> asyncFinishSpin(HttpServletRequest request, String spinId) throws InterruptedException, ExecutionException {
        return CompletableFuture.supplyAsync(() -> {
            boolean result = payService.payWinForLine(request, spinId);

            return new ResponseEntity<>(result, HttpStatus.OK);
        }, executorService).get();
    }
}
