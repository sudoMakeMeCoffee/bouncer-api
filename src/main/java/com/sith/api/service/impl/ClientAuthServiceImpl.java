package com.sith.api.service.impl;


import com.sith.api.dto.request.LoginRequestDto;
import com.sith.api.dto.request.SignUpRequestDto;
import com.sith.api.dto.response.ClientResponseDto;
import com.sith.api.dto.response.LoginResult;
import com.sith.api.dto.response.RefreshTokenResponseDto;
import com.sith.api.entity.Client;
import com.sith.api.entity.VerificationToken;
import com.sith.api.enums.VerificationType;
import com.sith.api.repository.ClientRepository;
import com.sith.api.service.*;
import com.sith.api.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
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

    public ClientAuthServiceImpl(ClientRepository clientRepository, AuthenticationManager authenticationManager, ClientDetailsService clientDetailsService, RefreshTokenService refreshTokenService, VerificationTokenService verificationTokenService, EmailService emailService, JwtUtil jwtUtil) {
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
                .password(requestDto.getPassword())
                .role(requestDto.getRole())
                .build();
        Client savedClient = clientRepository.save(newClient);

        UserDetails userDetails = clientDetailsService.loadUserByUsername(requestDto.getEmail());
        String accessToken = jwtUtil.generateToken(userDetails);
        RefreshTokenResponseDto refreshTokenResponseDto = refreshTokenService.createRefreshToken(savedClient.getId());

        sendVerificationLink(savedClient.getEmail(), "Verify your email.");

        return ClientResponseDto.fromEntity(savedClient);
    }

    @Override
    public LoginResult login(LoginRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword())
        );

        UserDetails userDetails = clientDetailsService.loadUserByUsername(requestDto.getEmail());
        Client client = clientRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Client not found."));
        String accessToken = jwtUtil.generateToken(userDetails);
        RefreshTokenResponseDto refreshTokenResponseDto = refreshTokenService.createRefreshToken(client.getId());


        return LoginResult.builder()
                .accessToken(accessToken)
                .refreshToken(refreshTokenResponseDto.getToken())
                .clientResponseDto(ClientResponseDto.fromEntity(client))
                .build();

    }

    @Override
    public void sendVerificationLink(String to, String subject){
        Client client = clientRepository.findByEmail(to).orElseThrow(() -> new EntityNotFoundException("Email not found"));
        List<VerificationToken> verificationTokens = verificationTokenService.findAllByClient(client);
        verificationTokens.forEach(verificationTokenService::delete);

        VerificationToken verificationToken = VerificationToken.builder()
                .client(client)
                .token(UUID.randomUUID().toString())
                .type(VerificationType.EMAIL)
                .expiredAt(LocalDateTime.now().plusMinutes(15))
                .build();

        VerificationToken createdVerificationToken = verificationTokenService.createVerificationToken(verificationToken);

        emailService.sendEmail(to, subject, String.format("http://localhost:8080/api/v1/auth/client/verification?token=%s&type=email", verificationToken.getToken()));
    }
}
