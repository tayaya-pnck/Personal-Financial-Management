package com.finance.tracker.auth;

import com.finance.tracker.auth.dto.AuthResponse;
import com.finance.tracker.auth.dto.LoginRequest;
import com.finance.tracker.auth.dto.RefreshTokenRequest;
import com.finance.tracker.auth.dto.RegisterRequest;
import com.finance.tracker.user.User;
import com.finance.tracker.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Value("${app.jwt.expiration-ms}")
    private long jwtExpirationMs;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email already registered");
        }

        var user = new User(
            request.getEmail().toLowerCase().trim(),
            passwordEncoder.encode(request.getPassword()),
            request.getName().trim(),
            request.getBaseCurrency()
        );

        user = userRepository.save(user);

        var accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        var refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return buildResponse(user, accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().toLowerCase().trim(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BadCredentialsException("Invalid email or password");
        }

        var user = userRepository.findByEmail(request.getEmail().toLowerCase().trim())
            .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        var accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        var refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return buildResponse(user, accessToken, refreshToken);
    }

    public AuthResponse refresh(RefreshTokenRequest request) {
        if (!jwtService.isValid(request.getRefreshToken())) {
            throw new IllegalArgumentException("Invalid refresh token");
        }

        var email = jwtService.extractEmail(request.getRefreshToken());
        var user = userRepository.findByEmail(email)
            .orElseThrow(() -> new IllegalArgumentException("User not found"));

        var accessToken = jwtService.generateAccessToken(user.getId(), user.getEmail());
        var refreshToken = jwtService.generateRefreshToken(user.getId(), user.getEmail());

        return buildResponse(user, accessToken, refreshToken);
    }

    private AuthResponse buildResponse(User user, String accessToken, String refreshToken) {
        return AuthResponse.builder()
            .userId(user.getId())
            .email(user.getEmail())
            .name(user.getName())
            .accessToken(accessToken)
            .refreshToken(refreshToken)
            .expiresIn(jwtExpirationMs)
            .build();
    }
}
