package com.wheel.core;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.AntPathMatcher;

import static com.wheel.core.utils.Constants.OPEN_API_DESCRIPTION;
import static com.wheel.core.utils.Constants.OPEN_API_SERVER;

@OpenAPIDefinition(servers = {@Server(url = OPEN_API_SERVER, description = OPEN_API_DESCRIPTION)})
@SpringBootApplication
public class WheelApplication {

    public static void main(String[] args) {
        SpringApplication.run(WheelApplication.class, args);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public AntPathMatcher antPathMatcher() {
        return new AntPathMatcher();
    }
}
