package com.sith.api.controller;

import com.sith.api.dto.request.LoginRequestDto;
import com.sith.api.dto.request.SignUpRequestDto;
import com.sith.api.dto.response.ApiResponse;
import com.sith.api.dto.response.ClientResponseDto;
import com.sith.api.dto.response.LoginResult;
import com.sith.api.entity.VerificationToken;
import com.sith.api.enums.VerificationType;
import com.sith.api.service.ClientAuthService;
import com.sith.api.service.ClientService;
import com.sith.api.service.VerificationTokenService;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1/auth/client")
public class ClientAuthController {

    private final ClientAuthService clientAuthService;
    private final PasswordEncoder passwordEncoder;
    private final VerificationTokenService verificationTokenService;
    private final ClientService clientService;

    public ClientAuthController(ClientAuthService clientAuthService, PasswordEncoder passwordEncoder, VerificationTokenService verificationTokenService, ClientService clientService) {
        this.clientAuthService = clientAuthService;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.clientService = clientService;
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
                "Login Successful.",
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

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", "")
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
                .header(HttpHeaders.SET_COOKIE, String.valueOf(refreshTokenCookie))
                .body(response);
    }

    @GetMapping("/verification")
    public ResponseEntity<Void> verification(@RequestParam("token") String token, @RequestParam("type") String type ){
        Optional<VerificationToken> optionalVerificationToken = verificationTokenService.findByToken(token);

        String baseUrl = null;

        if (VerificationType.EMAIL.name().equalsIgnoreCase(type)){
            baseUrl = "http://localhost:3000/verification/email";
        } else if (VerificationType.PASSWORD.name().equalsIgnoreCase(type)) {
            baseUrl = "http://localhost:3000/verification/password";
        }
        else {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create("http://localhost:3000/verification"))
                    .build();
        }

        if(optionalVerificationToken.isEmpty()){
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(baseUrl + "?expired=false&valid=false"))
                    .build();
        }

        VerificationToken verificationToken = optionalVerificationToken.get();

        if (verificationToken.getExpiredAt().isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.FOUND)
                    .location(URI.create(baseUrl + "?expired=true&valid=true"))
                    .build();
        }

        clientService.verification(verificationToken.getClient().getId());

        verificationTokenService.delete(verificationToken);

        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(baseUrl + "?expired=false&valid=true"))
                .build();


    }

}
