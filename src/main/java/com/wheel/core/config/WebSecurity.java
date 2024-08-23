package com.wheel.core.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static com.wheel.core.utils.Constants.*;
import static com.wheel.core.utils.UrlConstants.LOGIN;
import static com.wheel.core.utils.UrlConstants.SPIN;

@Configuration
@AllArgsConstructor
public class WebSecurity {
    private UserDetailsService userDetailsServiceImpl;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    protected SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .cors().and()
                .authorizeHttpRequests()
                .requestMatchers(HttpMethod.POST, "/" + LOGIN + "/*").permitAll()
                .requestMatchers(HttpMethod.POST, "/" + SPIN + "/*").authenticated()
                .requestMatchers(HttpMethod.GET, "/" + SPIN + "/*").authenticated()
                .requestMatchers(HttpMethod.GET, "/" + SPIN + "/*/*").authenticated()
                .anyRequest().permitAll()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();

        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfiguration);

        return source;
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder) throws Exception {
        AuthenticationManagerBuilder builder = http.getSharedObject(AuthenticationManagerBuilder.class);

        builder
                .userDetailsService(userDetailsServiceImpl)
                .passwordEncoder(passwordEncoder);

        return builder.build();
    }

    @Bean
    public OpenAPI openAPI() {
        String securitySchemeName = SECURITY_SCHEME_NAME;
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(securitySchemeName);
        Components openAPIComponents = new Components().addSecuritySchemes(securitySchemeName, createAPIKeyScheme());

        return new OpenAPI()
                .addSecurityItem(securityRequirement)
                .components(openAPIComponents);
    }

    private SecurityScheme createAPIKeyScheme() {
        return new SecurityScheme()
                .type(SecurityScheme.Type.HTTP)
                .bearerFormat(BEARER_FORMAT)
                .scheme(SCHEME_NAME);
    }
}