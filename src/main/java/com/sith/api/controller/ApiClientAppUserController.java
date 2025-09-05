package com.sith.api.controller;

import com.sith.api.dto.request.LoginAppUserRequestDto;
import com.sith.api.dto.request.LoginRequestDto;
import com.sith.api.dto.request.RegisterAppUserRequestDto;
import com.sith.api.dto.response.*;
import com.sith.api.entity.ClientApp;
import com.sith.api.exception.UnauthorizedException;
import com.sith.api.service.ClientAppUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
public class ApiClientAppUserController {


    private final ClientAppUserService clientAppUserService;

    public ApiClientAppUserController(ClientAppUserService clientAppUserService) {
        this.clientAppUserService = clientAppUserService;
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<ClientAppUserResponseDto>> register(
            @RequestHeader("x-api-key") String apiKey,
            @Valid @RequestBody RegisterAppUserRequestDto requestDto
    ){

        ClientAppUserResponseDto appUserResponseDto = clientAppUserService.register(requestDto, apiKey);

        ApiResponse<ClientAppUserResponseDto> response = new ApiResponse<>(
                true,
                "User registered successfully.",
                appUserResponseDto,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);

    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ClientAppUserResponseDto>> login(
            @RequestHeader("x-api-key") String apiKey,
            @Valid @RequestBody LoginAppUserRequestDto requestDto) {

        AppUserAuthResult authResult = clientAppUserService.login(requestDto, apiKey);

        ApiResponse<ClientAppUserResponseDto> response = new ApiResponse<>(
                true,
                "Login successful.",
                authResult.getClientAppUserResponseDto(),
                null
        );

        ResponseCookie accessTokenCookie = ResponseCookie.from("_access_token", authResult.getAccessToken())
                .httpOnly(true)
                .secure(false)   // set true in production
                .path("/")
                .maxAge(60 * 60) // 1 hour
                .sameSite("Lax")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {

        ResponseCookie accessTokenCookie = ResponseCookie.from("_access_token", "")
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
                .header(HttpHeaders.SET_COOKIE, String.valueOf(accessTokenCookie))
                .body(response);
    }

    @PostMapping("/authenticated")
    public ResponseEntity<ApiResponse<Object>> authenticated(HttpServletRequest request) {
        try {
            ClientAppUserResponseDto user = clientAppUserService.authenticated(request);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Authorized", user, null));
        } catch (UnauthorizedException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, "Unauthorized", null, ex.getMessage()));
        }
    }


}
