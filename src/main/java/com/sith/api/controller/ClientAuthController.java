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

        ResponseCookie cookie =ResponseCookie.from("jwt", loginResult.getToken())
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


    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ClientResponseDto>> login(@Valid @RequestBody LoginRequestDto requestDto){
        LoginResult loginResult = clientAuthService.login(requestDto);

        ApiResponse<ClientResponseDto> response = new ApiResponse<>(
                true,
                "Login Successfull.",
                loginResult.getClientResponseDto(),
                null
        );

        ResponseCookie cookie =ResponseCookie.from("jwt", loginResult.getToken())
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
