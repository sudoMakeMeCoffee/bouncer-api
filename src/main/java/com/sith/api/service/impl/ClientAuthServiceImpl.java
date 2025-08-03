package com.sith.api.service.impl;

import com.sith.api.dto.request.LoginRequestDto;
import com.sith.api.dto.request.SignUpRequestDto;
import com.sith.api.dto.response.ClientResponseDto;
import com.sith.api.dto.response.LoginResult;
import com.sith.api.dto.response.RefreshTokenResponseDto;
import com.sith.api.entity.Client;
import com.sith.api.entity.ClientPrincipal;
import com.sith.api.entity.VerificationToken;
import com.sith.api.enums.Role;
import com.sith.api.enums.VerificationType;
import com.sith.api.exception.UnauthorizedException;
import com.sith.api.repository.ClientRepository;
import com.sith.api.service.*;
import com.sith.api.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ClientAuthServiceImpl implements ClientAuthService {

    private final ClientRepository clientRepository;
    private final AuthenticationManager authenticationManager;
    private final ClientDetailsService clientDetailsService;
    private final RefreshTokenService refreshTokenService;
    private final VerificationTokenService verificationTokenService;
    private final EmailService emailService;
    private final JwtUtil jwtUtil;

    public ClientAuthServiceImpl(
            ClientRepository clientRepository,
            AuthenticationManager authenticationManager,
            ClientDetailsService clientDetailsService,
            RefreshTokenService refreshTokenService,
            VerificationTokenService verificationTokenService,
            EmailService emailService,
            JwtUtil jwtUtil) {
        this.clientRepository = clientRepository;
        this.authenticationManager = authenticationManager;
        this.clientDetailsService = clientDetailsService;
        this.refreshTokenService = refreshTokenService;
        this.verificationTokenService = verificationTokenService;
        this.emailService = emailService;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ClientResponseDto signUp(SignUpRequestDto requestDto) {
        Client newClient = Client.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(requestDto.getPassword()) // ⚠️ Make sure it's encrypted!
                .role(Role.CLIENT)
                .build();

        Client savedClient = clientRepository.save(newClient);

        sendVerificationLink(savedClient.getEmail(), "Verify your email");

        return ClientResponseDto.fromEntity(savedClient);
    }

    @Override
    public LoginResult login(LoginRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword())
        );

        UserDetails userDetails = clientDetailsService.loadUserByUsername(requestDto.getEmail());
        Client client = clientRepository.findByEmail(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Client not found"));

        String accessToken = jwtUtil.generateToken(userDetails);
        RefreshTokenResponseDto refreshTokenResponseDto = refreshTokenService.createRefreshToken(client.getId());

        return LoginResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenResponseDto.getToken())
                .clientResponseDto(ClientResponseDto.fromEntity(client))
                .build();
    }

    @Override
    public void sendVerificationLink(String to, String subject) {
        Client client = clientRepository.findByEmail(to)
                .orElseThrow(() -> new EntityNotFoundException("Email not found"));

        enforceResendCooldown(client);

        deletePreviousTokens(client);

        VerificationToken token = createVerificationToken(client);

        String baseUrl = "http://localhost:8080";
        String verificationLink = String.format(
                "%s/api/v1/auth/client/verification?token=%s&type=email",
                baseUrl,
                token.getToken()
        );

        emailService.sendEmail(to, subject, verificationLink);
    }

    @Override
    public ClientResponseDto checkAuth(HttpServletRequest request) {
        String jwt = jwtUtil.extractJwtFromCookie(request);
        String email = jwtUtil.extractEmail(jwt);

        Client client = clientRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found"));

        if (jwt == null || !jwtUtil.validateToken(jwt, new ClientPrincipal(client))){
            throw new UnauthorizedException("Invalid or missing token");
        }

        return ClientResponseDto.fromEntity(client);
    }


    // Helper Methods

    private void enforceResendCooldown(Client client) {
        List<VerificationToken> tokens = verificationTokenService.findAllByClient(client);
        for (VerificationToken token : tokens) {
            if (token.getCreatedAt() != null &&
                    token.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(1))) {
                throw new RuntimeException("Please wait before resending verification email.");
            }
        }
    }

    private void deletePreviousTokens(Client client) {
        verificationTokenService.findAllByClient(client)
                .forEach(verificationTokenService::delete);
    }

    private VerificationToken createVerificationToken(Client client) {
        VerificationToken token = VerificationToken.builder()
                .client(client)
                .token(UUID.randomUUID().toString())
                .type(VerificationType.EMAIL)
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();

        return verificationTokenService.createVerificationToken(token);
    }
}
