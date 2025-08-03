package com.sith.api.controller;

import com.sith.api.dto.request.LoginRequestDto;
import com.sith.api.dto.request.SignUpRequestDto;
import com.sith.api.dto.response.ApiResponse;
import com.sith.api.dto.response.ClientResponseDto;
import com.sith.api.dto.response.AuthResult;
import com.sith.api.entity.Client;
import com.sith.api.entity.VerificationToken;
import com.sith.api.enums.VerificationType;
import com.sith.api.exception.UnauthorizedException;
import com.sith.api.service.*;
import com.sith.api.utils.JwtUtil;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
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
    private final ClientDetailsService clientDetailsService;
    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    public ClientAuthController(ClientAuthService clientAuthService, PasswordEncoder passwordEncoder, VerificationTokenService verificationTokenService, ClientService clientService, ClientDetailsService clientDetailsService, JwtUtil jwtUtil, RefreshTokenService refreshTokenService) {
        this.clientAuthService = clientAuthService;
        this.passwordEncoder = passwordEncoder;
        this.verificationTokenService = verificationTokenService;
        this.clientService = clientService;
        this.clientDetailsService = clientDetailsService;
        this.jwtUtil = jwtUtil;
        this.refreshTokenService = refreshTokenService;
    }


    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<ClientResponseDto>> refresh(HttpServletRequest request) {
        String refresh_token = null;

        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refresh_token".equals(cookie.getName())) {
                    refresh_token = cookie.getValue();
                    break;
                }
            }
        }

        if (refresh_token == null) {
            ApiResponse<ClientResponseDto> response = new ApiResponse<>(
                    false,
                    null,
                    null,
                    "Refresh token not found. Please log in again."
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        try {
            AuthResult authResult = refreshTokenService.generateNewAccessToken(refresh_token);

            ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", authResult.getAccessToken())
                    .httpOnly(true)
                    .secure(false) // TODO: make conditional on env
                    .path("/")
                    .maxAge(15 * 60) // 15 mins in seconds
                    .sameSite("Lax")
                    .build();

            ApiResponse<ClientResponseDto> response = new ApiResponse<>(
                    true,
                    "Token refreshed successfully",
                    authResult.getClientResponseDto(),
                    null
            );

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                    .body(response);

        } catch (Exception ex) {
            ApiResponse<ClientResponseDto> response = new ApiResponse<>(
                    false,
                    null,
                    null,
                    "Refresh failed. Please login again."
            );
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }
    }

    @PostMapping("/check-auth")
    public ResponseEntity<ApiResponse<Object>> checkAuth(HttpServletRequest request) {
        try {
            ClientResponseDto user = clientAuthService.checkAuth(request);

            return ResponseEntity.ok(
                    new ApiResponse<>(true, "Authorized", user, null));
        } catch (UnauthorizedException ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse<>(false, null, null, ex.getMessage()));
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<ClientResponseDto>> signUp(@Valid @RequestBody SignUpRequestDto requestDto){
        requestDto.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        ClientResponseDto clientResponseDto = clientAuthService.signUp(requestDto);

        ApiResponse<ClientResponseDto> response = new ApiResponse<>(
                true,
                "Client created successfully",
                clientResponseDto,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<ClientResponseDto>> login(@Valid @RequestBody LoginRequestDto requestDto){
        AuthResult authResult = clientAuthService.login(requestDto);

        ApiResponse<ClientResponseDto> response = new ApiResponse<>(
                true,
                "Login Successful.",
                authResult.getClientResponseDto(),
                null
        );

        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", authResult.getAccessToken())
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(20 * 60) // 15 minutes for access token
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", authResult.getRefreshToken())
                .httpOnly(true)
                .secure(false)
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
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {

        clientAuthService.logout(request);

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


        Client client = verificationToken.getClient();
        UserDetails userDetails = clientDetailsService.loadUserByUsername(client.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        String refreshToken = refreshTokenService.createRefreshToken(client.getId()).getToken();


        ResponseCookie accessTokenCookie = ResponseCookie.from("access_token", accessToken)
                .httpOnly(true)
                .secure(true) // set to true in production
                .path("/")
                .maxAge(20) // 20 minutes
                .sameSite("Lax")
                .build();

        ResponseCookie refreshTokenCookie = ResponseCookie.from("refresh_token", refreshToken)
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(7 * 24 * 60 * 60) // 7 days
                .sameSite("Lax")
                .build();

        return ResponseEntity.status(HttpStatus.FOUND)
                .header(HttpHeaders.SET_COOKIE, accessTokenCookie.toString())
                .header(HttpHeaders.SET_COOKIE, refreshTokenCookie.toString())
                .location(URI.create(baseUrl + "?expired=false&valid=true"))
                .build();
    }

    @PostMapping("/send-verification-email")
    public ResponseEntity<Object> verification(@RequestParam("email") String email){
        clientAuthService.sendVerificationLink(email, "Verify your email");
        ApiResponse<Object> response = new ApiResponse<>(
                true,
                "Email sent successfully",
                null,
                null
        );

        return new ResponseEntity<>(response, HttpStatus.OK);

    }
}
