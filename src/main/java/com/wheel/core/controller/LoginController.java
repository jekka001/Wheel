package com.wheel.core.controller;

import com.wheel.core.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import static com.wheel.core.utils.Constants.ALLOW_CREDENTIALS_TRUE;
import static com.wheel.core.utils.Constants.CROSS_ORIGIN_URL;
import static com.wheel.core.utils.UrlConstants.CREATE;
import static com.wheel.core.utils.UrlConstants.LOGIN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequiredArgsConstructor
@RequestMapping(LOGIN)
@CrossOrigin(origins = CROSS_ORIGIN_URL, allowCredentials = ALLOW_CREDENTIALS_TRUE)
public class LoginController extends BaseController {
    private final UserService userService;
    private final AuthenticationManager authenticationManager;


    @PostMapping(value = CREATE, produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<Boolean> create(@RequestParam(name="login") String login, @RequestParam(name="password") String password) {
        boolean result = userService.create(login, password);

        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> login(@RequestParam(name="login") String login, @RequestParam(name="password") String password) {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(login, password);
        authenticationManager.authenticate(authenticationToken);

        String token = userService.login(login);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

}
