package com.sith.api.repository;

import com.sith.api.entity.ClientApp;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientAppRepository extends JpaRepository<ClientApp, UUID> {
    boolean existsByApiKey(String apiKey);
    List<ClientApp> findAllByClientId(UUID clientId);
}
