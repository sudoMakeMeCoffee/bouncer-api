package com.sith.api.repository;

import com.sith.api.entity.ClientApp;
import com.sith.api.entity.ClientAppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ClientAppUserRepository extends JpaRepository<ClientAppUser, UUID> {
    List<ClientAppUser> findByClientAppId(UUID clientAppId);
    Optional<ClientAppUser> findByEmailAndClientApp(String email, ClientApp clientApp);
    Optional<ClientAppUser> findByEmail(String email);
}
