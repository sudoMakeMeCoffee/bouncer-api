package com.sith.api.service.impl;

import com.sith.api.dto.request.CreateClientAppUserRequestDto;
import com.sith.api.dto.request.RegisterAppUserRequestDto;
import com.sith.api.dto.response.ClientAppUserResponseDto;
import com.sith.api.entity.ClientApp;
import com.sith.api.entity.ClientAppUser;
import com.sith.api.repository.ClientAppRepository;
import com.sith.api.repository.ClientAppUserRepository;
import com.sith.api.service.ClientAppUserService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientAppUserServiceImpl implements ClientAppUserService {

    private final ClientAppUserRepository clientAppUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ClientAppRepository clientAppRepository;

    public ClientAppUserServiceImpl(ClientAppUserRepository clientAppUserRepository, PasswordEncoder passwordEncoder, ClientAppRepository clientAppRepository) {
        this.clientAppUserRepository = clientAppUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.clientAppRepository = clientAppRepository;
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
}
