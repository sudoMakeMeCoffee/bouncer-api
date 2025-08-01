package com.sith.api.controller;

import com.sith.api.dto.request.LoginRequestDto;
import com.sith.api.dto.request.SignUpRequestDto;
import com.sith.api.dto.response.ApiResponse;
import com.sith.api.dto.response.ClientResponseDto;
import com.sith.api.dto.response.LoginResult;
import com.sith.api.service.ClientAuthService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth/client")
public class ClientAuthController {

    private final ClientAuthService clientAuthService;
    private final PasswordEncoder passwordEncoder;

    public ClientAuthController(ClientAuthService clientAuthService, PasswordEncoder passwordEncoder) {
        this.clientAuthService = clientAuthService;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<ClientResponseDto>> signUp(@Valid @RequestBody SignUpRequestDto requestDto){
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        LoginResult loginResult = clientAuthService.signUp(requestDto);

        ApiResponse<ClientResponseDto> response = new ApiResponse<>(
                true,
                "Client created successfully",
                loginResult.getClientResponseDto(),
                null
        );

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", loginResult.getAccessToken())
                .httpOnly(true)
                .secure(false) // change to true in production
                .path("/")
                .maxAge(15 * 60) // 15 minutes for access token
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", loginResult.getRefreshToken())
                .httpOnly(true)
                .secure(false) // change to true in production
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days for refresh token
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString()) // <-- add second cookie
                .body(response);
    }


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ClientResponseDto>> login(@Valid @RequestBody LoginRequestDto requestDto){
        LoginResult loginResult = clientAuthService.login(requestDto);

        ApiResponse<ClientResponseDto> response = new ApiResponse<>(
                true,
                "Login Successfull.",
                loginResult.getClientResponseDto(),
                null
        );

        ResponseCookie cookie =ResponseCookie.from("access_token", loginResult.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(24 * 60 * 60)
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        ResponseCookie cookie = ResponseCookie.from("jwt", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();

        ApiResponse<Void> response = new ApiResponse<>(
                true,
                "Logged out successfully",
                null,
                null
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, String.valueOf(cookie))
                .body(response);
    }

}
