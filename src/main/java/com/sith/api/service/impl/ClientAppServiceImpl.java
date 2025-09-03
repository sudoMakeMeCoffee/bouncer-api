package com.sith.api.service.impl;

import com.sith.api.dto.request.CreateClientAppRequestDto;
import com.sith.api.dto.response.ClientAppResponseDto;
import com.sith.api.entity.Client;
import com.sith.api.entity.ClientApp;
import com.sith.api.repository.ClientAppRepository;
import com.sith.api.service.AuthenticatedClientService;
import com.sith.api.service.ClientAppService;
import com.sith.api.utils.ApiKeyUtil;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ClientAppServiceImpl implements ClientAppService {

    private final ClientAppRepository clientAppRepository;
    private final AuthenticatedClientService authenticatedClientService;
    private final ApiKeyUtil apiKeyUtil;

    public ClientAppServiceImpl(ClientAppRepository clientAppRepository, AuthenticatedClientService authenticatedClientService, ApiKeyUtil apiKeyUtil) {
        this.clientAppRepository = clientAppRepository;
        this.authenticatedClientService = authenticatedClientService;
        this.apiKeyUtil = apiKeyUtil;
    }

    @Override
    public String createClientApp(CreateClientAppRequestDto requestDto) {

        Client currentClient = authenticatedClientService.getAuthenticatedClient();

        String apiKey;

        do {
            apiKey = apiKeyUtil.generateRawApiKey();
        }while (clientAppRepository.existsByApiKey(apiKeyUtil.hashApiKey(apiKey)));


        String hashed = apiKeyUtil.hashApiKey(apiKey);

        ClientApp app = ClientApp.builder()
                .client(currentClient)
                .name(requestDto.getName())
                .apiKey(hashed)
                .build();

        ClientApp createdApp = clientAppRepository.save(app);

        createdApp.setApiKey(apiKey);

        return apiKey;
    }

    @Override
    public List<ClientAppResponseDto> getAllAppsByClientId() {
        Client currentClient = authenticatedClientService.getAuthenticatedClient();
        List<ClientApp> apps = clientAppRepository.findAllByClientId(currentClient.getId());

        return  apps.stream().map(ClientAppResponseDto::fromEntity).toList();
    }

    @Override
    public ClientAppResponseDto getAppById(UUID appId) {
        ClientApp clientApp = clientAppRepository.findById(appId).orElseThrow(() -> new EntityNotFoundException("Client App not found."));

        return ClientAppResponseDto.fromEntity(clientApp);
    }


}
