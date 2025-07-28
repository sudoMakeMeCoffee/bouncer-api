package com.sith.api.service;

import com.sith.api.entity.Client;
import com.sith.api.repository.ClientRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class AuthenticatedClientService {

    private final ClientRepository clientRepository;

    public AuthenticatedClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client getAuthenticatedClient(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        return clientRepository.findByEmail(email).orElseThrow(
                () -> new UsernameNotFoundException("Client not found.")
        );

    }
}
