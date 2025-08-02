package com.sith.api.service.impl;

import com.sith.api.entity.Client;
import com.sith.api.repository.ClientRepository;
import com.sith.api.service.ClientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class ClientServiceImpl implements ClientService {
    private final ClientRepository clientRepository;

    public ClientServiceImpl(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public void verification(UUID clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (client.isEmailVerified()) {
            throw new IllegalStateException("User already verified");
        }

        client.setEmailVerified(true);
        clientRepository.save(client);
    }
}
