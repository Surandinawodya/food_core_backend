package com.example.food_backend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.IOException;

import static org.mockito.Mockito.*;

class JwtFilterTest {

    private JwtFilter jwtFilter;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setup() {
        jwtFilter = new JwtFilter();
        request = mock(HttpServletRequest.class);
        response = mock(HttpServletResponse.class);
        filterChain = mock(FilterChain.class);
    }

    @Test
    void doFilterInternal_WithNoAuthHeaderAndApiUsers_ShouldCallFilterChain() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/users/signup");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain, times(1)).doFilter(request, response);
        verify(response, never()).sendError(anyInt(), anyString());
    }

    @Test
    void doFilterInternal_WithInvalidToken_ShouldSendUnauthorized() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Bearer invalidtoken");
        when(request.getRequestURI()).thenReturn("/api/other");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), anyString());
        verify(filterChain, never()).doFilter(request, response);
    }

    @Test
    void doFilterInternal_WithMissingTokenOnProtectedEndpoint_ShouldSendUnauthorized() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);
        when(request.getRequestURI()).thenReturn("/api/other");

        jwtFilter.doFilterInternal(request, response, filterChain);

        verify(response, times(1)).sendError(eq(HttpServletResponse.SC_UNAUTHORIZED), anyString());
        verify(filterChain, never()).doFilter(request, response);
    }
}
