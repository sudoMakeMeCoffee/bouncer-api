package com.sith.api.service.impl;


import com.sith.api.dto.request.SignUpRequestDto;
import com.sith.api.dto.response.ClientResponseDto;
import com.sith.api.entity.Client;
import com.sith.api.repository.ClientRepository;
import com.sith.api.service.ClientAuthService;
import org.springframework.stereotype.Service;

@Service
public class ClientAuthServiceImpl implements ClientAuthService {
    private final ClientRepository clientRepository;

    public ClientAuthServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientResponseDto signUp(SignUpRequestDto requestDto) {
        Client client = Client.builder()
                .username(requestDto.getUsername())
                .email(requestDto.getEmail())
                .password(requestDto.getPassword())
                .role(requestDto.getRole())
                .build();
        Client savedClient = clientRepository.save(client);

        return ClientResponseDto.fromEntity(savedClient);
    }
}
