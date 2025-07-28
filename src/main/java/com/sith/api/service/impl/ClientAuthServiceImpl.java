package com.sith.api.service.impl;


import com.sith.api.dto.request.LoginRequestDto;
import com.sith.api.dto.request.SignUpRequestDto;
import com.sith.api.dto.response.ClientResponseDto;
import com.sith.api.dto.response.LoginResult;
import com.sith.api.entity.Client;
import com.sith.api.repository.ClientRepository;
import com.sith.api.service.ClientAuthService;
import com.sith.api.service.ClientDetailsService;
import com.sith.api.utils.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ClientAuthServiceImpl implements ClientAuthService {
    private final ClientRepository clientRepository;
    private final AuthenticationManager authenticationManager;
    private final ClientDetailsService clientDetailsService;
    private final JwtUtil jwtUtil;

    public ClientAuthServiceImpl(ClientRepository clientRepository, AuthenticationManager authenticationManager, ClientDetailsService clientDetailsService, JwtUtil jwtUtil) {
        this.clientRepository = clientRepository;
        this.authenticationManager = authenticationManager;
        this.clientDetailsService = clientDetailsService;
        this.jwtUtil = jwtUtil;
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

    @Override
    public LoginResult login(LoginRequestDto requestDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(requestDto.getEmail(), requestDto.getPassword()));

        UserDetails userDetails = clientDetailsService.loadUserByUsername(requestDto.getEmail());
        Client client = clientRepository.findByEmail(userDetails.getUsername()).orElseThrow(() -> new UsernameNotFoundException("Client not found."));
        String jwt = jwtUtil.generateToken(userDetails);

        return LoginResult.builder()
                .token(jwt)
                .clientResponseDto(ClientResponseDto.fromEntity(client))
                .build();

    }
}
