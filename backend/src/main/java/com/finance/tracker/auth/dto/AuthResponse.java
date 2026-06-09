package com.finance.tracker.auth.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
public class AuthResponse {
    private UUID userId;
    private String email;
    private String name;
    private String accessToken;
    private String refreshToken;
    private long expiresIn;
}
