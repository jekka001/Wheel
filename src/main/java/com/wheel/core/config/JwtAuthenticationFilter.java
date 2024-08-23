package com.wheel.core.config;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtUtils jwtUtils;
    private final AntPathMatcher pathMatcher;

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestURI = request.getRequestURI();

        return jwtUtils.getSkipUrls()
                .stream()
                .anyMatch(skipUrl -> pathMatcher.match(skipUrl, requestURI));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {
        checkAuthentication(request, response);

        filterChain.doFilter(request, response);
    }

    private void checkAuthentication(HttpServletRequest request, HttpServletResponse response) {
        try {
            checkToken(request);
        } catch (Exception e) {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        }
    }

    private void checkToken(HttpServletRequest request) {
        String accessToken = jwtUtils.resolveToken(request);
        Claims claims = jwtUtils.resolveClaims(accessToken);

        if (claims != null && jwtUtils.validateToken(accessToken)) {
            createAuthentication(claims);
        }
    }

    private void createAuthentication(Claims claims) {
        String login = claims.getSubject();

        Authentication authentication = new UsernamePasswordAuthenticationToken(login, "", new ArrayList<>());

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}