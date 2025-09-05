package com.sith.api.service.impl;

import com.sith.api.dto.request.CreateClientAppUserRequestDto;
import com.sith.api.dto.request.LoginAppUserRequestDto;
import com.sith.api.dto.request.RegisterAppUserRequestDto;
import com.sith.api.dto.response.*;
import com.sith.api.entity.Client;
import com.sith.api.entity.ClientApp;
import com.sith.api.entity.ClientAppUser;
import com.sith.api.entity.ClientPrincipal;
import com.sith.api.exception.UnauthorizedException;
import com.sith.api.repository.ClientAppRepository;
import com.sith.api.repository.ClientAppUserRepository;
import com.sith.api.service.ClientAppUserService;
import com.sith.api.utils.JwtUtil;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientAppUserServiceImpl implements ClientAppUserService {

    private final ClientAppUserRepository clientAppUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientAppRepository clientAppRepository;
    private final JwtUtil jwtUtil;

    public ClientAppUserServiceImpl(ClientAppUserRepository clientAppUserRepository, PasswordEncoder passwordEncoder, ClientAppRepository clientAppRepository, JwtUtil jwtUtil) {
        this.clientAppUserRepository = clientAppUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientAppRepository = clientAppRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public ClientAppUserResponseDto registerAppUser(CreateClientAppUserRequestDto requestDto) {

        ClientApp clientApp = clientAppRepository.findById(requestDto.getClientAppId()).orElseThrow(() -> new EntityNotFoundException("Client App Not Found."));
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        ClientAppUser appUser = clientAppUserRepository.save(ClientAppUser.builder()
                .clientApp(clientApp)
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .build());

        return ClientAppUserResponseDto.fromEntity(appUser);

    }

    @Override
    public List<ClientAppUserResponseDto> getAllAppUsersByAppId(UUID appId) {
        List<ClientAppUser> appUsers = clientAppUserRepository.findByClientAppId(appId);

        return appUsers.stream().map(ClientAppUserResponseDto::fromEntity).toList();
    }

    @Override
    public ClientAppUserResponseDto register(RegisterAppUserRequestDto requestDto, String apiKey) {

        ClientApp clientApp = clientAppRepository.findByApiKey(apiKey).orElseThrow(() -> new EntityNotFoundException("Invalid Api Key"));
        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());
        ClientAppUser appUser = clientAppUserRepository.save(ClientAppUser.builder()
                .clientApp(clientApp)
                .email(requestDto.getEmail())
                .password(encodedPassword)
                .build());

        return ClientAppUserResponseDto.fromEntity(appUser);

    }

    @Override
    public AppUserAuthResult login(LoginAppUserRequestDto requestDto, String apiKey) {

        ClientApp clientApp = clientAppRepository.findByApiKey(apiKey)
                .orElseThrow(() -> new EntityNotFoundException("Invalid API Key"));

        ClientAppUser appUser = clientAppUserRepository
                .findByEmailAndClientApp(requestDto.getEmail(), clientApp)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!passwordEncoder.matches(requestDto.getPassword(), appUser.getPassword())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String accessToken = jwtUtil.generateToken(appUser.getEmail());

        return AppUserAuthResult.builder()
                .accessToken(accessToken)
                .clientAppUserResponseDto(ClientAppUserResponseDto.fromEntity(appUser))
                .build();
    }

    @Override
    public ClientAppUserResponseDto authenticated(HttpServletRequest request) {
        String jwt = jwtUtil.extractAppUserTokenFromCookie(request);

        if(jwt == null){
            throw new UnauthorizedException("Can not find access token");
        }

        String email = jwtUtil.extractEmail(jwt);

        ClientAppUser appUser = clientAppUserRepository.findByEmail(email).orElseThrow(() -> new UnauthorizedException("User not found"));

        if (jwt == null || !jwtUtil.validateToken(jwt, appUser.getEmail())){
            throw new UnauthorizedException("Invalid or missing token");
        }

        return ClientAppUserResponseDto.fromEntity(appUser);
    }


}
