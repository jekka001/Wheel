package com.wheel.core.controller;

import com.wheel.core.exception.NotFoundException;
import com.wheel.core.exception.UserSpinResultException;
import com.wheel.core.exception.ValidateTokenException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class BaseController {
    protected final ExecutorService executorService = Executors.newFixedThreadPool(8);

    private final Logger logger = LoggerFactory.getLogger(BaseController.class);

    @ExceptionHandler({IllegalArgumentException.class})
    public ResponseEntity<String> handleRuntimeException(IllegalArgumentException e) {
        logger.error(e.getMessage(), this.getClass());

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ValidateTokenException.class})
    public ResponseEntity<String> handleRuntimeException(ValidateTokenException e) {
        logger.error(e.getMessage(), this.getClass());

        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({UserSpinResultException.class})
    public ResponseEntity<String> handleRuntimeException(UserSpinResultException e) {
        logger.error(e.getMessage(), this.getClass());

        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<String> handleRuntimeException(NotFoundException e) {
        logger.error(e.getMessage(), this.getClass());

        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler({RuntimeException.class})
    public ResponseEntity<String> handleRuntimeException(RuntimeException e) {
        logger.error(e.getMessage(), this.getClass());

        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
