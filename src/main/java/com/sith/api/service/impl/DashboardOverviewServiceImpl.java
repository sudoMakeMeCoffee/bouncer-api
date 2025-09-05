package com.sith.api.service.impl;

import com.sith.api.dto.response.ClientAppResponseDto;
import com.sith.api.dto.response.DashboardOverviewResponseDto;
import com.sith.api.entity.Client;
import com.sith.api.entity.ClientApp;
import com.sith.api.repository.ClientAppRepository;
import com.sith.api.service.AuthenticatedClientService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DashboardOverviewServiceImpl implements DashboardOverviewService{

    private final ClientAppRepository clientAppRepository;
    private final AuthenticatedClientService authenticatedClientService;

    public DashboardOverviewServiceImpl(ClientAppRepository clientAppRepository, AuthenticatedClientService authenticatedClientService) {
        this.clientAppRepository = clientAppRepository;
        this.authenticatedClientService = authenticatedClientService;
    }

    @Override
    public DashboardOverviewResponseDto getDashboardOverviewData() {
        Client currentClient = authenticatedClientService.getAuthenticatedClient();
        long totalCount = clientAppRepository.countByClientId(currentClient.getId());

        List<ClientApp> recentApps = clientAppRepository
                .findAllByClientIdOrderByCreatedAtDesc(currentClient.getId(), PageRequest.of(0, 3));

       return DashboardOverviewResponseDto.builder()
               .appsCount(totalCount)
               .clientApps(recentApps.stream().map(ClientAppResponseDto::fromEntity).toList())
               .build();
    }


}
